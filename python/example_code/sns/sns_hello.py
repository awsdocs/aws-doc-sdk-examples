# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Shows how to get started with Amazon Simple Notification Service (Amazon SNS)
    by listing the SNS topics in your account.
"""

import logging
import sys
from typing import Any, List

import boto3
from botocore.exceptions import ClientError

# Configure logging
logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sns.Hello]
def hello_sns(sns_client: Any) -> List[str]:
    """
    Use the AWS SDK for Python (Boto3) to create an Amazon SNS client and list
    the topics in your account. This example uses the default settings specified
    in your shared credentials and config files.

    :param sns_client: A Boto3 Amazon SNS client object. This object wraps
                       the low-level Amazon SNS service API.
    :return: A list of topic ARNs found in the account.
    """
    print("Hello, Amazon SNS! Let's list your topics:\n")

    topic_arns = list()

    try:
        paginator = sns_client.get_paginator("list_topics")
        page_iterator = paginator.paginate()

        for page in page_iterator:
            topics = page.get("Topics", list())
            for topic in topics:
                topic_arn = topic.get("TopicArn", "")
                if topic_arn:
                    topic_arns.append(topic_arn)
                    print(f"  - {topic_arn}")

    except sns_client.exceptions.AuthorizationErrorException:
        logger.error(
            "Access denied. Please verify your IAM policies grant "
            "sns:ListTopics permission."
        )
        raise

    if topic_arns:
        print(f"\nFound {len(topic_arns)} topic(s).")
    else:
        print("No topics found in this region.")

    return topic_arns


# snippet-end:[python.example_code.sns.Hello]


# snippet-start:[python.example_code.sns.ListTopics]
def list_topics(sns_client: Any) -> List[str]:
    """
    Lists all Amazon SNS topics in the current account using pagination.

    :param sns_client: A Boto3 Amazon SNS client object.
    :return: A list of topic ARNs.
    """
    topic_arns = list()

    try:
        paginator = sns_client.get_paginator("list_topics")
        page_iterator = paginator.paginate()

        for page in page_iterator:
            topics = page.get("Topics", list())
            for topic in topics:
                topic_arn = topic.get("TopicArn", "")
                if topic_arn:
                    topic_arns.append(topic_arn)

    except sns_client.exceptions.AuthorizationErrorException:
        logger.error(
            "Access denied. Please verify your IAM policies grant "
            "sns:ListTopics permission."
        )
        raise

    return topic_arns


# snippet-end:[python.example_code.sns.ListTopics]


if __name__ == "__main__":
    sns = boto3.client("sns")
    try:
        hello_sns(sns)
    except ClientError:
        logger.error("Failed to list SNS topics.")
        sys.exit(1)
