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
    def get_agent(self, agent_id):
        """
        Gets information about an agent.

        :param agent_id: The unique identifier of the agent.
        :return: The information about the requested agent.
        """

        try:
            agent = self.client.get_agent(agentId=agent_id)
        except ClientError as e:
            logger.error(f"Error: Couldn't get agent {agent_id}. Here's why: {e}")
            raise
        else:
            return agent

    # snippet-end:[python.example_code.bedrock.GetAgent]


# snippet-end:[python.example_code.bedrock.BedrockAgentWrapper.class]


def usage_demo():
    """
    Shows how to use Amazon Bedrock agents.
    This demonstration gets the list of available agents, retrieves their
    respective details, and prints them to the console.
    """
    logging.basicConfig(level=logging.INFO)
    print("-" * 88)
    print("Welcome to the Amazon Bedrock Agents demo.")
    print("-" * 88)

    region = "us-east-1"
    client = boto3.client(service_name="bedrock-agent", region_name=region)

    wrapper = BedrockAgentWrapper(client)

    print("\n" + "=" * 42)
    print("Retrieving list of Bedrock agents...")
    try:
        agents = wrapper.list_agents()
    except ClientError:
        logger.exception("Couldn't list Bedrock agents.")
        raise

    if len(agents) == 0:
        print(f"Couldn't find any agents in {region}.")
        exit()

    print(f"Found {len(agents)} agents in {region}.")

    for agent_summary in agents:
        agent_id = agent_summary["agentId"]
        print("=" * 42)
        print(f"Retrieving details for agent {agent_id}...")
        try:
            agent = wrapper.get_agent(agent_id)["agent"]
            print(f' Agent name: {agent["agentName"]}')
            print(f' Agent status: {agent["agentStatus"]}')
            if "agentVersion" in agent:
                print(f' Agent version: {agent["agentVersion"]}')
            if "description" in agent:
                print(f' Description: {agent["description"]}')
            print(f' Foundation Model: {agent["foundationModel"]}')
            if "recommendedActions" in agent:
                print(f' Recommended actions: {agent["recommendedActions"]}')
        except ClientError:
            logger.exception(f"Couldn't get agents {agent_id}.")
            raise


if __name__ == "__main__":
    usage_demo()
