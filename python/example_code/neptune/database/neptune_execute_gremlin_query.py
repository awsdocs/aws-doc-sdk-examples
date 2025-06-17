# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start: [neptune.python.data.query.gremlin.profile.main]
import boto3
import json
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
# Customize this with your Neptune endpoint
NEPTUNE_ENDPOINT = "https://<your-neptune-endpoint>:8182"

def execute_gremlin_profile_query(client):
    """
    Executes a Gremlin query using the provided Neptune Data client.
    """
    print("Executing Gremlin PROFILE query...")

    try:
        response = client.execute_gremlin_query(
            gremlinQueyr="g.V().has('code', 'ANC')"
        )

        print("Response is:")
        print(response['result'])

    except ClientError as e:
        print(f"Neptune error: {e.response['Error']['Message']}")
    except BotoCoreError as e:
        print(f"Unexpected Boto3 error: {str(e)}")
    except Exception as e:
        print(f"Unexpected error: {str(e)}")

def main():
    """
    Main entry point: creates the Neptune client and runs the profile query.
    """

    # * To prevent unneccesary retries please set the total_max_attempts to 1
    # * To prevent a read timeout on the client when a query runs longer than 60 seconds set the read_timeout to None
    config = Config(retries={"total_max_attempts": 1, "mode": "standard"}, read_timeout=None)

    neptune_client = boto3.client(
        "neptunedata",
        endpoint_url=NEPTUNE_ENDPOINT,
        config=config
    )

    execute_gremlin_profile_query(neptune_client)


if __name__ == "__main__":
    main()

# snippet-end: [neptune.python.data.query.gremlin.profile.main]