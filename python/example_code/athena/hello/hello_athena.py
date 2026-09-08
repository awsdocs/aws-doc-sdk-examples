# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Hello Amazon Athena - Lists available workgroups to confirm connectivity.
"""

import boto3
from botocore.exceptions import ClientError


# snippet-start:[python.example_code.athena.Hello]
def hello_athena():
    """
    Lists Amazon Athena workgroups to confirm service connectivity.
    """
    athena_client = boto3.client("athena")

    print("Hello, Amazon Athena! Let's list your workgroups:\n")

    try:
        paginator = athena_client.get_paginator("list_work_groups")
        workgroups = list()
        for page in paginator.paginate():
            workgroups.extend(page.get("WorkGroups", list()))

        if workgroups:
            print(f"Found {len(workgroups)} workgroup(s):")
            for wg in workgroups:
                name = wg.get("Name", "Unknown")
                state = wg.get("State", "Unknown")
                engine = wg.get("EngineVersion", dict())
                engine_version = engine.get(
                    "EffectiveEngineVersion", "Not specified"
                )
                print(f"  - {name} (State: {state}, Engine: {engine_version})")
        else:
            print("No workgroups found.")
    except ClientError as err:
        print(f"Error listing workgroups: {err.response['Error']['Message']}")
        raise


# snippet-end:[python.example_code.athena.Hello]


if __name__ == "__main__":
    hello_athena()
