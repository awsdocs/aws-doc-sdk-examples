# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Shows how to get started with Amazon SESv2 by listing email identities
    associated with the account.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.sesv2.Hello]
def hello_sesv2(sesv2_client):
    """
    Use the AWS SDK for Python (Boto3) to create an Amazon SESv2 client and
    list the email identities in your account. This example uses the default
    settings specified in your shared credentials and config files.

    :param sesv2_client: A Boto3 SESv2 client object.
    """
    print("Hello, Amazon SESv2! Let's list your email identities:\n")

    identity_count = 0
    next_token = None
    try:
        while True:
            kwargs = {"PageSize": 20}
            if next_token:
                kwargs["NextToken"] = next_token
            response = sesv2_client.list_email_identities(**kwargs)
            identities = response.get("EmailIdentities", [])
            for identity in identities:
                identity_count += 1
                identity_name = identity.get("IdentityName", "Unknown")
                identity_type = identity.get("IdentityType", "Unknown")
                verification_status = identity.get(
                    "VerificationStatus", "Unknown"
                )
                sending_enabled = identity.get("SendingEnabled", False)
                print(
                    f"  Identity: {identity_name}"
                    f"  Type: {identity_type}"
                    f"  Status: {verification_status}"
                    f"  Sending: {'Enabled' if sending_enabled else 'Disabled'}"
                )
            next_token = response.get("NextToken")
            if not next_token:
                break

        if identity_count == 0:
            print(
                "No email identities found. "
                "Use CreateEmailIdentity to add one."
            )
        else:
            print(f"\nFound {identity_count} email identity(ies).")

    except ClientError as err:
        logger.error(
            "Couldn't list email identities. Here's why: %s: %s",
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise


# snippet-end:[python.example_code.sesv2.Hello]


if __name__ == "__main__":
    hello_sesv2(boto3.client("sesv2"))
