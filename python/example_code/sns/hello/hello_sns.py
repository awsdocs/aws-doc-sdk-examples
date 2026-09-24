# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Shows how to get started with Amazon Simple Notification Service (Amazon SNS)
    by listing the SNS topics in your account.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sns.Hello]
def hello_sns(sns_client):
    """
    Use the AWS SDK for Python (Boto3) to create an Amazon SNS client and list
    the topics in your account. This example uses the default settings specified
    in your shared credentials and config files.

    :param sns_client: A Boto3 Amazon SNS client.
    :return: The list of topic dictionaries found.
    """
    print("Hello, Amazon SNS! Let's list your topics:\n")
    try:
        response = sns_client.list_topics()
        topics = response.get("Topics", list())
        if topics:
            for topic in topics:
                print(f"  - {topic['TopicArn']}")
            if response.get("NextToken", None):
                print("\n  (Additional topics exist but are not shown.)")
        else:
            print("  You have no SNS topics in this region.")
    except ClientError as err:
        if err.response["Error"]["Code"] == "AuthorizationError":
            logger.error(
                "Authorization error: Your credentials do not have permission "
                "to list SNS topics. Verify your IAM permissions."
            )
        logger.error("Couldn't list SNS topics. Error: %s", err)
        raise
    else:
        return topics


if __name__ == "__main__":
    hello_sns(boto3.client("sns"))
# snippet-end:[python.example_code.sns.Hello]
