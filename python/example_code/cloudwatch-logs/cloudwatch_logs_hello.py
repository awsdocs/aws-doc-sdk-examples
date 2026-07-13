# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Hello CloudWatch Logs - demonstrates the simplest interaction with
Amazon CloudWatch Logs by listing log groups in the account.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.cloudwatch-logs.Hello]
def hello_cloudwatch_logs():
    """
    Use the AWS SDK for Python (Boto3) to create a CloudWatch Logs client and
    list the log groups in the account. This example uses the default settings
    specified in your shared credentials and config files.
    """
    print("Hello, CloudWatch Logs! Let's list your log groups:\n")
    print("-" * 55)

    logs_client = boto3.client("logs")

    try:
        paginator = logs_client.get_paginator("describe_log_groups")
        page_iterator = paginator.paginate()
        log_groups = list()
        for page in page_iterator:
            log_groups.extend(page.get("logGroups", list()))

        for log_group in log_groups:
            name = log_group.get("logGroupName", "N/A")
            arn = log_group.get("arn", "N/A")
            retention = log_group.get("retentionInDays", None)
            stored_bytes = log_group.get("storedBytes", 0)
            retention_display = f"{retention} days" if retention is not None else "Never expire"
            print(f"Log Group: {name}")
            print(f"  ARN: {arn}")
            print(f"  Retention: {retention_display}")
            print(f"  Stored Bytes: {stored_bytes}")
        print("-" * 55)
        print(f"Found {len(log_groups)} log group(s).")
    except ClientError as error:
        logger.error(
            "Couldn't list log groups. Error: %s: %s",
            error.response["Error"]["Code"],
            error.response["Error"]["Message"],
        )
        raise


# snippet-end:[python.example_code.cloudwatch-logs.Hello]


if __name__ == "__main__":
    hello_cloudwatch_logs()
