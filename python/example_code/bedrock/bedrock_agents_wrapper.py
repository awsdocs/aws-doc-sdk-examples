# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Bedrock to manage
Bedrock agents.
"""

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.bedrock.BedrockAgentsWrapper.class]
# snippet-start:[python.example_code.bedrock.BedrockAgentsWrapper.decl]
class BedrockAgentsWrapper:
    """Encapsulates Amazon Bedrock Agent actions."""

    def __init__(self, bedrock_client):
        """
        :param bedrock_client: A Boto3 Amazon Bedrock client, which is a low-level client that
                               represents Amazon Bedrock and describes the API operations for
                               creating and managing Bedrock resources.
        """
        self.bedrock_client = bedrock_client
    # snippet-end:[python.example_code.bedrock.BedrockAgentsWrapper.decl]

    # snippet-start:[python.example_code.bedrock.ListAgents]
    def list_agents(self):
        """
        List the available Amazon Bedrock agents.

        :return: The list of available bedrock agents.
        """

        try:
            response = self.bedrock_client.list_agents()
            agents = response
            print(agents)
        except ClientError as e:
            logger.error(f'Error: Couldn\'t list agents. Here\'s why: {e}')
            raise
        else:
            return agents

    # snippet-end:[python.example_code.bedrock.ListAgents]

# snippet-end:[python.example_code.bedrock.BedrockAgentsWrapper.class]


def usage_demo():
    """
    Shows how to use of Amazon Bedrock agents.
    This demonstration gets the list of available agents and prints their
    respective summaries.
    """
    logging.basicConfig(level=logging.INFO)
    print("-" * 88)
    print("Welcome to the Amazon Bedrock Agents demo.")
    print("-" * 88)

    client = boto3.client(service_name="bedrock", region_name="us-east-1")

    wrapper = BedrockAgentsWrapper(client)

    print("Listing the available Bedrock agents.")

    try:
        for agent in wrapper.list_agents():
            print("\n" + "=" * 42)
            print(f' Agent: {agent}')
            print("-" * 42)
            print("=" * 42)
    except ClientError:
        logger.exception("Couldn't list Bedrock agents.")
        raise

if __name__ == "__main__":
    usage_demo()
