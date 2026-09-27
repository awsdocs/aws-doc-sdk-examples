# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Hello Athena example — verifies connectivity to the Amazon Athena service
by listing workgroups in the account.

Usage:
    python athena_hello.py
"""

import logging

import boto3
from botocore.client import BaseClient
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.athena.Hello]
def hello_athena(athena_client: BaseClient) -> None:
    """
    Lists Amazon Athena workgroups. Demonstrates basic connectivity to the
    Athena service.

    :param athena_client: A Boto3 Athena client.
    """
    print("Listing Athena workgroups...\n")
    try:
        workgroup_count = 0
        next_token = None
        while True:
            kwargs = dict()
            if next_token is not None:
                kwargs["NextToken"] = next_token
            response = athena_client.list_work_groups(**kwargs)
            for wg in response.get("WorkGroups", list()):
                name = wg.get("Name", "Unknown")
                state = wg.get("State", "Unknown")
                print(f"  Workgroup: {name} (State: {state})")
                workgroup_count += 1
            next_token = response.get("NextToken", None)
            if next_token is None:
                break
        print(f"\nFound {workgroup_count} workgroup(s).")
    except ClientError as err:
        logger.error(
            "Error listing Athena workgroups. %s: %s",
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    hello_athena(boto3.client("athena"))
# snippet-end:[python.example_code.athena.Hello]
