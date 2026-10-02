# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Kinesis Data Streams
to list existing streams. This is a "Hello, Kinesis!" example that verifies
connectivity to the service.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kinesis.Hello]
def hello_kinesis(kinesis_client):
    """
    Use the AWS SDK for Python (Boto3) to create a Kinesis client and list
    the streams in your account. This example uses the default settings
    specified in your shared credentials and config files.

    :param kinesis_client: A Boto3 Kinesis client.
    """
    print("Hello, Amazon Kinesis! Let's list your data streams:\n")
    try:
        response = kinesis_client.list_streams(Limit=10)
        stream_names = response.get("StreamNames", list())
        has_more = response.get("HasMoreStreams", False)
        summaries = response.get("StreamSummaries", list())
        # Build a lookup keyed by name; the API does not guarantee that
        # StreamNames and StreamSummaries are parallel, same-order arrays.
        summary_by_name = {s["StreamName"]: s for s in summaries}
        if stream_names:
            print(f"Found {len(stream_names)} Kinesis data stream(s):")
            for name in stream_names:
                info = ""
                summary = summary_by_name.get(name)
                if summary is not None:
                    status = summary.get("StreamStatus", "UNKNOWN")
                    mode = summary.get("StreamModeDetails", dict()).get(
                        "StreamMode", "UNKNOWN"
                    )
                    info = f" ({status}, {mode})"
                print(f"  - {name}{info}")
            if has_more:
                print("\n  Additional streams exist beyond this page.")
        else:
            print("No Kinesis data streams found in the current region.")
    except ClientError as err:
        logger.error(
            "Couldn't list Kinesis streams. Error: %s: %s",
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise


if __name__ == "__main__":
    hello_kinesis(boto3.client("kinesis"))
# snippet-end:[python.example_code.kinesis.Hello]
