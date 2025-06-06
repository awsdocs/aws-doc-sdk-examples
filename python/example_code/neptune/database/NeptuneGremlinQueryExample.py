# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import boto3
from botocore.config import Config
from botocore.exceptions import BotoCoreError, ClientError

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
# snippet-start:[neptune.python.data.query.gremlin.main]
# Replace this with your actual Neptune endpoint
NEPTUNE_ENDPOINT = "https://[Specify Endpoint]:8182"

def main():
    """
    Entry point of the program. Initializes the Neptune client and executes the Gremlin query.
    """
    config = Config(connect_timeout=10, read_timeout=30, retries={'max_attempts': 3})

    neptune_client = boto3.client(
        "neptunedata",
        region_name="us-east-1",
        endpoint_url=NEPTUNE_ENDPOINT,
        config=config
    )

    execute_gremlin_query(neptune_client)


def execute_gremlin_query(neptune_client):
    """
    Executes a Gremlin query against an Amazon Neptune database.

    :param neptune_client: Boto3 Neptunedata client
    """
    try:
        print("Querying Neptune...")

        response = neptune_client.execute_gremlin_query(
            gremlinQuery="g.V().has('code', 'ANC')"
        )

        print("Full Response:")
        print(response)

        result = response.get("result")
        if result:
            print("Query Result:")
            print(result)
        else:
            print("No result returned from the query.")
    except ClientError as e:
        print(f"Error calling Neptune: {e.response['Error']['Message']}")
    except BotoCoreError as e:
        print(f"BotoCore error: {str(e)}")
    except Exception as e:
        print(f"Unexpected error: {str(e)}")


if __name__ == "__main__":
    main()
# snippet-end:[neptune.python.data.query.gremlin.main]