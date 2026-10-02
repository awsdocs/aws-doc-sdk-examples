# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Hello AWS Batch example.

Lists all compute environments in the account/region.
"""

import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.batch.Hello]
def hello_batch() -> None:
    """
    Lists compute environments using the AWS Batch service.
    """
    batch_client = boto3.client("batch")
    print("Hello, AWS Batch! Let's list your compute environments:\n")
    try:
        response = batch_client.describe_compute_environments()
        environments = response.get("computeEnvironments", list())
        if environments:
            print(f"Found {len(environments)} compute environment(s):")
            for env in environments:
                print(
                    f"  - Name: {env['computeEnvironmentName']} | "
                    f"Type: {env.get('type', 'N/A')} | "
                    f"State: {env.get('state', 'N/A')} | "
                    f"Status: {env.get('status', 'N/A')}"
                )
        else:
            print("No compute environments found in your account.")
    except ClientError as err:
        if err.response["Error"]["Code"] == "ClientException":
            logger.error(
                "Client error listing compute environments: %s",
                err.response["Error"]["Message"],
            )
        raise

    print("\nHello example complete.")
# snippet-end:[python.example_code.batch.Hello]


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    hello_batch()
