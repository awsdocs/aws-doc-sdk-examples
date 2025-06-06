#  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
#  SPDX-License-Identifier: Apache-2.0

import boto3
from botocore.exceptions import ClientError

# snippet-start:[neptune.python.graph.execute.main]
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

NEPTUNE_ANALYTICS_ENDPOINT = "https://<your-neptune-analytics-endpoint>:8182"
GRAPH_ID = "<your-graph-id>"
REGION = "us-east-1"

def main():
    # Build the boto3 client for neptune-graph with endpoint override
    client = boto3.client(
        "neptune-graph",
        region_name=REGION,
        endpoint_url=NEPTUNE_ANALYTICS_ENDPOINT
    )

    try:
        execute_gremlin_profile_query(client, GRAPH_ID)
    except Exception as e:
        print(f"Unexpected error in main: {e}")

def execute_gremlin_profile_query(client, graph_id):
    """
    Executes a Gremlin or OpenCypher query on Neptune Analytics graph.

    Args:
        client (boto3.client): The NeptuneGraph client.
        graph_id (str): The graph identifier.
    """
    print("Running openCypher query on Neptune Analytics...")

    try:
        response = client.execute_query(
            GraphIdentifier=graph_id,
            QueryString="MATCH (n {code: 'ANC'}) RETURN n",
            Language="OPEN_CYPHER"
        )

        # The response 'Payload' may contain the query results as a streaming bytes object
        # Convert to string and print
        if 'Payload' in response:
            result = response['Payload'].read().decode('utf-8')
            print("Query Result:")
            print(result)
        else:
            print("No query result returned.")

    except ClientError as e:
        print(f"NeptuneGraph error: {e.response['Error']['Message']}")
    except Exception as e:
        print(f"Unexpected error: {e}")

if __name__ == "__main__":
    main()
# snippet-end:[neptune.python.graph.execute.main]