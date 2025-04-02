# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Bedrock Flow Generator for Music Playlists

This module implements functionality to create, run, and manage an Amazon Bedrock flow
that generates music playlists. The flow consists of three nodes:
- Input node: Accepts genre and number of songs as input
- Prompt node: Uses a foundation model to generate a playlist
- Output node: Returns the generated playlist

The flow takes a music genre and desired number of songs as input and returns
a generated playlist using the specified foundation model.

The script will:
1. Create necessary IAM roles and permissions
2. Create and configure the Bedrock flow
3. Run the flow with user-provided genre and song count
4. Optionally delete the flow and cleanup resources

"""

# snippet-start:[python.example_code.bedrock-agent-runtine.Scenario_GettingStartedBedrockFlows]

from datetime import datetime
import logging
import boto3

from botocore.exceptions import ClientError

from roles import create_flow_role, delete_flow_role, update_role_policy
from flow import create_flow, prepare_flow, delete_flow
from run_flow import run_playlist_flow
from flow_version import create_flow_version, delete_flow_version
from flow_alias import create_flow_alias, delete_flow_alias

logging.basicConfig(
    level=logging.INFO
)
logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.bedrock-agent-runtime.create_input_node]


def create_input_node(name):
    """
    Creates an input node configuration for a Bedrock flow.

    The input node serves as the entry point for the flow and defines
    the initial document structure that will be passed to subsequent nodes.

    Args:
        name (str): The name of the input node.

    Returns:
        dict: The input node configuration.

    """
    return {
        "type": "Input",
        "name": name,
        "outputs": [
            {
                "name": "document",
                "type": "Object"
            }
        ]
    }

# snippet-end:[python.example_code.bedrock-agent-runtime.create_input_node]

# snippet-start:[python.example_code.bedrock-agent-runtime.create_prompt_node]


def create_prompt_node(name, model_id):
    """
    Creates a prompt node configuration for a Bedrock flow that generates music playlists.

    The prompt node defines an inline prompt template that creates a music playlist based on
    a specified genre and number of songs. The prompt uses two variables that are mapped from
    the input JSON object:
    - {{genre}}: The genre of music to create a playlist for
    - {{number}}: The number of songs to include in the playlist

    Args:
        name (str): The name of the prompt node.
        model_id (str): The identifier of the foundation model to use for the prompt.

    Returns:
        dict: The prompt node.

    """

    return {
        "type": "Prompt",
        "name": name,
        "configuration": {
            "prompt": {
                "sourceConfiguration": {
                    "inline": {
                        "modelId": model_id,
                        "templateType": "TEXT",
                        "inferenceConfiguration": {
                            "text": {
                                "temperature": 0.8
                            }
                        },
                        "templateConfiguration": {
                            "text": {
                                "text": "Make me a {{genre}} playlist consisting of the following number of songs: {{number}}."
                            }
                        }
                    }
                }
            }
        },
        "inputs": [
            {
                "name": "genre",
                "type": "String",
                "expression": "$.data.genre"
            },
            {
                "name": "number",
                "type": "Number",
                "expression": "$.data.number"
            }
        ],
        "outputs": [
            {
                "name": "modelCompletion",
                "type": "String"
            }
        ]
    }

# snippet-end:[python.example_code.bedrock-agent-runtime.create_prompt_node]

# snippet-start:[python.example_code.bedrock-agent-runtime.create_output_node]


def create_output_node(name):
    """
    Creates an output node configuration for a Bedrock flow.

    The output node validates that the output from the last node is a string
    and returns it unmodified. The input name must be "document".

    Args:
        name (str): The name of the output node.

    Returns:
        dict: The output node configuration containing the output node:

    """

    return {
        "type": "Output",
        "name": name,
        "inputs": [
            {
                "name": "document",
                "type": "String",
                "expression": "$.data"
            }
        ]
    }

# snippet-end:[python.example_code.bedrock-agent-runtime.create_output_node]


# snippet-start:[python.example_code.bedrock-agent-runtime.create_playlist_flow]

def create_playlist_flow(client, flow_name, flow_description, role_arn, prompt_model_id):
    """
    Creates the playlist generator flow.
    Args:
        client: bedrock agent boto3 client.
        role_arn (str): Name for the new IAM role.
        prompt_model_id (str): The id of the model to use in the prompt node.
    Returns:
        dict: The response from the create_flow operation.
    """

    input_node = create_input_node("FlowInput")
    prompt_node = create_prompt_node("MakePlaylist", prompt_model_id)
    output_node = create_output_node("FlowOutput")

    # Create connections between the nodes
    connections = []

    #  First, create connections between the output of the flow 
    # input node and each input of the prompt node.
    for prompt_node_input in prompt_node["inputs"]:
        connections.append(
            {
                "name": "_".join([input_node["name"], prompt_node["name"],
                                   prompt_node_input["name"]]),
                "source": input_node["name"],
                "target": prompt_node["name"],
                "type": "Data",
                "configuration": {
                    "data": {
                        "sourceOutput": input_node["outputs"][0]["name"],
                        "targetInput": prompt_node_input["name"]
                    }
                }
            }
        )

    # Then, create a connection between the output of the prompt node and the input of the flow output node
    connections.append(
        {
            "name": "_".join([prompt_node["name"], output_node["name"]]),
            "source": prompt_node["name"],
            "target": output_node["name"],
            "type": "Data",
            "configuration": {
                "data": {
                    "sourceOutput": prompt_node["outputs"][0]["name"],
                    "targetInput": output_node["inputs"][0]["name"]
                }
            }
        }
    )

    flow_def = {
        "nodes": [input_node, prompt_node, output_node],
        "connections": connections
    }

    # Create the flow.

    response = create_flow(
        client, flow_name, flow_description, role_arn, flow_def)

    return response

# snippet-end:[python.example_code.bedrock-agent-runtime.create_playlist_flow]


# snippet-start:[python.example_code.bedrock-agent-runtime.create_get_model_arn]
def get_model_arn(client, model_id):
    """
    Gets the Amazon Resource Name (ARN) for a model.
    Args:
        client (str): Amazon Bedrock boto3 client.
        model_id (str): The id of the model.
    Returns:
        str: The ARN of the model.
    """

    try:
        # Call GetFoundationModelDetails operation
        response = client.get_foundation_model(modelIdentifier=model_id)

        # Extract model ARN from the response
        model_arn = response['modelDetails']['modelArn']

        return model_arn

    except ClientError as e:
        logger.exception("Client error getting model ARN: %s", {str(e)})
        raise

    except Exception as e:
        logger.exception("Unexpected error getting model ARN: %s", {str(e)})
        raise
# snippet-end:[python.example_code.bedrock-agent-runtime.create_get_model_arn]


# snippet-start:[python.example_code.bedrock-agent-runtime.flow_prepare_version_alias]


def prepare_flow_version_and_alias(bedrock_agent_client,
                                   flow_id):
    """
    Prepares the flow and then creates a flow version and flow alias.
    Args:
        bedrock_agent_client: Amazon Bedrock Agent boto3 client.
        flowd_id (str): The ID of the flow that you want to prepare.
    Returns: The flow_version and flow_alias. 

    """

    status = prepare_flow(bedrock_agent_client, flow_id)

    flow_version = None
    flow_alias = None

    if status == 'Prepared':

        # Create the flow version and alias.
        flow_version = create_flow_version(bedrock_agent_client,
                                           flow_id,
                                           f"flow version for flow {flow_id}.")

        flow_alias = create_flow_alias(bedrock_agent_client,
                                       flow_id,
                                       flow_version,
                                       "latest",
                                       f"Alias for flow {flow_id}, version {flow_version}")

    return flow_version, flow_alias

# snippet-end:[python.example_code.bedrock-agent-runtime.flow_prepare_version_alias]

# snippet-start:[python.example_code.bedrock-agent-runtime.flow_delete_resources]


def delete_role_resources(bedrock_agent_client,
                          iam_client,
                          role_name,
                          flow_id,
                          flow_version,
                          flow_alias):
    """
    Deletes the flow, flow alias, flow version, and IAM roles.
    Args:
        bedrock_agent_client: Amazon Bedrock Agent boto3 client.
        iam_client: Amazon IAM boto3 client.
        role_name (str): The name of the IAM role.
        flow_id (str): The id of the flow.
        flow_version (str): The version of the flow.
        flow_alias (str): The alias of the flow.
    """

    delete_flow_alias(bedrock_agent_client, flow_id, flow_alias)
    delete_flow_version(bedrock_agent_client,
                        flow_id, flow_version)
    delete_flow(bedrock_agent_client, flow_id)
    delete_flow_role(iam_client, role_name)

# snippet-end:[python.example_code.bedrock-agent-runtime.flow_delete_resources]


def main():
    """
    Creates, runs, and optionally deletes a Bedrock flow for generating music playlists.

    Note:
        Requires valid AWS credentials in the default profile
    """

    try:

        # Get various boto3 clients.
        session = boto3.Session(profile_name='default')
        bedrock_agent_runtime_client = session.client('bedrock-agent-runtime')
        bedrock_agent_client = session.client('bedrock-agent')
        bedrock_client = session.client('bedrock')
        iam_client = session.client('iam')

        prompt_model_id = "amazon.nova-pro-v1:0"

        # Base the flow name on the current date and time
        current_time = datetime.now()
        timestamp = current_time.strftime("%Y-%m-%d-%H-%M-%S")
        flow_name = f"FlowPlayList_{timestamp}"
        flow_description = "A flow to generate a music playlist."

        # Create a role for the flow.
        role_name = f"BedrockFlowRole-{flow_name}"
        role = create_flow_role(iam_client, role_name)
        role_arn = role['Arn']

        # Create the flow.
        response = create_playlist_flow(
            bedrock_agent_client, flow_name, flow_description, role_arn, prompt_model_id)
        flow_id = response.get('id')

        if flow_id:
            # Update accessible resources in the role.
            model_arn = get_model_arn(bedrock_client, prompt_model_id)
            update_role_policy(iam_client, role_name, [
                               response.get('arn'), model_arn])

            # Prepare the flow and flow version.
            flow_version, flow_alias = prepare_flow_version_and_alias(
                bedrock_agent_client, flow_id)

            # Run the flow.
            if flow_version and flow_alias:
                run_playlist_flow(bedrock_agent_runtime_client,
                                  flow_id, flow_alias)

                delete_choice = input("Delete flow? y or n : ").lower()

                if delete_choice == 'y':
                    delete_role_resources(bedrock_agent_client,
                                          iam_client,
                                          role_name,
                                          flow_id,
                                          flow_version,
                                          flow_alias)
                else:
                    print("Flow not deleted.")
                    print(f"Flow ID: {flow_id}")
                    print(f"Flow version: {flow_version}")
                    print(f"Flow alias: {flow_alias}")
                    print(f"Role ARN: {role_arn}")

            else:
                print("Couldn't run. Deleting flow and role.")
                delete_flow(bedrock_agent_client, flow_id)
                delete_flow_role(iam_client, role_name)
        else:
            print("Couldn't create flow. Deleting role.")
            delete_flow_role(iam_client, role_name)
        print("Done")

    except Exception as e:
        print(f"Fatal error: {str(e)}")


if __name__ == "__main__":
    main()

# snippet-end:[python.example_code.bedrock-agent-runtine.Scenario_GettingStartedBedrockFlows]
