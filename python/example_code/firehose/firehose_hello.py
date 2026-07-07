# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Hello Amazon Data Firehose - Lists delivery streams in your account.
"""

# snippet-start:[python.example_code.firehose.Hello]
import boto3
from botocore.exceptions import ClientError


def hello_firehose(firehose_client):
    """
    Use the AWS SDK for Python (Boto3) to create an Amazon Data Firehose client
    and list delivery streams in your account. This example uses the default
    settings specified in your shared credentials and config files.

    :param firehose_client: A Boto3 Amazon Data Firehose client object.
    """
    print("Hello, Amazon Data Firehose! Let's list your delivery streams:\n")
    try:
        response = firehose_client.list_delivery_streams(Limit=10)
        stream_names = response.get("DeliveryStreamNames", list())
        if stream_names:
            print(f"  Found {len(stream_names)} delivery stream(s):")
            for name in stream_names:
                print(f"    - {name}")
            if response.get("HasMoreDeliveryStreams", False):
                print(
                    "\n  Note: There are additional delivery streams not shown."
                )
        else:
            print("  No delivery streams found in the current region.")
    except ClientError as err:
        print(
            f"  Couldn't list delivery streams. Error: "
            f"{err.response['Error']['Code']}: {err.response['Error']['Message']}"
        )
        raise


if __name__ == "__main__":
    hello_firehose(boto3.client("firehose"))
# snippet-end:[python.example_code.firehose.Hello]
