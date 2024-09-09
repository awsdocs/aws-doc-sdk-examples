# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import boto3
from botocore.config import Config
from datetime import datetime, timedelta
import time


class DateOutOfBoundsError(Exception):
    pass


class CloudWatchQuery:
    def __init__(self, log_group_names, date_range, limit=10000):
        self.logs_client = boto3.client(
            "logs", config=Config(retries={"max_attempts": 10})
        )
        self.log_group_names = log_group_names
        self.date_range = date_range
        self.limit = limit
        self.results = []
        self.seconds_elapsed = None

    def run(self):
        start = datetime.now()
        self.results = self._large_query(self.date_range)
        end = datetime.now()
        self.seconds_elapsed = (end - start).total_seconds()
        return self.results

    def _large_query(self, date_range):
        logs = self._query(date_range, self.limit)

        print(
            f"Query date range: {date_range[0].isoformat()} to {date_range[1].isoformat()}. Found {len(logs)} logs."
        )

        if len(logs) < self.limit:
            return logs

        last_log_date = self._get_last_log_date(logs)
        offset_last_log_date = last_log_date + timedelta(milliseconds=1)
        sub_date_range = [offset_last_log_date, date_range[1]]
        r1, r2 = self._split_date_range(sub_date_range)

        results = []
        results.extend(self._large_query(r1))
        results.extend(self._large_query(r2))

        return [logs] + results

    def _get_last_log_date(self, logs):
        timestamps = [log["@timestamp"] for log in logs if "@timestamp" in log]
        timestamps.sort()
        return datetime.fromisoformat(timestamps[-1] + "Z")

    def _split_date_range(self, date_range):
        midpoint = date_range[0] + (date_range[1] - date_range[0]) / 2
        return [date_range[0], midpoint], [midpoint, date_range[1]]

    def _query(self, date_range, max_logs):
        try:
            query_id = self._start_query(date_range, max_logs)
            results = self._wait_until_query_done(query_id)
            return results["results"] if "results" in results else []
        except DateOutOfBoundsError:
            return []

    def _start_query(self, date_range, max_logs):
        try:
            response = self.logs_client.start_query(
                logGroupName=self.log_group_names,
                startTime=int(date_range[0].timestamp() * 1000),
                endTime=int(date_range[1].timestamp() * 1000),
                queryString="fields @timestamp, @message | sort @timestamp asc",
                limit=max_logs,
            )
            return response["queryId"]
        except self.logs_client.exceptions.ResourceNotFoundException as e:
            raise DateOutOfBoundsError(e)

    def _get_query_results(self, query_id):
        return self.logs_client.get_query_results(queryId=query_id)

    def _wait_until_query_done(self, query_id):
        while True:
            time.sleep(1)
            results = self._get_query_results(query_id)
            if results["status"] in [
                "Complete",
                "Failed",
                "Cancelled",
                "Timeout",
                "Unknown",
            ]:
                return results
