# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
from botocore.exceptions import ClientError, BotoCoreError

# snippet-start:[neptune.python.graph.create.main]
"""
Running this example.

----------------------------------------------------------------------------------
VPC Networking Requirement:
----------------------------------------------------------------------------------
Amazon Neptune must be accessed from **within the same VPC** as the Neptune cluster.
It does not expose a public endpoint, so this code must be executed from:

  - An **AWS Lambda function** configured to run inside the same VPC
  - An **EC2 instance** or **ECS task** running in the same VPC
  - A connected environment such as a **VPN**, **AWS Direct Connect**, or a **peered VPC**

"""

GRAPH_NAME = "sample-analytics-graph"
REGION = "us-east-1"

def main():
    """
    Main entry point: create NeptuneGraph client and call graph creation.
    """
    # Hypothetical client - boto3 currently doesn't have NeptuneGraph client, so replace with actual client if available
    neptune_graph_client = boto3.client("neptune", region_name=REGION)

    execute_create_graph(neptune_graph_client, GRAPH_NAME)


def execute_create_graph(client, graph_name):
    """
    Creates a new Neptune graph.

    :param client: Boto3 Neptune graph client (hypothetical)
    :param graph_name: Name of the graph to create
    """
    try:
        print("Creating Neptune graph...")

        # Hypothetical method for create_graph, adjust accordingly if you use HTTP API or SDK extensions
        response = client.create_graph(
            GraphName=graph_name,
            ProvisionedMemory=16  # Example parameter, adjust if API differs
        )

        created_graph_name = response.get("Name")
        graph_arn = response.get("Arn")
        graph_endpoint = response.get("Endpoint")

        print("Graph created successfully!")
        print(f"Graph Name: {created_graph_name}")
        print(f"Graph ARN: {graph_arn}")
        print(f"Graph Endpoint: {graph_endpoint}")

    except ClientError as e:
        print(f"Failed to create graph: {e.response['Error']['Message']}")
    except BotoCoreError as e:
        print(f"Failed to create graph: {str(e)}")
    except Exception as e:
        print(f"Unexpected error: {str(e)}")


if __name__ == "__main__":
    main()
# snippet-end:[neptune.python.graph.create.main]