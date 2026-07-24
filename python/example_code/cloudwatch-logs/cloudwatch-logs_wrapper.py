# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon CloudWatch Logs wrapper class for managing log groups, streams, events,
metric filters, and retention policies.
"""

import logging
import time
from datetime import datetime, timezone
from typing import Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.cloudwatch-logs.CloudWatchLogsWrapper.class]
# snippet-start:[python.example_code.cloudwatch-logs.CloudWatchLogsWrapper.decl]
class CloudWatchLogsWrapper:
    """Encapsulates Amazon CloudWatch Logs operations."""

    def __init__(self, logs_client):
        """
        Initializes the CloudWatchLogsWrapper with a Boto3 CloudWatch Logs client.

        :param logs_client: A Boto3 CloudWatch Logs client.
        """
        self.logs_client = logs_client

    @classmethod
    def from_client(cls):
        """
        Creates a CloudWatchLogsWrapper instance from a new Boto3 client.

        :return: A CloudWatchLogsWrapper instance.
        """
        logs_client = boto3.client("logs")
        return cls(logs_client)

    # snippet-end:[python.example_code.cloudwatch-logs.CloudWatchLogsWrapper.decl]

    # snippet-start:[python.example_code.cloudwatch-logs.CreateLogGroup]
    def create_log_group(self, log_group_name: str, tags: Optional[Dict[str, str]] = None) -> None:
        """
        Creates a CloudWatch Logs log group.

        :param log_group_name: The name of the log group to create.
        :param tags: Optional tags to apply to the log group.
        :raises ClientError: If the log group cannot be created.
        """
        try:
            params = dict()
            params["logGroupName"] = log_group_name
            if tags is not None:
                params["tags"] = tags
            self.logs_client.create_log_group(**params)
            logger.info("Created log group: %s", log_group_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceAlreadyExistsException":
                logger.info("Log group '%s' already exists.", log_group_name)
            else:
                logger.error(
                    "Failed to create log group '%s': %s",
                    log_group_name,
                    error.response["Error"]["Message"],
                )
                raise
    # snippet-end:[python.example_code.cloudwatch-logs.CreateLogGroup]

    # snippet-start:[python.example_code.cloudwatch-logs.CreateLogStream]
    def create_log_stream(self, log_group_name: str, log_stream_name: str) -> None:
        """
        Creates a log stream within a log group.

        :param log_group_name: The name of the log group.
        :param log_stream_name: The name of the log stream to create.
        :raises ClientError: If the log stream cannot be created.
        """
        try:
            self.logs_client.create_log_stream(
                logGroupName=log_group_name,
                logStreamName=log_stream_name,
            )
            logger.info("Created log stream: %s", log_stream_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceAlreadyExistsException":
                logger.info("Log stream '%s' already exists.", log_stream_name)
            else:
                logger.error(
                    "Failed to create log stream '%s': %s",
                    log_stream_name,
                    error.response["Error"]["Message"],
                )
                raise
    # snippet-end:[python.example_code.cloudwatch-logs.CreateLogStream]

    # snippet-start:[python.example_code.cloudwatch-logs.PutLogEvents]
    def put_log_events(
        self,
        log_group_name: str,
        log_stream_name: str,
        log_events: List[Dict],
    ) -> dict:
        """
        Uploads a batch of log events to a log stream.

        :param log_group_name: The name of the log group.
        :param log_stream_name: The name of the log stream.
        :param log_events: A list of log event dicts with 'timestamp' and 'message' keys.
        :return: The response from the PutLogEvents call.
        :raises ClientError: If the log events cannot be uploaded.
        """
        try:
            response = self.logs_client.put_log_events(
                logGroupName=log_group_name,
                logStreamName=log_stream_name,
                logEvents=log_events,
            )
            logger.info(
                "Put %d log events to stream '%s'.",
                len(log_events),
                log_stream_name,
            )
            return response
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Log group or stream not found. Verify '%s' and '%s' exist.",
                    log_group_name,
                    log_stream_name,
                )
            else:
                logger.error(
                    "Failed to put log events: %s",
                    error.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.cloudwatch-logs.PutLogEvents]

    # snippet-start:[python.example_code.cloudwatch-logs.GetLogEvents]
    def get_log_events(
        self,
        log_group_name: str,
        log_stream_name: str,
        start_from_head: bool = True,
    ) -> List[Dict]:
        """
        Retrieves log events from a log stream.

        :param log_group_name: The name of the log group.
        :param log_stream_name: The name of the log stream.
        :param start_from_head: If True, read from the beginning of the stream.
        :return: A list of log events.
        :raises ClientError: If the log events cannot be retrieved.
        """
        try:
            response = self.logs_client.get_log_events(
                logGroupName=log_group_name,
                logStreamName=log_stream_name,
                startFromHead=start_from_head,
            )
            events = response.get("events", list())
            logger.info("Retrieved %d log events.", len(events))
            return events
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Log group or stream not found. Verify '%s' and '%s' exist.",
                    log_group_name,
                    log_stream_name,
                )
            else:
                logger.error(
                    "Failed to get log events: %s",
                    error.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.cloudwatch-logs.GetLogEvents]

    # snippet-start:[python.example_code.cloudwatch-logs.FilterLogEvents]
    def filter_log_events(
        self,
        log_group_name: str,
        filter_pattern: str,
    ) -> List[Dict]:
        """
        Filters log events in a log group using a filter pattern.

        :param log_group_name: The name of the log group to search.
        :param filter_pattern: The filter pattern to apply.
        :return: A list of matching log events.
        :raises ClientError: If the log events cannot be filtered.
        """
        try:
            paginator = self.logs_client.get_paginator("filter_log_events")
            events = list()
            for page in paginator.paginate(
                logGroupName=log_group_name,
                filterPattern=filter_pattern,
            ):
                events.extend(page.get("events", list()))
            logger.info(
                "Found %d events matching pattern '%s'.",
                len(events),
                filter_pattern,
            )
            return events
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Log group '%s' not found. Verify the log group name.",
                    log_group_name,
                )
            else:
                logger.error(
                    "Failed to filter log events: %s",
                    error.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.cloudwatch-logs.FilterLogEvents]

    # snippet-start:[python.example_code.cloudwatch-logs.PutMetricFilter]
    def put_metric_filter(
        self,
        log_group_name: str,
        filter_name: str,
        filter_pattern: str,
        metric_name: str,
        metric_namespace: str,
        metric_value: str,
        default_value: float = 0,
    ) -> None:
        """
        Creates or updates a metric filter for a log group.

        :param log_group_name: The name of the log group.
        :param filter_name: The name of the metric filter.
        :param filter_pattern: The filter pattern.
        :param metric_name: The name of the CloudWatch metric.
        :param metric_namespace: The namespace of the CloudWatch metric.
        :param metric_value: The value to publish when the pattern matches.
        :param default_value: The value to use when no matches are found.
        :raises ClientError: If the metric filter cannot be created.
        """
        try:
            self.logs_client.put_metric_filter(
                logGroupName=log_group_name,
                filterName=filter_name,
                filterPattern=filter_pattern,
                metricTransformations=[
                    {
                        "metricName": metric_name,
                        "metricNamespace": metric_namespace,
                        "metricValue": metric_value,
                        "defaultValue": default_value,
                    }
                ],
            )
            logger.info(
                "Created metric filter '%s' on log group '%s'.",
                filter_name,
                log_group_name,
            )
        except ClientError as error:
            if error.response["Error"]["Code"] == "LimitExceededException":
                logger.error(
                    "Maximum number of metric filters reached for log group '%s'.",
                    log_group_name,
                )
            else:
                logger.error(
                    "Failed to create metric filter: %s",
                    error.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.cloudwatch-logs.PutMetricFilter]

    # snippet-start:[python.example_code.cloudwatch-logs.DescribeMetricFilters]
    def describe_metric_filters(
        self,
        log_group_name: str,
        filter_name_prefix: Optional[str] = None,
    ) -> List[Dict]:
        """
        Describes metric filters for a log group.

        :param log_group_name: The name of the log group.
        :param filter_name_prefix: Optional prefix to filter results.
        :return: A list of metric filter descriptions.
        :raises ClientError: If metric filters cannot be described.
        """
        try:
            paginator = self.logs_client.get_paginator("describe_metric_filters")
            params = dict()
            params["logGroupName"] = log_group_name
            if filter_name_prefix is not None:
                params["filterNamePrefix"] = filter_name_prefix
            metric_filters = list()
            for page in paginator.paginate(**params):
                metric_filters.extend(page.get("metricFilters", list()))
            logger.info("Found %d metric filter(s).", len(metric_filters))
            return metric_filters
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Log group '%s' not found. Verify the log group name.",
                    log_group_name,
                )
            else:
                logger.error(
                    "Failed to describe metric filters: %s",
                    error.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.cloudwatch-logs.DescribeMetricFilters]

    # snippet-start:[python.example_code.cloudwatch-logs.PutRetentionPolicy]
    def put_retention_policy(self, log_group_name: str, retention_in_days: int) -> None:
        """
        Sets a retention policy on a log group.

        :param log_group_name: The name of the log group.
        :param retention_in_days: The number of days to retain log events.
        :raises ClientError: If the retention policy cannot be set.
        """
        try:
            self.logs_client.put_retention_policy(
                logGroupName=log_group_name,
                retentionInDays=retention_in_days,
            )
            logger.info(
                "Set retention policy to %d days for log group '%s'.",
                retention_in_days,
                log_group_name,
            )
        except ClientError as error:
            if error.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid retention value %d. Valid values: "
                    "1, 3, 5, 7, 14, 30, 60, 90, 120, 150, 180, 365, 400, "
                    "545, 731, 1096, 1827, 2192, 2557, 2922, 3288, 3653.",
                    retention_in_days,
                )
            else:
                logger.error(
                    "Failed to set retention policy: %s",
                    error.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.cloudwatch-logs.PutRetentionPolicy]

    # snippet-start:[python.example_code.cloudwatch-logs.DescribeLogGroups]
    def describe_log_groups(
        self, log_group_name_prefix: Optional[str] = None, limit: Optional[int] = None
    ) -> List[Dict]:
        """
        Describes log groups in the account.

        :param log_group_name_prefix: Optional prefix to filter log groups.
        :param limit: Maximum number of log groups to return.
        :return: A list of log group descriptions.
        :raises ClientError: If log groups cannot be described.
        """
        try:
            paginator = self.logs_client.get_paginator("describe_log_groups")
            params = dict()
            if log_group_name_prefix is not None:
                params["logGroupNamePrefix"] = log_group_name_prefix
            pagination_config = dict()
            if limit is not None:
                pagination_config["MaxItems"] = limit
            if pagination_config:
                params["PaginationConfig"] = pagination_config
            log_groups = list()
            for page in paginator.paginate(**params):
                log_groups.extend(page.get("logGroups", list()))
                if limit is not None and len(log_groups) >= limit:
                    log_groups = log_groups[:limit]
                    break
            logger.info("Found %d log group(s).", len(log_groups))
            return log_groups
        except ClientError as error:
            if error.response["Error"]["Code"] == "ServiceUnavailableException":
                logger.error(
                    "CloudWatch Logs service is temporarily unavailable. "
                    "Please retry after a brief delay."
                )
            else:
                logger.error(
                    "Failed to describe log groups: %s",
                    error.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.cloudwatch-logs.DescribeLogGroups]

    # snippet-start:[python.example_code.cloudwatch-logs.DeleteLogGroup]
    def delete_log_group(self, log_group_name: str) -> None:
        """
        Deletes a log group and all its associated resources.

        :param log_group_name: The name of the log group to delete.
        :raises ClientError: If the log group cannot be deleted.
        """
        try:
            self.logs_client.delete_log_group(logGroupName=log_group_name)
            logger.info("Deleted log group: %s", log_group_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.info(
                    "Log group '%s' was already deleted or does not exist.",
                    log_group_name,
                )
            else:
                logger.error(
                    "Failed to delete log group '%s': %s",
                    log_group_name,
                    error.response["Error"]["Message"],
                )
                raise
    # snippet-end:[python.example_code.cloudwatch-logs.DeleteLogGroup]


# snippet-end:[python.example_code.cloudwatch-logs.CloudWatchLogsWrapper.class]
