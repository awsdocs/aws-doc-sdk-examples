# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
from datetime import datetime
import time
import logging

from date_utilities import DateUtilities


class DateOutOfBoundsError(Exception):
    """Exception raised when the date range for a query is out of bounds."""

    pass


class CloudWatchQuery:
    """
    A class to query AWS CloudWatch logs within a specified date range.

    :ivar client: The Cloudwatch Logs Client.
    :vartype client: Client
    :ivar log_groups: Names of the log groups to query.
    :vartype log_groups: list
    :ivar date_range: Start and end datetime for the query.
    :vartype date_range: tuple
    :ivar limit: Maximum number of log entries to return.
    :vartype limit: int
    """

    def __init__(self, client, log_groups, date_range, limit=10000):
        self.cloudwatch_logs = client
        self.log_groups = log_groups
        self.date_range = date_range
        self.limit = limit
        self.query_results = []
        self.query_duration = None
        self.datetime_format = "%Y-%m-%d %H:%M:%S.%f"
        self.log_batch = None
        self.date_utilities = DateUtilities()

    def query_logs(self):
        """
        Executes a CloudWatch logs query for a specified date range and calculates the execution time of the query.

        :return: A batch of logs retrieved from the CloudWatch logs query.
        :rtype: list
        """
        start_time = datetime.now()

        start_date, end_date = self.date_utilities.normalize_date_range_format(
            self.date_range, from_format="unix_timestamp", to_format="datetime"
        )

        logging.info(
            f"Original query:"
            f"\n       START:    {start_date}"
            f"\n       END:      {end_date}"
        )
        logs_batch = self.process_logs((start_date, end_date))
        end_time = datetime.now()
        self.query_duration = (end_time - start_time).total_seconds()
        return logs_batch

    def process_logs(self, date_range):
        # Fetch a batch of logs
        batch_of_logs = self._perform_query(date_range, self.limit)
        # Add the batch to the accumulated logs
        self.query_results.extend(batch_of_logs)
        logging.info(f"Recursive log count: {len(self.query_results)}")

        # If the batch size is exactly 10,000, assume there might be more logs and fetch again
        if len(batch_of_logs) == self.limit:
            logging.info(f"Fetched {self.limit}, checking for more...")
            most_recent_log = self._find_most_recent_log(batch_of_logs)
            most_recent_log_timestamp = next(
                item["value"]
                for item in most_recent_log
                if item["field"] == "@timestamp"
            )
            new_range = (most_recent_log_timestamp, date_range[1])
            return self.process_logs(new_range)
        else:
            # If the batch size is less than 10,000, assume this is the last batch and return the accumulated logs
            logging.info(f"Fetched final batch of {len(batch_of_logs)} logs.")

    def _find_most_recent_log(self, logs):
        """
        Search a list of log items and return most recent log entry.
        :param logs:
        :return:
        """
        most_recent_log = None
        most_recent_date = "1970-01-01 00:00:00.000"

        for log in logs:
            for item in log:
                if item["field"] == "@timestamp":
                    logging.debug(f"Compared: {item['value']} to {most_recent_date}")
                    if (
                        self.date_utilities.compare_dates(
                            item["value"], most_recent_date
                        )
                        == item["value"]
                    ):
                        logging.debug(f"New most recent: {item['value']}")
                        most_recent_date = item["value"]
                        most_recent_log = log
        logging.info(f"Most recent log date of batch: {most_recent_date}")
        return most_recent_log

    def _perform_query(self, date_range, max_logs):
        """
        Performs the actual CloudWatch log query.

        :param date_range: A tuple representing the start and end datetime for the query.
        :type date_range: tuple
        :param max_logs: The maximum number of logs to retrieve.
        :type max_logs: int
        :return: A list containing the query results.
        :rtype: list
        """
        try:
            query_id = self._initiate_query(date_range, max_logs)
            return self._wait_for_query_results(query_id)
        except DateOutOfBoundsError:
            return []

    def _initiate_query(self, date_range, max_logs):
        """
        Initiates the CloudWatch logs query.

        :param date_range: A tuple representing the start and end datetime for the query.
        :type date_range: tuple
        :param max_logs: The maximum number of logs to retrieve.
        :type max_logs: int
        :return: The query ID as a string.
        :rtype: str
        """
        try:
            start_time = round(
                self.date_utilities.convert_iso1806_to_unix_timestamp(date_range[0])
            )
            end_time = round(
                self.date_utilities.convert_iso1806_to_unix_timestamp(date_range[1])
            )
            response = self.cloudwatch_logs.start_query(
                logGroupName=self.log_groups,
                startTime=start_time,
                endTime=end_time,
                queryString="fields @timestamp, @message | sort @timestamp asc",
                limit=max_logs,
            )
            return response["queryId"]
        except self.cloudwatch_logs.exceptions.ResourceNotFoundException as e:
            raise DateOutOfBoundsError(f"Resource not found: {e}")

    def _wait_for_query_results(self, query_id):
        """
        Waits for the query to complete and retrieves the results.

        :param query_id: The ID of the initiated query.
        :type query_id: str
        :return: A list containing the results of the query.
        :rtype: list
        """
        while True:
            time.sleep(1)
            results = self.cloudwatch_logs.get_query_results(queryId=query_id)
            if results["status"] in [
                "Complete",
                "Failed",
                "Cancelled",
                "Timeout",
                "Unknown",
            ]:
                return results.get("results", [])
