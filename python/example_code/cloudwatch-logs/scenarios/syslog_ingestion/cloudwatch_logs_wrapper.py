# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Wrapper class for Amazon CloudWatch Logs operations, including the new
syslog configuration management APIs.
"""

import json
import logging
from datetime import datetime, timezone
from typing import Any, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.cloudwatchlogs.CloudWatchLogsWrapper.decl]
class CloudWatchLogsWrapper:
    """Encapsulates Amazon CloudWatch Logs actions."""

    def __init__(self, logs_client: boto3.client) -> None:
        """
        Initializes the CloudWatchLogsWrapper with a CloudWatch Logs client.

        :param logs_client: A Boto3 CloudWatch Logs client.
        """
        self.logs_client = logs_client

    @classmethod
    def from_client(cls) -> "CloudWatchLogsWrapper":
        """
        Creates a CloudWatchLogsWrapper instance with a default CloudWatch Logs client.

        :return: An instance of CloudWatchLogsWrapper.
        """
        logs_client = boto3.client("logs")
        return cls(logs_client)

    # snippet-end:[python.example_code.cloudwatchlogs.CloudWatchLogsWrapper.decl]

    # snippet-start:[python.example_code.cloudwatchlogs.CreateLogGroup]
    def create_log_group(self, log_group_name: str) -> None:
        """
        Creates a CloudWatch Logs log group.

        :param log_group_name: The name of the log group to create.
        :raises ClientError: If the API call fails for a reason other than the
            log group already existing.
        """
        try:
            self.logs_client.create_log_group(logGroupName=log_group_name)
            logger.info("Created log group '%s'.", log_group_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceAlreadyExistsException":
                logger.info(
                    "Log group '%s' already exists. Proceeding with the existing log group.",
                    log_group_name,
                )
            else:
                logger.error(
                    "Failed to create log group '%s': %s",
                    log_group_name,
                    error.response["Error"]["Message"],
                )
                raise

    # snippet-end:[python.example_code.cloudwatchlogs.CreateLogGroup]

    # snippet-start:[python.example_code.cloudwatchlogs.DescribeLogGroups]
    def describe_log_groups(
        self, log_group_name_prefix: Optional[str] = None, limit: int = 50
    ) -> list[dict[str, Any]]:
        """
        Describes log groups, optionally filtered by a name prefix.

        :param log_group_name_prefix: When set, only log groups whose names
            start with this prefix are returned.
        :param limit: The maximum number of log groups to return.
        :return: A list of log group description dictionaries.
        :raises ClientError: If the service is unavailable.
        """
        try:
            params = dict()
            if log_group_name_prefix is not None:
                params["logGroupNamePrefix"] = log_group_name_prefix
            params["limit"] = limit

            paginator = self.logs_client.get_paginator("describe_log_groups")
            log_groups = list()
            for page in paginator.paginate(**params):
                log_groups.extend(page.get("logGroups", list()))
            logger.info("Described %d log group(s).", len(log_groups))
            return log_groups
        except ClientError as error:
            if error.response["Error"]["Code"] == "ServiceUnavailableException":
                logger.error(
                    "Service is temporarily unavailable. Please retry later. %s",
                    error.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Failed to describe log groups: %s",
                    error.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.cloudwatchlogs.DescribeLogGroups]

    # snippet-start:[python.example_code.cloudwatchlogs.PutSyslogConfiguration]
    def put_syslog_configuration(
        self, log_group_identifier: str, vpc_endpoint_id: str
    ) -> None:
        """
        Creates or updates a syslog configuration for a log group. This enables
        ingestion of syslog data through the specified VPC endpoint.

        :param log_group_identifier: The name or ARN of the log group to associate
            with the syslog configuration.
        :param vpc_endpoint_id: The ID of the VPC endpoint to use for syslog
            ingestion.
        :raises ClientError: If the log group or VPC endpoint does not exist.
        """
        try:
            self.logs_client.put_syslog_configuration(
                logGroupIdentifier=log_group_identifier,
                vpcEndpointId=vpc_endpoint_id,
            )
            logger.info(
                "Created syslog configuration for log group '%s' with VPC endpoint '%s'.",
                log_group_identifier,
                vpc_endpoint_id,
            )
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "The specified log group or VPC endpoint does not exist: %s",
                    error.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Failed to put syslog configuration: %s",
                    error.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.cloudwatchlogs.PutSyslogConfiguration]

    # snippet-start:[python.example_code.cloudwatchlogs.ListSyslogConfigurations]
    def list_syslog_configurations(
        self,
        log_group_identifier: Optional[str] = None,
        vpc_endpoint_id: Optional[str] = None,
    ) -> list[dict[str, Any]]:
        """
        Lists syslog configurations, optionally filtered by log group or VPC endpoint.

        Handles pagination by following nextToken until all results are returned.

        :param log_group_identifier: Optional log group name or ARN to filter by.
        :param vpc_endpoint_id: Optional VPC endpoint ID to filter by.
        :return: A list of syslog configuration dictionaries.
        :raises ClientError: If a filter parameter is invalid.
        """
        try:
            configurations = list()
            params = dict()
            if log_group_identifier is not None:
                params["logGroupIdentifier"] = log_group_identifier
            if vpc_endpoint_id is not None:
                params["vpcEndpointId"] = vpc_endpoint_id

            while True:
                response = self.logs_client.list_syslog_configurations(**params)
                configurations.extend(response.get("syslogConfigurations", list()))
                next_token = response.get("nextToken", None)
                if next_token is None:
                    break
                params["nextToken"] = next_token

            logger.info("Listed %d syslog configuration(s).", len(configurations))
            return configurations
        except ClientError as error:
            if error.response["Error"]["Code"] == "InvalidParameterException":
                logger.error(
                    "Invalid filter parameter: %s. Ensure the log group identifier "
                    "or VPC endpoint ID is correctly formatted.",
                    error.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Failed to list syslog configurations: %s",
                    error.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.cloudwatchlogs.ListSyslogConfigurations]

    # snippet-start:[python.example_code.cloudwatchlogs.DeleteSyslogConfiguration]
    def delete_syslog_configuration(
        self, log_group_identifier: str, vpc_endpoint_id: str
    ) -> None:
        """
        Deletes a syslog configuration for a log group. After deletion, syslog
        data is no longer ingested through the specified VPC endpoint.

        :param log_group_identifier: The name or ARN of the log group.
        :param vpc_endpoint_id: The ID of the VPC endpoint associated with the
            syslog configuration.
        :raises ClientError: If the syslog configuration does not exist (in which
            case the error is logged but not re-raised during cleanup).
        """
        try:
            self.logs_client.delete_syslog_configuration(
                logGroupIdentifier=log_group_identifier,
                vpcEndpointId=vpc_endpoint_id,
            )
            logger.info(
                "Deleted syslog configuration for log group '%s' and VPC endpoint '%s'.",
                log_group_identifier,
                vpc_endpoint_id,
            )
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.info(
                    "Syslog configuration does not exist or was already deleted: %s",
                    error.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Failed to delete syslog configuration: %s",
                    error.response["Error"]["Message"],
                )
                raise

    # snippet-end:[python.example_code.cloudwatchlogs.DeleteSyslogConfiguration]

    # snippet-start:[python.example_code.cloudwatchlogs.DeleteLogGroup]
    def delete_log_group(self, log_group_name: str) -> None:
        """
        Deletes a CloudWatch Logs log group and all archived log events.

        :param log_group_name: The name of the log group to delete.
        :raises ClientError: If the log group does not exist (logged and
            continues gracefully during cleanup).
        """
        try:
            self.logs_client.delete_log_group(logGroupName=log_group_name)
            logger.info("Deleted log group '%s'.", log_group_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.info(
                    "Log group '%s' does not exist or was already deleted: %s",
                    log_group_name,
                    error.response["Error"]["Message"],
                )
            else:
                logger.error(
                    "Failed to delete log group '%s': %s",
                    log_group_name,
                    error.response["Error"]["Message"],
                )
                raise

    # snippet-end:[python.example_code.cloudwatchlogs.DeleteLogGroup]

    def put_resource_policy(
        self,
        log_group_arn: str,
        vpc_endpoint_id: str,
    ) -> dict[str, Any]:
        """
        Creates or updates a resource-scoped policy on a log group that grants
        the syslog.logs.amazonaws.com service principal permission to call
        logs:PutLogEvents and logs:CreateLogStream.

        A resource-scoped policy (one attached to a specific log group) is
        specified with resourceArn and policyDocument. The PutResourcePolicy
        API does not allow policyName to be combined with resourceArn.

        :param log_group_arn: The ARN of the log group (with trailing :*).
        :param vpc_endpoint_id: The VPC endpoint ID to scope the condition.
        :return: The resource policy response.
        """
        policy_document = json.dumps(
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Sid": "SyslogIngestPermissions",
                        "Effect": "Allow",
                        "Principal": {"Service": "syslog.logs.amazonaws.com"},
                        "Action": [
                            "logs:PutLogEvents",
                            "logs:CreateLogStream",
                        ],
                        "Resource": log_group_arn,
                        "Condition": {
                            "StringEquals": {"aws:sourceVpce": vpc_endpoint_id}
                        },
                    }
                ],
            }
        )
        try:
            response = self.logs_client.put_resource_policy(
                policyDocument=policy_document,
                resourceArn=log_group_arn,
            )
            logger.info("Put resource policy on log group '%s'.", log_group_arn)
            return response
        except ClientError as error:
            logger.error(
                "Failed to put resource policy: %s",
                error.response["Error"]["Message"],
            )
            raise
