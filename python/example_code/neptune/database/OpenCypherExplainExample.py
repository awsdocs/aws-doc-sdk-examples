#  Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
#  SPDX-License-Identifier: Apache-2.0

import boto3
from botocore.config import Config
from botocore.exceptions import ClientError, BotoCoreError

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
# snippet-start:[neptune.python.data.query.opencypher.main]
# Replace with your actual Neptune endpoint URL
NEPTUNE_ENDPOINT = "https://<your-neptune-endpoint>:8182"

def main():
    """
    Entry point: Create Neptune client and execute the OpenCypher EXPLAIN query.
    """
    config = Config(connect_timeout=10, read_timeout=30, retries={'max_attempts': 3})

    neptune_client = boto3.client(
        "neptunedata",
        region_name="us-east-1",
        endpoint_url=NEPTUNE_ENDPOINT,
        config=config
    )

    execute_opencypher_explain_query(neptune_client)


def execute_opencypher_explain_query(neptune_client):
    """
    Executes an OpenCypher EXPLAIN query on Amazon Neptune.

    :param neptune_client: Boto3 Neptunedata client
    """
    try:
        print("Executing OpenCypher EXPLAIN query...")

        response = neptune_client.execute_open_cypher_explain_query(
            openCypherQuery="MATCH (n {code: 'ANC'}) RETURN n",
            explainMode="debug"
        )

        results = response.get("results")
        if results:
            # `results` might be bytes or string, decode if necessary
            if isinstance(results, bytes):
                print("Explain Results:")
                print(results.decode("utf-8"))
            else:
                print("Explain Results:")
                print(results)
        else:
            print("No explain results returned.")
    except ClientError as e:
        print(f"Neptune error: {e.response['Error']['Message']}")
    except BotoCoreError as e:
        print(f"BotoCore error: {str(e)}")
    except Exception as e:
        print(f"Unexpected error: {str(e)}")


if __name__ == "__main__":
    main()
# snippet-end:[neptune.python.data.query.opencypher.main]