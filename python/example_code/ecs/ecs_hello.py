# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Hello Amazon ECS — a minimal example that lists ECS clusters to verify connectivity.

Usage:
    python ecs_hello.py
"""

# snippet-start:[python.example_code.ecs.Hello]
import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


def hello_ecs():
    """
    Lists the Amazon ECS clusters in the current AWS region.
    Uses a paginator to handle large numbers of clusters.
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
            print("No ECS clusters found in the current region.")
    except ClientError as err:
        logger.error(
            "Error listing ECS clusters: %s: %s",
            err.response["Error"]["Code"],
            err.response["Error"]["Message"],
        )
        raise


if __name__ == "__main__":
    hello_ecs()
# snippet-end:[python.example_code.ecs.Hello]
