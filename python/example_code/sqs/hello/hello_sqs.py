# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Shows how to get started with Amazon Simple Queue Service (Amazon SQS)
    by listing the SQS queues in your account.
"""

import logging

import boto3
from botocore.exceptions import ClientError

# Configure logging
logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sqs.Hello]
def hello_sqs(sqs_client):
    """
    Use the AWS SDK for Python (Boto3) to create an Amazon SQS client and list
    the queues in your account. This example uses the default settings specified
    in your shared credentials and config files.

    :param sqs_client: A Boto3 Amazon SQS client object.
    """
    print("Hello, Amazon SQS! Let's list your queues:\n")

    try:
        response = sqs_client.list_queues()
        queue_urls = response.get("QueueUrls", list())

        if queue_urls:
            print(f"Found {len(queue_urls)} queue(s):")
            for url in queue_urls:
                print(f"  {url}")
        else:
            print("No queues found in this account/region.")

    except ClientError as error:
        if error.response["Error"]["Code"] == "RequestThrottled":
            logger.error(
                "Request was throttled. Please retry after a brief delay. %s",
                error.response["Error"]["Message"],
            )
        logger.error("Couldn't list SQS queues. Here's why: %s", error)
        raise

    print()


# snippet-end:[python.example_code.sqs.Hello]


if __name__ == "__main__":
    hello_sqs(boto3.client("sqs"))
