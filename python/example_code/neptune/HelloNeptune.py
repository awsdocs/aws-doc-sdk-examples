# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[neptune.python.hello.main]
import boto3

def describe_db_clusters(neptune_client):
    """
    Describes the Amazon Neptune DB clusters using a paginator to handle multiple pages.

    :param neptune_client: Boto3 Neptune client
    """
    paginator = neptune_client.get_paginator("describe_db_clusters")
    for page in paginator.paginate():
        for cluster in page.get("DBClusters", []):
            print(f"Cluster Identifier: {cluster['DBClusterIdentifier']}")
            print(f"Status: {cluster['Status']}")

def main():
    """
    Main entry point: creates the Neptune client and calls the describe operation.
    """
    neptune_client = boto3.client("neptune", region_name="us-east-1")
    try:
        describe_db_clusters(neptune_client)
    except Exception as e:
        print(f"Error describing DB clusters: {str(e)}")

if __name__ == "__main__":
    main()

# snippet-end:[neptune.python.hello.main]