# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import logging
from typing import Dict

import boto3
import coloredlogs
from prettytable import PrettyTable

from s3_operations import set_legal_hold, set_retention

# Configure logging
logger = logging.getLogger(__name__)
coloredlogs.install(
    level="DEBUG", logger=logger, fmt="%(asctime)s [%(levelname)s] %(message)s"
)


def read_bucket_names() -> Dict[str, str]:
    """
    Read bucket names from the buckets.txt file.

    Returns:
        A dictionary containing the bucket names.
    """
    buckets = {}
    with open("buckets.txt", "r") as f:
        for line in f:
            name, bucket = line.strip().split("=")
            buckets[name] = bucket
    return buckets


def demo_s3_object_locking() -> None:
    """
    Demonstrate S3 object locking by setting legal holds and retention policies on objects.
    """
    s3_client = boto3.client("s3")

    # Read bucket names from file
    buckets = read_bucket_names()
    lock_enabled_bucket = buckets["lock_enabled"]
    retention_bucket = buckets["retention"]

    logger.info("Starting S3 Object Locking Demo")

    # Create and print summary table
    summary_table = PrettyTable()
    summary_table.field_names = ["Bucket", "File Name", "Action", "Details"]

    # Set legal hold on an object in the lock-enabled bucket
    set_legal_hold(s3_client, lock_enabled_bucket, "file0.txt")
    summary_table.add_row(
        [lock_enabled_bucket, "file0.txt", "Legal Hold", "Status: ON"]
    )

    # Set retention period on an object in the lock-enabled bucket
    set_retention(s3_client, lock_enabled_bucket, "file1.txt", 1)
    summary_table.add_row([lock_enabled_bucket, "file1.txt", "Retention", "Days: 1"])

    # Set legal hold on an object in the retention bucket
    set_legal_hold(s3_client, retention_bucket, "file0.txt")
    summary_table.add_row([retention_bucket, "file0.txt", "Legal Hold", "Status: ON"])

    # Set retention period on an object in the retention bucket
    set_retention(s3_client, retention_bucket, "file1.txt", 1)
    summary_table.add_row([retention_bucket, "file1.txt", "Retention", "Days: 1"])

    print("\nSummary of Actions:")
    print(summary_table)
