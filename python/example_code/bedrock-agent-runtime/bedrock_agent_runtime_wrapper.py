# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Bedrock Agents Runtime
client to send prompts to an agent to process and respond to.
"""

import logging

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.bedrock-agent-runtime.BedrockAgentsRuntimeWrapper.class]
# snippet-start:[python.example_code.bedrock-agent-runtime.BedrockAgentRuntimeWrapper.decl]
class BedrockAgentRuntimeWrapper:
    """Encapsulates Amazon Bedrock Agents Runtime actions."""

    def __init__(self, runtime_client):
        """
        :param runtime_client: A low-level client representing the Amazon Bedrock Agents Runtime.
                               Describes the API operations for running
                               inferences using Bedrock Agents.
        """
        self.agents_runtime_client = runtime_client

    # snippet-end:[python.example_code.bedrock-agent-runtime.BedrockAgentRuntimeWrapper.decl]

    # snippet-start:[python.example_code.bedrock-agent-runtime.InvokeAgent]
    def invoke_agent(self, agent_id, agent_alias_id, session_id, prompt):
        """
        Sends a prompt for the agent to process and respond to.

        :param agent_id: The unique identifier of the agent to use.
        :param agent_alias_id: The alias of the agent to use.
        :param session_id: The unique identifier of the session. Use the same value across requests
                           to continue the same conversation.
        :param prompt: The prompt that you want Claude to complete.
        :return: Inference response from the model.
        """

        try:
            # Note: The execution time depends on the foundation model, complexity of the agent,
            # and the length of the prompt. In some cases, it can take up to a minute or more to
            # generate a response.
            response = self.agents_runtime_client.invoke_agent(
                agentId=agent_id,
                agentAliasId=agent_alias_id,
                sessionId=session_id,
                inputText=prompt,
            )

            completion = ""

            for event in response.get("completion"):
                chunk = event["chunk"]
                completion = completion + chunk["bytes"].decode()

        except ClientError as e:
            logger.error(f"Couldn't invoke agent. {e}")
            raise

        return completion

    # snippet-end:[python.example_code.bedrock-agent-runtime.InvokeAgent]

    # snippet-start:[python.example_code.bedrock-agent-runtime.InvokeFlow]
    def invoke_flow(self, flow_id, flow_alias_id, input_data, execution_id):
        """
        Invoke an Amazon Bedrock flow and handle the response stream.

        Args:
            param flow_id: The ID of the flow to invoke.
            param flow_alias_id: The alias ID of the flow.
            param input_data: Input data for the flow.
            param execution_id: Execution ID for continuing a flow. Use the value None on first run.

        Return: Response from the flow.
        """
        try:
      
            request_params = None

            if execution_id is None:
                # Don't pass execution ID for first run.
                request_params = {
                    "flowIdentifier": flow_id,
                    "flowAliasIdentifier": flow_alias_id,
                    "inputs": input_data,
                    "enableTrace": True
                }
            else:
                request_params = {
                    "flowIdentifier": flow_id,
                    "flowAliasIdentifier": flow_alias_id,
                    "executionId": execution_id,
                    "inputs": input_data,
                    "enableTrace": True
                }

            response = self.agents_runtime_client.invoke_flow(**request_params)

            if "executionId" not in request_params:
                execution_id = response['executionId']

            result = ""

            # Get the streaming response
            for event in response['responseStream']:
                result = result + str(event) + '\n'
            print(result)

        except ClientError as e:
            logger.error("Couldn't invoke flow %s.", {e})
            raise

        return result

    # snippet-end:[python.example_code.bedrock-agent-runtime.InvokeFlow]

# snippet-end:[python.example_code.bedrock-agent-runtime.BedrockAgentsRuntimeWrapper.class]