# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Hello Amazon Inspector example.

This standalone program demonstrates how to list account permissions for
Amazon Inspector using the AWS SDK for Python (Boto3).
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.inspector2.Hello]
def hello_inspector2() -> None:
    """
    Lists the permissions an account has to configure Amazon Inspector.

    This is a Hello World example that demonstrates how to get started with
    the Amazon Inspector2 service using the AWS SDK for Python (Boto3).
    """
    inspector2_client = boto3.client("inspector2")

    print("Hello Amazon Inspector!")
    print("Listing account permissions for Amazon Inspector...\n")

    try:
        permissions = list()
        paginator = inspector2_client.get_paginator("list_account_permissions")
        page_iterator = paginator.paginate()
        for page in page_iterator:
            permissions.extend(page.get("permissions", list()))

        if permissions:
            print("Account Permissions:")
            for perm in permissions:
                operation = perm.get("operation", "UNKNOWN")
                service = perm.get("service", "UNKNOWN")
                print(f"  - Operation: {operation}, Service: {service}")
            print(f"\nFound {len(permissions)} permissions.")
        else:
            print("No permissions found. Amazon Inspector may not be enabled.")

    except ClientError as err:
        if err.response["Error"]["Code"] == "AccessDeniedException":
            logger.error(
                "Access denied listing account permissions. "
                "Verify your IAM policies grant inspector2:ListAccountPermissions."
            )
        raise

    print("\nHello Inspector example completed successfully.")


# snippet-end:[python.example_code.inspector2.Hello]


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    hello_inspector2()
