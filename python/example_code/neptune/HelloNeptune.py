#  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
#  SPDX-License-Identifier: Apache-2.0

# snippet-start:[neptune.python.hello.main]
import boto3

def describe_db_clusters(neptune_client):
    """
    Describes the Amazon Neptune DB clusters synchronously using a single call.

    :param neptune_client: Boto3 Neptune client
    """
    response = neptune_client.describe_db_clusters()
    for cluster in response.get("DBClusters", []):
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