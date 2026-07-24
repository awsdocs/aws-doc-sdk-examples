# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Hello CloudWatch Logs - A simple example demonstrating the most basic
interaction with Amazon CloudWatch Logs using the AWS SDK for Python (Boto3).
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.cloudwatch-logs.Hello]
def hello_cloudwatch_logs():
    """
    Demonstrates the simplest interaction with Amazon CloudWatch Logs
    by listing up to 5 log groups in the account.
    """
    logs_client = boto3.client("logs")

    print("\n---------- Hello CloudWatch Logs ----------")
    try:
        response = logs_client.describe_log_groups(limit=5)
        log_groups = response.get("logGroups", list())

        if not log_groups:
            print("No log groups found in this account.")
        else:
            print(f"Found {len(log_groups)} log group(s):")
            for group in log_groups:
                name = group.get("logGroupName", "N/A")
                arn = group.get("arn", "N/A")
                retention = group.get("retentionInDays", None)
                stored_bytes = group.get("storedBytes", 0)
                retention_display = f"{retention} days" if retention else "Never expire"
                print(f"  Log Group: {name}")
                print(f"    ARN: {arn}")
                print(f"    Retention: {retention_display}")
                print(f"    Stored Bytes: {stored_bytes}")
    except ClientError as error:
        logger.error(
            "Error describing log groups: %s", error.response["Error"]["Message"]
        )
        raise


# snippet-end:[python.example_code.cloudwatch-logs.Hello]


if __name__ == "__main__":
    hello_cloudwatch_logs()
