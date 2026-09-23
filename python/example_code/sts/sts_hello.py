# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Hello AWS STS — A minimal, standalone runnable program that demonstrates
basic connectivity to the AWS Security Token Service.

This example calls GetCallerIdentity to verify that the caller has valid AWS
credentials and displays the account ID, ARN, and user ID.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sts.Hello]
def hello_sts():
    """
    Use the AWS SDK for Python (Boto3) to create an STS client and call
    GetCallerIdentity to verify connectivity. Displays the account ID,
    user/role ARN, and user ID of the caller.

    This example uses the default credential provider chain for authentication.
    """
    try:
        sts_client = boto3.client("sts")
        response = sts_client.get_caller_identity()

        print("\nHello, AWS STS! Let's verify your identity.\n")
        print("Your caller identity:")
        print(f"  Account: {response['Account']}")
        print(f"  ARN:     {response['Arn']}")
        print(f"  User ID: {response['UserId']}")
        print("\nSuccessfully connected to AWS STS!")

    except ClientError as error:
        if error.response["Error"]["Code"] == "ExpiredTokenException":
            logger.error(
                "Your temporary credentials have expired. Please obtain fresh credentials."
            )
        else:
            logger.error("Unexpected error: %s", error)
        raise


# snippet-end:[python.example_code.sts.Hello]


if __name__ == "__main__":
    hello_sts()
