# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Hello Amazon Kinesis Data Streams — minimal example to verify connectivity.

This example lists Kinesis data streams in your account and displays their names.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


def hello_kinesis():
    """
    Lists Kinesis data streams with a limit of 10 and displays the results.
    Use this example to verify that your environment is configured correctly.
    """
    kinesis_client = boto3.client("kinesis")
    print("Hello, Amazon Kinesis! Let's list some of your streams:\n")
    try:
        response = kinesis_client.list_streams(Limit=10)
        stream_names = response.get("StreamNames", list())
        if stream_names:
            for name in stream_names:
                print(f"  Stream: {name}")
            print(f"\nFound {len(stream_names)} stream(s).")
        else:
            print("No streams found in your account.")
    except ClientError as err:
        if err.response["Error"]["Code"] == "LimitExceededException":
            logger.error("Request rate too high. Retry after a brief delay.")
        else:
            logger.error(
                "Error listing streams: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
        raise


if __name__ == "__main__":
    hello_kinesis()
