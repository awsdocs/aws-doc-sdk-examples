import boto3
from datetime import datetime, timezone
import time
import logging
import sys


class DateOutOfBoundsError(Exception):
    """Exception raised when the date range for a query is out of bounds."""
    pass


class CloudWatchQuery:
    """A class to query AWS CloudWatch logs within a specified date range.

    Attributes:
        client (Client): The Cloudwatch Logs Client
        log_groups (list): Names of the log groups to query.
        date_range (tuple): Start and end datetime for the query.
        limit (int): Maximum number of log entries to return.
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

    def execute_query(self):
        """Executes the CloudWatch logs query and calculates the execution time."""
        start_time = datetime.now()

        if not self.is_datetime(self.date_range[0], self.datetime_format):
            start_date = self.convert_unix_timestamp_to_datetime(self.date_range[0])
        else:
            start_date = self.date_range[0]

        if not self.is_datetime(self.date_range[1], self.datetime_format):
            end_date = self.convert_unix_timestamp_to_datetime(self.date_range[1])
        else:
            end_date = self.date_range[1]

        logging.info(
            f"Original query:"
            f"\n       START:    {start_date}"
            f"\n       END:      {end_date}"
        )
        logs_batch = self._recursive_query(
            (start_date, end_date)
        )
        end_time = datetime.now()
        self.query_duration = (end_time - start_time).total_seconds()
        return logs_batch

    def _recursive_query(self, date_range, logs_batch=None):
        """Performs a recursive query to handle large data sets.

        Args:
            date_range (tuple): Start and end UNIX timestamps for the query.
            ISO 8601 format including date and time

        Returns:
            list: Aggregated results from the recursive queries.
        """
        if logs_batch is None:
            logs_batch = []

        while True:
            logging.info(
                f"Recursive Query:"
                f"\n       START:    {date_range[0]}"
                f"\n       END:      {date_range[1]}"
            )
            new_logs_batch = self._perform_query(date_range, self.limit)
            new_log_count = len(new_logs_batch)
            logging.info(f"Logs returned: {new_log_count}")
            logs_batch.extend(new_logs_batch)

            # If returned logs exceed 10,000 log limit, divide in half and try again.
            if new_log_count < self.limit:
                break
            else:
                logging.info(f"Logs cut off at {self.limit} limit.")
                most_recent_log = self._find_most_recent_log(logs_batch)
                most_recent_log_timestamp = next(item['value'] for item in most_recent_log if item['field'] == '@timestamp')
                new_range = (most_recent_log_timestamp, date_range[1])
                logging.info(f"Recursive log count: {len(logs_batch)}")
                logs_batch.extend(self._recursive_query(new_range, logs_batch))
                break
        return logs_batch

    @staticmethod
    def convert_unix_timestamp_to_iso1806(unix_timestamp, iso1806_format="%Y-%m-%d %H:%M:%S.%f"):
        """
        Converts a UNIX timetamp in milliseconds
        :param unix_timestamp:
        :param iso1806_format:
        :return:
        """
        in_seconds = unix_timestamp / 1000.0
        date_time = datetime.utcfromtimestamp(in_seconds)
        iso1806 = date_time.strftime(iso1806_format)
        return iso1806

    @staticmethod
    def convert_iso1806_to_datetime(iso1806, iso1806_format="%Y-%m-%d %H:%M:%S.%f"):
        """
        Converts a ISO1806 date to Python datetime object.
        :param iso1806:
        :param iso1806_format:
        :return: datetime
        """
        date = datetime.strptime(iso1806, iso1806_format)
        return date

    def convert_unix_timestamp_to_datetime(self, unix_timestamp):
        """

        :param unix_timestamp:
        :return:
        """
        ts = self.convert_unix_timestamp_to_iso1806(unix_timestamp)
        dt = self.convert_iso1806_to_datetime(ts)
        return dt

    def convert_iso1806_to_unix_timestamp(self, iso1806):
        """

        :param iso1806:
        :return:
        """
        dt = self.convert_iso1806_to_datetime(iso1806)
        unix_timestamp = dt.replace(tzinfo=timezone.utc).timestamp()
        return unix_timestamp * 1000

    @staticmethod
    def convert_datetime_to_unix_timestamp(dt):
        """

        :param dt:
        :return:
        """
        unix_timestamp = dt.replace(tzinfo=timezone.utc).timestamp()
        return unix_timestamp * 1000

    @staticmethod
    def is_datetime(date_string, format_string):
        """

        :param date_string:
        :param format_string:
        :return:
        """
        try:
            datetime.strptime(date_string, format_string)
            return True
        except ValueError:
            return False

    def _compare_dates(self, date_str1, date_str2):
        """
        Compare two dates in ISO 8601 format.
        :param date_str1:
        :param date_str2:
        :return:
        """
        date1 = self.convert_iso1806_to_datetime(date_str1)
        date2 = self.convert_iso1806_to_datetime(date_str2)

        if date1 > date2:
            return date_str1
        else:
            return date_str2

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
                if item['field'] == '@timestamp':
                    logging.debug(f"Compared: {item['value']} to {most_recent_date}")
                    if self._compare_dates(item['value'], most_recent_date) == item['value']:
                        logging.debug(f"New most recent: {item['value']}")
                        most_recent_date = item['value']
                        most_recent_log = log
        logging.info(f"Most recent log date of batch: {most_recent_date}")
        return most_recent_log

    def _divide_date_range(self, date_range):
        """Splits a date range into two equal halves.

        Args:
            date_range (tuple): Start and end datetime.

        Returns:
            list of tuples: Two split date ranges.
        """
        midpoint = (date_range[0] + date_range[1]) / 2
        return [(date_range[0], round(midpoint)), (round(midpoint), date_range[1])]

    def _perform_query(self, date_range, max_logs):
        """Performs the actual CloudWatch log query.

        Args:
            date_range (tuple): Start and end datetime for the query.
            max_logs (int): Maximum number of logs to retrieve.

        Returns:
            list: Query results.
        """
        try:
            query_id = self._initiate_query(date_range, max_logs)
            return self._wait_for_query_results(query_id)
        except DateOutOfBoundsError:
            return []

    def _initiate_query(self, date_range, max_logs):
        """Initiates the CloudWatch logs query.

        Args:
            date_range (tuple): Start and end datetime for the query.
            max_logs (int): Maximum number of logs to retrieve.

        Returns:
            str: Query ID.
        """
        try:
            start_time = round(self.convert_iso1806_to_unix_timestamp(date_range[0]))
            end_time = round(self.convert_iso1806_to_unix_timestamp(date_range[1]))
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
        """Waits for the query to complete and retrieves the results.

        Args:
            query_id (str): The ID of the initiated query.

        Returns:
            list: The results of the query.
        """
        while True:
            time.sleep(1)
            results = self.cloudwatch_logs.get_query_results(queryId=query_id)
            if results["status"] in ["Complete", "Failed", "Cancelled", "Timeout", "Unknown"]:
                return results.get("results", [])
