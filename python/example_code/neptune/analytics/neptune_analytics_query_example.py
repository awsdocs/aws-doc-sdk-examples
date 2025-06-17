# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import boto3
from botocore.exceptions import ClientError
from botocore.config import Config
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

GRAPH_ID = "<your-graph-id>"

def main():
    config = Config(retries={"total_max_attempts": 1, "mode": "standard"}, read_timeout=None)
    client = boto3.client("neptune-graph", config=config)

    try:
        print("\n--- Running OpenCypher query without parameters ---")
        run_open_cypher_query(client, GRAPH_ID)

        print("\n--- Running OpenCypher query with parameters ---")
        run_open_cypher_query_with_params(client, GRAPH_ID)

        print("\n--- Running OpenCypher explain query ---")
        run_open_cypher_explain_query(client, GRAPH_ID)

    except Exception as e:
        print(f"Unexpected error in main: {e}")

def run_open_cypher_query(client, graph_id):
    """
    Run an OpenCypher query without parameters.
    """
    try:
        resp = client.execute_query(
            GraphIdentifier=graph_id,
            QueryString="MATCH (n {code: 'ANC'}) RETURN n",
            Language="OPEN_CYPHER"
        )
        if 'Payload' in resp:
            result = resp['Payload'].read().decode('utf-8')
            print(result)
        else:
            print("No query result returned.")
    except ClientError as e:
        print(f"NeptuneGraph ClientError: {e.response['Error']['Message']}")
    except Exception as e:
        print(f"Unexpected error: {e}")

def run_open_cypher_query_with_params(client, graph_id):
    """
    Run an OpenCypher query with parameters.
    """
    try:
        parameters = {'code': 'ANC'}
        resp = client.execute_query(
            GraphIdentifier=graph_id,
            QueryString="MATCH (n {code: $code}) RETURN n",
            Language="OPEN_CYPHER",
            Parameters=parameters
        )
        if 'Payload' in resp:
            result = resp['Payload'].read().decode('utf-8')
            print(result)
        else:
            print("No query result returned.")
    except ClientError as e:
        print(f"NeptuneGraph ClientError: {e.response['Error']['Message']}")
    except Exception as e:
        print(f"Unexpected error: {e}")

def run_open_cypher_explain_query(client, graph_id):
    """
    Run an OpenCypher explain query (explainMode = "debug").
    """
    try:
        resp = client.execute_query(
            GraphIdentifier=graph_id,
            QueryString="MATCH (n {code: 'ANC'}) RETURN n",
            Language="OPEN_CYPHER",
            ExplainMode="debug"
        )
        if 'Payload' in resp:
            result = resp['Payload'].read().decode('utf-8')
            print(result)
        else:
            print("No query result returned.")
    except ClientError as e:
        print(f"NeptuneGraph ClientError: {e.response['Error']['Message']}")
    except Exception as e:
        print(f"Unexpected error: {e}")

if __name__ == "__main__":
    main()
# snippet-end:[neptune.python.graph.execute.main]