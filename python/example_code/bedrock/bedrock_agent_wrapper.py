# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Bedrock to manage
Bedrock Agents.
"""

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.bedrock.BedrockAgentWrapper.class]
# snippet-start:[python.example_code.bedrock.BedrockAgentWrapper.decl]
class BedrockAgentWrapper:
    """Encapsulates Amazon Bedrock Agent actions."""

    def __init__(self, client):
        """
        :param client: A Boto3 Agents for Amazon Bedrock client, which is a low-level client that
                       represents Agents for Amazon Bedrock and describes the API operations
                       for creating and managing Bedrock Agent resources.
        """
        self.client = client

    # snippet-end:[python.example_code.bedrock.BedrockAgentWrapper.decl]

    # snippet-start:[python.example_code.bedrock.ListAgents]
    def list_agents(self):
        """
        List the available Amazon Bedrock Agents.

        :return: The list of available bedrock agents.
        """

        try:
            response = self.client.list_agents()
            agents = response["agentSummaries"]
        except ClientError as e:
            logger.error(f"Error: Couldn't list agents. Here's why: {e}")
            raise
        else:
            return agents

    # snippet-end:[python.example_code.bedrock.ListAgents]

    # snippet-start:[python.example_code.bedrock.GetAgent]
    def get_agent(self, agent_id, log_error=True):
        """
        Gets information about an agent.

        :param agent_id: The unique identifier of the agent.
        :param log_error: Whether to log any errors that occur.
                          Default: True
        :return: The information about the requested agent.
        """

        try:
            agent = self.client.get_agent(agentId=agent_id)
        except ClientError as e:
            if log_error:
                logger.error(f"Error: Couldn't get agent {agent_id}. Here's why: {e}")
            raise
        else:
            return agent

    # snippet-end:[python.example_code.bedrock.GetAgent]

    # snippet-start:[python.example_code.bedrock.CreateAgent]
    def create_agent(self, name, foundation_model, role_arn, instruction):
        try:
            response = self.client.create_agent(
                agentName=name,
                foundationModel=foundation_model,
                agentResourceRoleArn=role_arn,
                instruction=instruction
            )
        except ClientError as e:
            logger.error(f"Error: Couldn't create agent. Here's why: {e}")
            raise
        else:
            return response

    # snippet-end:[python.example_code.bedrock.CreateAgent]

    # snippet-start:[python.example_code.bedrock.DeleteAgent]
    def delete_agent(self, agent_id, skip_resource_in_use_check=False):
        """
        Deletes an Amazon Bedrock agent.

        :param agent_id: The unique identifier of the agent to delete.
        :param skip_resource_in_use_check: Whether to skip the resource in use check. By default, this value is
                                           false and deletion is stopped if the resource is in use. If you set it
                                           to true, the resource will be deleted even if the resource is in use.
        :return: The response from Bedrock if successful, otherwise raises an exception.
        """

        try:
            response = self.client.delete_agent(
                agentId=agent_id,
                skipResourceInUseCheck=skip_resource_in_use_check
            )
        except ClientError as e:
            logger.error(f"Error: Couldn't delete agent. Here's why: {e}")
            raise
        else:
            return response

    # snippet-end:[python.example_code.bedrock.DeleteAgent]

# snippet-end:[python.example_code.bedrock.BedrockAgentWrapper.class]
