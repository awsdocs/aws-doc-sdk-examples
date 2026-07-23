# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Data Firehose Hello example - Lists delivery streams in your account.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.firehose.Hello]
def hello_firehose():
    """
    Lists Amazon Data Firehose delivery streams in your account.
    This is a basic example to demonstrate connecting to the Firehose service.
    """
    firehose_client = boto3.client("firehose")

    print("-" * 80)
    print("Hello, Amazon Data Firehose! Let's list your delivery streams.")
    print("-" * 80)

    try:
        response = firehose_client.list_delivery_streams(Limit=20)
        stream_names = response.get("DeliveryStreamNames", list())
        has_more = response.get("HasMoreDeliveryStreams", False)

        if stream_names:
            print(f"Found {len(stream_names)} delivery stream(s):")
            for name in stream_names:
                print(f"  - {name}")
        else:
            print("No delivery streams found in this account/region.")

        print(f"Has more streams: {has_more}")
    except ClientError as error:
        logger.error("Error listing delivery streams: %s", error)
        raise

    print("-" * 80)


# snippet-end:[python.example_code.firehose.Hello]


if __name__ == "__main__":
    hello_firehose()