# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to get started with Amazon Inspector by checking the current account status.
"""

import logging
import boto3
from botocore.exceptions import ClientError

from inspector_wrapper import InspectorWrapper

# snippet-start:[python.example_code.inspector.Hello]
logger = logging.getLogger(__name__)


def hello_inspector(inspector_wrapper: InspectorWrapper):
    """
    Use the AWS SDK for Python (Boto3) to check the current account status for Amazon Inspector.
    This function is intended to get you started with Amazon Inspector.

    :param inspector_wrapper: An InspectorWrapper object that wraps Inspector actions.
    """
    print("Hello, Amazon Inspector! Let's check your account status.")
    try:
        # Get the current account status
        response = inspector_wrapper.get_account_status()

        if "accounts" in response and response["accounts"]:
            account = response["accounts"][0]
            account_id = account.get("accountId", "Unknown")

            print(f"\nAccount ID: {account_id}")

            # Display overall status
            if "state" in account:
                status = account["state"].get("status", "Unknown")
                print(f"Inspector Status: {status}")

            # Display resource-specific status
            if "resourceState" in account:
                resource_state = account["resourceState"]
                print("\nResource Scanning Status:")

                for resource_type, state in resource_state.items():
                    resource_status = state.get("status", "Unknown")
                    print(f"  {resource_type.upper()}: {resource_status}")

            print(
                "\nAmazon Inspector is ready to help you identify security vulnerabilities!"
            )

        else:
            print("No account information available.")

    except ClientError as e:
        error_code = e.response["Error"]["Code"]
        if error_code == "AccessDeniedException":
            print(
                "Access denied. Please ensure you have the necessary permissions to use Amazon Inspector."
            )
        else:
            print(f"Error checking Inspector status: {e}")


if __name__ == "__main__":
    hello_inspector(InspectorWrapper.from_client())
# snippet-end:[python.example_code.inspector.Hello]
