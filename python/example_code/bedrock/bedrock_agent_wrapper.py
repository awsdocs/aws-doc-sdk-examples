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
            print(agents)
        except ClientError as e:
            logger.error(f'Error: Couldn\'t list agents. Here\'s why: {e}')
            raise
        else:
            return agents

    # snippet-end:[python.example_code.bedrock.ListAgents]

    # snippet-start:[python.example_code.bedrock.GetAgent]
    def get_agent(self, agent_id):
        """
        Gets information about an agent.

        :param agent_id: The unique identifier of the agent.
        :return: The information about the requested agent.
        """

        try:
            agent = self.client.get_agent(agentId=agent_id)
        except ClientError as e:
            logger.error(f'Error: Couldn\'t get agent {agent_id}. Here\'s why: {e}')
            raise
        else:
            return agent

    # snippet-end:[python.example_code.bedrock.GetAgent]

# snippet-end:[python.example_code.bedrock.BedrockAgentWrapper.class]


def usage_demo():
    """
    Shows how to use of Amazon Bedrock agents.
    This demonstration gets the list of available agents, retrieves their
    respective details, and prints them to the console.
    """
    logging.basicConfig(level=logging.INFO)
    print("-" * 88)
    print("Welcome to the Amazon Bedrock Agents demo.")
    print("-" * 88)

    client = boto3.client(service_name="bedrock-agent", region_name="us-east-1")

    wrapper = BedrockAgentWrapper(client)

    print("Listing the available Bedrock agents.")

    try:
        for agent in wrapper.list_agents():
            print("\n" + "=" * 42)
            print(f' Agent ID: {agent["agentId"]}')
            print("-" * 42)
            print(f' Name: {agent["agentName"]}')
            print(f' Status: {agent["agentStatus"]}')
            if "description" in agent:
                print(f' Description: {agent["description"]}')
            if "latestAgentVersion" in agent:
                print(f' Latest version: {agent["latestAgentVersion"]}')
            print(f' Updated at: {agent["updatedAt"]}')
            print("=" * 42)
    except ClientError:
        logger.exception("Couldn't list Bedrock agents.")
        raise

if __name__ == "__main__":
    usage_demo()
