# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Wrapper class for Amazon CloudWatch Logs operations.

This module encapsulates CloudWatch Logs service operations including creating
and managing log groups and streams, ingesting log events, configuring retention
policies, searching and filtering logs, running Insights queries, and using
Live Tail for real-time log streaming.
"""

import logging
import time
from datetime import datetime, timezone
from typing import Any, Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.cloudwatch-logs.CloudWatchLogsWrapper.class]
# snippet-start:[python.example_code.cloudwatch-logs.CloudWatchLogsWrapper.decl]
class CloudWatchLogsWrapper:
    """Encapsulates Amazon CloudWatch Logs operations."""

    def __init__(self, logs_client: Any) -> None:
        """
        Initializes the CloudWatchLogsWrapper with a CloudWatch Logs client.

        :param logs_client: A Boto3 CloudWatch Logs client.
        """
        self.logs_client = logs_client

    @classmethod
    def from_client(cls) -> "CloudWatchLogsWrapper":
        """
        Creates a CloudWatchLogsWrapper instance from a new Boto3 client.

        :return: A new CloudWatchLogsWrapper instance.
        """
        logs_client = boto3.client("logs")
        return cls(logs_client)

    # snippet-end:[python.example_code.cloudwatch-logs.CloudWatchLogsWrapper.decl]

    # snippet-start:[python.example_code.cloudwatch-logs.CreateLogGroup]
    def create_log_group(
        self, log_group_name: str, log_group_class: Optional[str] = None
    ) -> None:
        """
        Creates a CloudWatch Logs log group.

        :param log_group_name: The name of the log group to create.
        :param log_group_class: The log group class (STANDARD or INFREQUENT_ACCESS).
        :raises ClientError: If the log group already exists or another error occurs.
        """
        try:
            params = dict()
            params["logGroupName"] = log_group_name
            if log_group_class is not None:
                params["logGroupClass"] = log_group_class
            self.logs_client.create_log_group(**params)
            logger.info("Created log group '%s'.", log_group_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceAlreadyExistsException":
                logger.error(
                    "Log group '%s' already exists. Use a unique name.",
                    log_group_name,
                )
            raise

    # snippet-end:[python.example_code.cloudwatch-logs.CreateLogGroup]

    # snippet-start:[python.example_code.cloudwatch-logs.CreateLogStream]
    def create_log_stream(self, log_group_name: str, log_stream_name: str) -> None:
        """
        Creates a log stream within a log group.

        :param log_group_name: The name of the log group.
        :param log_stream_name: The name of the log stream to create.
        :raises ClientError: If the log group doesn't exist or another error occurs.
        """
        try:
            self.logs_client.create_log_stream(
                logGroupName=log_group_name, logStreamName=log_stream_name
            )
            logger.info(
                "Created log stream '%s' in log group '%s'.",
                log_stream_name,
                log_group_name,
            )
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Log group '%s' does not exist. Create it first.",
                    log_group_name,
                )
            raise

    # snippet-end:[python.example_code.cloudwatch-logs.CreateLogStream]

    # snippet-start:[python.example_code.cloudwatch-logs.PutLogEvents]
    def put_log_events(
        self,
        log_group_name: str,
        log_stream_name: str,
        log_events: List[Dict[str, Any]],
    ) -> None:
        """
        Uploads a batch of log events to a log stream.

        :param log_group_name: The name of the log group.
        :param log_stream_name: The name of the log stream.
        :param log_events: A list of log event dicts, each with 'message' and 'timestamp'.
        :raises ClientError: If the log group or stream doesn't exist.
        """
        try:
            self.logs_client.put_log_events(
                logGroupName=log_group_name,
                logStreamName=log_stream_name,
                logEvents=log_events,
            )
            logger.info(
                "Put %d log events to '%s/%s'.",
                len(log_events),
                log_group_name,
                log_stream_name,
            )
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Log group '%s' or stream '%s' does not exist.",
                    log_group_name,
                    log_stream_name,
                )
            raise

    # snippet-end:[python.example_code.cloudwatch-logs.PutLogEvents]

    # snippet-start:[python.example_code.cloudwatch-logs.PutRetentionPolicy]
    def put_retention_policy(
        self, log_group_name: str, retention_in_days: int
    ) -> None:
        """
        Sets the retention policy for a log group.

        :param log_group_name: The name of the log group.
        :param retention_in_days: The number of days to retain log events.
        :raises ClientError: If the log group doesn't exist.
        """
        try:
            self.logs_client.put_retention_policy(
                logGroupName=log_group_name, retentionInDays=retention_in_days
            )
            logger.info(
                "Set retention policy for '%s' to %d days.",
                log_group_name,
                retention_in_days,
            )
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Log group '%s' does not exist. Cannot set retention policy.",
                    log_group_name,
                )
            raise

    # snippet-end:[python.example_code.cloudwatch-logs.PutRetentionPolicy]

    # snippet-start:[python.example_code.cloudwatch-logs.DescribeLogGroups]
    def describe_log_groups(
        self, log_group_name_prefix: Optional[str] = None
    ) -> List[Dict[str, Any]]:
        """
        Describes log groups, optionally filtered by a prefix.

        :param log_group_name_prefix: A prefix to filter log groups by name.
        :return: A list of log group information dictionaries.
        :raises ClientError: If parameters are invalid.
        """
        try:
            log_groups = list()
            paginator = self.logs_client.get_paginator("describe_log_groups")
            params = dict()
            if log_group_name_prefix is not None:
                params["logGroupNamePrefix"] = log_group_name_prefix
            page_iterator = paginator.paginate(**params)
            for page in page_iterator:
                log_groups.extend(page.get("logGroups", list()))
            logger.info("Described %d log group(s).", len(log_groups))
            return log_groups
        except ClientError as error:
            if error.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid parameter for DescribeLogGroups. Check prefix and pagination values."
                )
            raise

    # snippet-end:[python.example_code.cloudwatch-logs.DescribeLogGroups]

    # snippet-start:[python.example_code.cloudwatch-logs.FilterLogEvents]
    def filter_log_events(
        self,
        log_group_name: str,
        filter_pattern: Optional[str] = None,
        start_time: Optional[int] = None,
        end_time: Optional[int] = None,
        log_stream_names: Optional[List[str]] = None,
    ) -> List[Dict[str, Any]]:
        """
        Filters log events from a log group using a pattern.

        :param log_group_name: The name of the log group to search.
        :param filter_pattern: The filter pattern to apply (e.g., "ERROR").
        :param start_time: Start of time range in epoch milliseconds.
        :param end_time: End of time range in epoch milliseconds.
        :param log_stream_names: Specific log streams to search.
        :return: A list of matching log events.
        :raises ClientError: If the log group doesn't exist.
        """
        try:
            events = list()
            paginator = self.logs_client.get_paginator("filter_log_events")
            params = dict()
            params["logGroupName"] = log_group_name
            if filter_pattern is not None:
                params["filterPattern"] = filter_pattern
            if start_time is not None:
                params["startTime"] = start_time
            if end_time is not None:
                params["endTime"] = end_time
            if log_stream_names is not None:
                params["logStreamNames"] = log_stream_names
            page_iterator = paginator.paginate(**params)
            for page in page_iterator:
                events.extend(page.get("events", list()))
            logger.info("Found %d matching log event(s).", len(events))
            return events
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Log group '%s' does not exist.", log_group_name
                )
            raise

    # snippet-end:[python.example_code.cloudwatch-logs.FilterLogEvents]

    # snippet-start:[python.example_code.cloudwatch-logs.StartLiveTail]
    def start_live_tail(
        self,
        log_group_identifiers: List[str],
        log_stream_names: Optional[List[str]] = None,
        log_event_filter_pattern: Optional[str] = None,
        duration_seconds: int = 10,
    ) -> List[Dict[str, Any]]:
        """
        Starts a Live Tail session to stream log events in near real time.

        :param log_group_identifiers: List of log group ARNs to tail.
        :param log_stream_names: Optional list of log stream names to filter.
        :param log_event_filter_pattern: Optional filter pattern for events.
        :param duration_seconds: How long to keep the session open (default 10s).
        :return: A list of log events received during the session.
        :raises ClientError: If parameters are invalid.
        """
        try:
            params = dict()
            params["logGroupIdentifiers"] = log_group_identifiers
            if log_stream_names is not None:
                params["logStreamNames"] = log_stream_names
            if log_event_filter_pattern is not None:
                params["logEventFilterPattern"] = log_event_filter_pattern

            response = self.logs_client.start_live_tail(**params)
            event_stream = response["responseStream"]
            session_events = list()
            start_time = time.time()

            for event in event_stream:
                if (time.time() - start_time) >= duration_seconds:
                    event_stream.close()
                    break
                if "sessionStart" in event:
                    session_start = event["sessionStart"]
                    logger.info(
                        "Live Tail session started (ID: %s).",
                        session_start.get("sessionId", "unknown"),
                    )
                elif "sessionUpdate" in event:
                    log_events = event["sessionUpdate"].get("sessionResults", list())
                    session_events.extend(log_events)
                    for log_event in log_events:
                        logger.info(
                            "Live Tail event: %s", log_event.get("message", "")
                        )
                else:
                    logger.warning("Unexpected Live Tail event: %s", event)

            logger.info(
                "Live Tail session ended. Received %d event(s).",
                len(session_events),
            )
            return session_events
        except ClientError as error:
            if error.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid parameters for StartLiveTail. "
                    "Check log group identifiers and filter pattern."
                )
            raise

    # snippet-end:[python.example_code.cloudwatch-logs.StartLiveTail]

    # snippet-start:[python.example_code.cloudwatch-logs.StartQuery]
    def start_query(
        self,
        log_group_name: str,
        query_string: str,
        start_time: int,
        end_time: int,
        limit: Optional[int] = None,
    ) -> str:
        """
        Starts a CloudWatch Logs Insights query.

        :param log_group_name: The log group to query.
        :param query_string: The Insights query string.
        :param start_time: Start of time range in epoch seconds.
        :param end_time: End of time range in epoch seconds.
        :param limit: Maximum number of log events to return.
        :return: The query ID for retrieving results.
        :raises ClientError: If the query string is malformed.
        """
        try:
            params = dict()
            params["logGroupName"] = log_group_name
            params["queryString"] = query_string
            params["startTime"] = start_time
            params["endTime"] = end_time
            if limit is not None:
                params["limit"] = limit
            response = self.logs_client.start_query(**params)
            query_id = response["queryId"]
            logger.info("Started query with ID '%s'.", query_id)
            return query_id
        except ClientError as error:
            if error.response["Error"]["Code"] == "MalformedQueryException":
                logger.error(
                    "Query string is malformed. Check the syntax: %s",
                    query_string,
                )
            raise

    # snippet-end:[python.example_code.cloudwatch-logs.StartQuery]

    # snippet-start:[python.example_code.cloudwatch-logs.GetQueryResults]
    def get_query_results(self, query_id: str) -> Dict[str, Any]:
        """
        Retrieves the results of a CloudWatch Logs Insights query.
        Polls until the query status is Complete, Failed, Cancelled, or Timeout.

        :param query_id: The ID of the query to retrieve results for.
        :return: A dictionary containing 'status', 'results', and 'statistics'.
        :raises ClientError: If the query ID is invalid.
        """
        try:
            while True:
                time.sleep(1)
                response = self.logs_client.get_query_results(queryId=query_id)
                status = response.get("status", "Unknown")
                if status in ["Complete", "Failed", "Cancelled", "Timeout", "Unknown"]:
                    logger.info("Query '%s' completed with status '%s'.", query_id, status)
                    return {
                        "status": status,
                        "results": response.get("results", list()),
                        "statistics": response.get("statistics", dict()),
                    }
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Query ID '%s' not found. The query may have expired.",
                    query_id,
                )
            raise

    # snippet-end:[python.example_code.cloudwatch-logs.GetQueryResults]

    # snippet-start:[python.example_code.cloudwatch-logs.DeleteLogGroup]
    def delete_log_group(self, log_group_name: str) -> None:
        """
        Deletes a CloudWatch Logs log group.

        :param log_group_name: The name of the log group to delete.
        :raises ClientError: If the log group doesn't exist.
        """
        try:
            self.logs_client.delete_log_group(logGroupName=log_group_name)
            logger.info("Deleted log group '%s'.", log_group_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Log group '%s' does not exist. It may already be deleted.",
                    log_group_name,
                )
            raise

    # snippet-end:[python.example_code.cloudwatch-logs.DeleteLogGroup]


# snippet-end:[python.example_code.cloudwatch-logs.CloudWatchLogsWrapper.class]
