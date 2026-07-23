# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Hello Amazon Data Firehose - Lists delivery streams.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.firehose.Hello]
def hello_firehose():
    """
    Lists Amazon Data Firehose delivery streams in the current account.
    This is a simple demonstration of connecting to the Firehose service.
    """
    firehose_client = boto3.client("firehose")

    print("\n--- Amazon Data Firehose Hello ---")
    try:
        response = firehose_client.list_delivery_streams(DeliveryStreamType="DirectPut")
        stream_names = response.get("DeliveryStreamNames", list())
        if stream_names:
            print(f"Found {len(stream_names)} delivery stream(s):")
            for name in stream_names:
                print(f"  - {name}")
        else:
            print("No delivery streams found in this account/region.")
    except ClientError as err:
        logger.error(
            "Error listing delivery streams: %s: %s",
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise


# snippet-end:[python.example_code.firehose.Hello]


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    hello_firehose()
