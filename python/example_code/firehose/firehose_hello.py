# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Shows how to get started with Amazon Data Firehose by listing delivery
    streams in your account using the ListDeliveryStreams API.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.firehose.Hello]
def hello_firehose(firehose_client):
    """
    Use the AWS SDK for Python (Boto3) to create an Amazon Data Firehose
    client and list the delivery streams in your account.
    This example uses the default settings specified in your shared credentials
    and config files.

    :param firehose_client: A Boto3 Amazon Data Firehose client object.
    """
    print("Hello, Amazon Data Firehose! Here are your delivery streams:\n")
    try:
        response = firehose_client.list_delivery_streams(Limit=10)
        stream_names = response.get("DeliveryStreamNames", list())
        has_more = response.get("HasMoreDeliveryStreams", False)

        if stream_names:
            for name in stream_names:
                print(f"  - {name}")
        else:
            print("  No delivery streams found in this account/region.")

        if has_more:
            print(
                "\n  (There are additional delivery streams not shown. "
                "Use pagination to list them all.)"
            )
    except ClientError as error:
        logger.error(
            "Couldn't list delivery streams. Here's why: %s: %s",
            error.response["Error"]["Code"],
            error.response["Error"]["Message"],
        )
        raise


# snippet-end:[python.example_code.firehose.Hello]


if __name__ == "__main__":
    hello_firehose(boto3.client("firehose"))
