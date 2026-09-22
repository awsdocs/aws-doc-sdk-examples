# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Demonstrates a "Hello, AWS KMS!" example that lists KMS keys in the
    account using a paginator.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kms.Hello]
def hello_kms():
    """
    Lists the KMS keys in the current account and Region using a paginator.
    This is the simplest possible interaction with AWS KMS.
    """
    kms_client = boto3.client("kms")
    print("Hello, AWS KMS! Let's list your KMS keys:\n")
    try:
        paginator = kms_client.get_paginator("list_keys")
        key_count = 0
        for page in paginator.paginate():
            for key in page.get("Keys", list()):
                print(f"  Key ID: {key['KeyId']}")
                print(f"  Key ARN: {key['KeyArn']}\n")
                key_count += 1
        print(f"Found {key_count} KMS key(s) in the current account and Region.")
    except ClientError as err:
        if err.response["Error"]["Code"] == "KMSInternalException":
            logger.error(
                "KMS internal error while listing keys: %s",
                err.response["Error"]["Message"],
            )
        raise


if __name__ == "__main__":
    hello_kms()
# snippet-end:[python.example_code.kms.Hello]
