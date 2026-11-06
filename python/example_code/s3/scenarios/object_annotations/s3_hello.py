# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Hello Amazon S3! This example lists the S3 buckets in your account to
    verify S3 connectivity.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.s3.Hello]
def hello_s3():
    """
    Lists the S3 buckets in the configured AWS account.
    Use this example to verify that your S3 connectivity is set up correctly.
    """
    s3_client = boto3.client("s3")

    print("Hello, Amazon S3! Let's list your buckets:\n")

    try:
        response = s3_client.list_buckets()
        buckets = response.get("Buckets", list())
        if buckets:
            print(f"Found {len(buckets)} bucket(s):")
            for bucket in buckets:
                print(f"  - {bucket['Name']} (created: {bucket['CreationDate']})")
        else:
            print("No buckets found in your account.")
    except ClientError as err:
        logger.error("Error listing buckets: %s", err.response["Error"]["Message"])
        raise


if __name__ == "__main__":
    hello_s3()
# snippet-end:[python.example_code.s3.Hello]
