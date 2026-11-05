# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to get started with Amazon Inspector by using the AWS SDK for Python
(Boto3) to list the member accounts associated with the current Inspector
administrator account.
"""

# snippet-start:[python.example_code.inspector2.Hello]
import logging

import boto3
from botocore.client import BaseClient
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


def hello_inspector(inspector2_client: BaseClient) -> None:
    """
    Lists the member accounts associated with the current Amazon Inspector
    administrator account.

    The account status is checked first. If that call succeeds, credentials,
    Region, and basic Inspector access are confirmed, so an AccessDeniedException
    from ListMembers means this account is not the Inspector delegated
    administrator for an AWS Organization, rather than an IAM problem.

    :param inspector2_client: A Boto3 Amazon Inspector2 client.
    """
    print("Hello Amazon Inspector!")
    try:
        accounts = inspector2_client.batch_get_account_status().get("accounts", list())
        for account in accounts:
            print(
                f"Account {account.get('accountId')} Inspector status: "
                f"{account.get('state', dict()).get('status', 'UNKNOWN')}"
            )
    except ClientError as err:
        logger.error(
            "Couldn't get the Inspector account status. Here's why: %s: %s",
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        print("Troubleshooting:")
        print("  1. Verify AWS credentials are configured.")
        print("  2. Check IAM permissions for Inspector2.")
        print("  3. Verify you're using a supported Region.")
        raise

    print("Listing member accounts for this Inspector administrator account...")
    try:
        response = inspector2_client.list_members(maxResults=50)
    except ClientError as err:
        if err.response["Error"]["Code"] == "AccessDeniedException":
            print(
                "This account is not an Inspector delegated administrator, so it "
                "has no member accounts to list. To manage member accounts, "
                "designate a delegated administrator for your AWS Organization."
            )
            return
        logger.error(
            "Couldn't list Inspector members. Here's why: %s: %s",
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise

    members = response.get("members", list())
    if not members:
        print("No member accounts found for this Inspector administrator account.")
        return
    print(f"Found {len(members)} member account(s):")
    for member in members:
        print(
            f"  - Account ID: {member.get('accountId')}, "
            f"Status: {member.get('relationshipStatus')}"
        )


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    hello_inspector(boto3.client("inspector2"))
# snippet-end:[python.example_code.inspector2.Hello]
