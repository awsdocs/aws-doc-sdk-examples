# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Bedrock Flow Runner for Playlist Generation

This module provides functionality to execute an existing Amazon Bedrock flow that generates
music playlists based on user-specified genre and number of songs. It handles flow invocation,
response streaming, and error handling.

The module interacts with a pre-configured Bedrock flow that expects:
    - Input: JSON document containing genre and number of songs
    - Output: Generated playlist as formatted text

"""

import logging
from botocore.exceptions import ClientError

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.bedrock-agent-runtime.flow_invoke_flow]  
def invoke_flow(client, flow_id, flow_alias_id, input_data):
    """
    Invoke an Amazon Bedrock flow and handle the response stream.

    Args:
        client: Boto3 client for Amazon Bedrock agent runtime.
        flow_id: The ID of the flow to invoke.
        flow_alias_id: The alias ID of the flow.
        input_data: Input data for the flow.

    Returns:
        Dict containing flow_complete status, input_required info, and execution_id
    """

    response = None
    request_params = None

    request_params = {
            "flowIdentifier": flow_id,
            "flowAliasIdentifier": flow_alias_id,
            "inputs": [input_data],
            "enableTrace": True
        }


    response = client.invoke_flow(**request_params)

    flow_status = ""

    # Process the streaming response
    for event in response['responseStream']:

        # Check if flow is complete.
        if 'flowCompletionEvent' in event:
            flow_status = event['flowCompletionEvent']['completionReason']

        # Print the model output.
        elif 'flowOutputEvent' in event:
            logger.info(event['flowOutputEvent']['content']['document'])

        # Log trace events.
        elif 'flowTraceEvent' in event:
            logger.info("Flow trace:  %s", event['flowTraceEvent'])

    return {
        "flow_status": flow_status
    }
# snippet-end:[python.example_code.bedrock-agent-runtime.flow_invoke_flow]  

# snippet-start:[python.example_code.bedrock-agent-runtime.run_playlist_flow]  

def run_playlist_flow(bedrock_agent_client, flow_id, flow_alias_id):
    """
    Runs the playlist generator flow.

    Args:
        bedrock_agent_client: Boto3 client for Amazon Bedrock agent runtime.
        flow_id: The ID of the flow to run.
        flow_alias_id: The alias ID of the flow.

    """


    print ("Welcome to the playlist generator flow.")
    # Get the initial prompt from the user.
    genre = input("Enter genre: ")
    number_of_songs = int(input("Enter number of songs: "))


    # Use prompt to create input data for the input node.
    flow_input_data = {
        "content": {
            "document": {
                "genre" : genre,
                "number" : number_of_songs
            }
        },
        "nodeName": "FlowInput",
        "nodeOutputName": "document"
    }

    try:

        result = invoke_flow(
                bedrock_agent_client, flow_id, flow_alias_id, flow_input_data)

        status = result['flow_status']
  
        if status == "SUCCESS":
                # The flow completed successfully.
                logger.info("The flow %s successfully completed.", flow_id)
        else:
            logger.warning("Flow status: %s",status)

    except ClientError as e:
        print(f"Client error: {str(e)}")
        logger.error("Client error: %s", {str(e)})
        raise

    except Exception as e:
        logger.error("An error occurred: %s", {str(e)})
        logger.error("Error type: %s", {type(e)})
        raise

# snippet-end:[python.example_code.bedrock-agent-runtime.run_playlist_flow]  


