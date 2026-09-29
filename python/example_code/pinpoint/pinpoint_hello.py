# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Hello Amazon Pinpoint example — a minimal standalone program that verifies the
caller can interact with Amazon Pinpoint by listing existing applications.

Purpose: Demonstrates using the AWS SDK for Python (Boto3) to call the
Amazon Pinpoint GetApps API.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.pinpoint.Hello]
def hello_pinpoint() -> None:
    """
    Lists Amazon Pinpoint applications to verify connectivity.
    Calls GetApps with a small page size and prints results.
    """
    pinpoint_client = boto3.client("pinpoint")
    print("Hello, Amazon Pinpoint! Let's list your applications.\n")
    try:
        response = pinpoint_client.get_apps(PageSize="5")
        apps = response.get("ApplicationsResponse", dict()).get("Item", list())
        if apps:
            print(f"Found {len(apps)} application(s):")
            for app in apps:
                print(f"  - {app.get('Name')} (ID: {app.get('Id')})")
        else:
            print("No applications found. Create one to get started!")
    except ClientError as err:
        error_code = err.response["Error"]["Code"]
        if error_code == "BadRequestException":
            logger.error("Bad request: %s", err.response["Error"]["Message"])
        elif error_code == "InternalServerErrorException":
            logger.error("Internal server error: %s", err.response["Error"]["Message"])
        raise


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    hello_pinpoint()
# snippet-end:[python.example_code.pinpoint.Hello]
