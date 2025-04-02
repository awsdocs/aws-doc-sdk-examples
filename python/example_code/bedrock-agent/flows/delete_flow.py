# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
"""
This module provides functionality delete an Amazon Bedrock flow.
"""

import boto3
from botocore.exceptions import ClientError

def delete_flow(client, flow_id):
    """
    Deletes a Bedrock flow.

    Args:
    client: bedrock agent boto3 client.
        flow_id (str): The identifier of the flow that you want to delete.

    Returns:
        dict: Flow information if successful, None if an error occurs
    """
    try:

        # Call DeleteFlow operation
        response = client.delete_flow(
            flowIdentifier=flow_id
        )

        print(f"Flow {flow_id} deleted successfully")
        return response

    except ClientError as e:
        if e.response['Error']['Code'] == 'ResourceNotFoundException':
            print(f"Flow with ID {flow_id} not found")
        elif e.response['Error']['Code'] == 'AccessDeniedException':
            print("You don't have permission to delete this flow")
        else:
            print(f"Error deleting flow: {str(e)}")
        raise
    except Exception as e:
        print(f"Unexpected error: {str(e)}")
        raise


def main():
    """
     Entry point that initializes AWS client and executes flow deletion.
     Uses default AWS profile for authentication.
     """

    # Replace with your flow ID
    flow_id = "FLOW_ID"

    session = boto3.Session(profile_name='default')
    bedrock_agent_client = session.client('bedrock-agent')
    # Get and display flow details
    delete_flow(bedrock_agent_client,flow_id)




if __name__ == "__main__":
    main()
