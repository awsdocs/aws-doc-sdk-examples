# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Hello Amazon ECS - Verifies ECS connectivity by listing clusters.
"""

# snippet-start:[python.example_code.ecs.Hello]
import boto3
from botocore.exceptions import ClientError


def hello_ecs():
    """
    Lists all ECS clusters in the account to verify connectivity.
    """
    ecs_client = boto3.client("ecs")

    try:
        paginator = ecs_client.get_paginator("list_clusters")
        cluster_arns = list()
        for page in paginator.paginate():
            cluster_arns.extend(page.get("clusterArns", list()))

        if cluster_arns:
            print(f"Found {len(cluster_arns)} ECS cluster(s):")
            for arn in cluster_arns:
                print(f"  - {arn}")
        else:
            print("No ECS clusters found in this account/region.")

    except ClientError as err:
        print(f"Error listing ECS clusters: {err.response['Error']['Message']}")
        raise


if __name__ == "__main__":
    hello_ecs()
# snippet-end:[python.example_code.ecs.Hello]
