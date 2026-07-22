# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Hello Kinesis - Lists existing Kinesis data streams as a connectivity check.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kinesis.Hello]
def hello_kinesis():
    """
    Lists Kinesis data streams in the current account/region.
    This serves as a simple connectivity check for the Kinesis service.
    """
    kinesis_client = boto3.client("kinesis")

    print("Hello, Amazon Kinesis! Let's list your data streams:\n")
    try:
        paginator = kinesis_client.get_paginator("list_streams")
        stream_names = list()
        for page in paginator.paginate():
            for summary in page.get("StreamSummaries", list()):
                stream_names.append(summary["StreamName"])

        if stream_names:
            print(f"Found {len(stream_names)} stream(s):")
            for name in stream_names:
                print(f"  - {name}")
        else:
            print("No Kinesis data streams found in this account/region.")
    except ClientError as error:
        logger.error(
            "Error listing streams: %s: %s",
            error.response["Error"]["Code"],
            error.response["Error"]["Message"],
        )
        raise


# snippet-end:[python.example_code.kinesis.Hello]


if __name__ == "__main__":
    hello_kinesis()