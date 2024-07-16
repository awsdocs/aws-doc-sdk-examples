# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
from typing import Dict

import boto3

from s3_operations import create_buckets, populate_buckets, update_retention_policy


def deploy_s3_object_locking() -> Dict[str, str]:
    """
    Deploy S3 object locking by creating and configuring S3 buckets.

    Returns:
        A dictionary containing the names of the created buckets.
    """
    s3_client = boto3.client("s3")
    buckets = create_buckets(s3_client)
    update_retention_policy(s3_client, buckets["retention"])
    populate_buckets(s3_client, buckets)
    print("Buckets created and populated successfully!")
    return buckets
