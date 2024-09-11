# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Bedrock to manage
Bedrock Agents.
"""

import logging
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.bedrock-agent.BedrockAgentWrapper.class]
# snippet-start:[python.example_code.bedrock-agent.BedrockAgentWrapper.decl]
class BedrockAgentWrapper:
    """Encapsulates Amazon Bedrock Agent actions."""

    def __init__(self, client):
        """
        :param client: A Boto3 Amazon Bedrock Agents client, which is a low-level client that
                       represents Amazon Bedrock Agents and describes the API operations
                       for creating and managing Bedrock Agent resources.
        """
        self.client = client

    # snippet-end:[python.example_code.bedrock-agent.BedrockAgentWrapper.decl]

    # snippet-start:[python.example_code.bedrock-agent.CreateAgent]
    def create_agent(self, agent_name, foundation_model, role_arn, instruction):
        """
        Creates an agent that orchestrates interactions between foundation models,
        data sources, software applications, user conversations, and APIs to carry
        out tasks to help customers.

        :param agent_name: A name for the agent.
        :param foundation_model: The foundation model to be used for orchestration by the agent.
        :param role_arn: The ARN of the IAM role with permissions needed by the agent.
        :param instruction: Instructions that tell the agent what it should do and how it should
                            interact with users.
        :return: The response from Amazon Bedrock Agents if successful, otherwise raises an exception.
        """
        try:
            response = self.client.create_agent(
                agentName=agent_name,
                foundationModel=foundation_model,
                agentResourceRoleArn=role_arn,
                instruction=instruction,
            )
        except ClientError as e:
            logger.error(f"Error: Couldn't create agent. Here's why: {e}")
            raise
        else:
            return response["agent"]

    # snippet-end:[python.example_code.bedrock-agent.CreateAgent]

    # snippet-start:[python.example_code.bedrock-agent.CreateAgentActionGroup]
    def create_agent_action_group(
            self, name, description, agent_id, agent_version, function_arn, api_schema
    ):
        """
        Creates an action group for an agent. An action group defines a set of actions that an
        agent should carry out for the customer.

        :param name: The name to give the action group.
        :param description: The description of the action group.
        :param agent_id: The unique identifier of the agent for which to create the action group.
        :param agent_version: The version of the agent for which to create the action group.
        :param function_arn: The ARN of the Lambda function containing the business logic that is
                             carried out upon invoking the action.
        :param api_schema: Contains the OpenAPI schema for the action group.
        :return: Details about the action group that was created.
        """
        try:
            response = self.client.create_agent_action_group(
                actionGroupName=name,
                description=description,
                agentId=agent_id,
                agentVersion=agent_version,
                actionGroupExecutor={"lambda": function_arn},
                apiSchema={"payload": api_schema},
            )
            agent_action_group = response["agentActionGroup"]
        except ClientError as e:
            logger.error(f"Error: Couldn't create agent action group. Here's why: {e}")
            raise
        else:
            return agent_action_group

    # snippet-end:[python.example_code.bedrock-agent.CreateAgentActionGroup]

    # snippet-start:[python.example_code.bedrock-agent.CreateAgentAlias]
    def create_agent_alias(self, name, agent_id):
        """
        Creates an alias of an agent that can be used to deploy the agent.

        :param name: The name of the alias.
        :param agent_id: The unique identifier of the agent.
        :return: Details about the alias that was created.
        """
        try:
            response = self.client.create_agent_alias(
                agentAliasName=name, agentId=agent_id
            )
            agent_alias = response["agentAlias"]
        except ClientError as e:
            logger.error(f"Couldn't create agent alias. {e}")
            raise
        else:
            return agent_alias

    # snippet-end:[python.example_code.bedrock-agent.CreateAgentAlias]

    # snippet-start:[python.example_code.bedrock-agent.DeleteAgent]
    def delete_agent(self, agent_id):
        """
        Deletes an Amazon Bedrock agent.

        :param agent_id: The unique identifier of the agent to delete.
        :return: The response from Amazon Bedrock Agents if successful, otherwise raises an exception.
        """

        try:
            response = self.client.delete_agent(
                agentId=agent_id, skipResourceInUseCheck=False
            )
        except ClientError as e:
            logger.error(f"Couldn't delete agent. {e}")
            raise
        else:
            return response

    # snippet-end:[python.example_code.bedrock-agent.DeleteAgent]

    # snippet-start:[python.example_code.bedrock-agent.DeleteAgentAlias]
    def delete_agent_alias(self, agent_id, agent_alias_id):
        """
        Deletes an alias of an Amazon Bedrock agent.

        :param agent_id: The unique identifier of the agent that the alias belongs to.
        :param agent_alias_id: The unique identifier of the alias to delete.
        :return: The response from Amazon Bedrock Agents if successful, otherwise raises an exception.
        """

        try:
            response = self.client.delete_agent_alias(
                agentId=agent_id, agentAliasId=agent_alias_id
            )
        except ClientError as e:
            logger.error(f"Couldn't delete agent alias. {e}")
            raise
        else:
            return response

    # snippet-end:[python.example_code.bedrock-agent.DeleteAgentAlias]

    # snippet-start:[python.example_code.bedrock-agent.GetAgent]
    def get_agent(self, agent_id, log_error=True):
        """
        Gets information about an agent.

        :param agent_id: The unique identifier of the agent.
        :param log_error: Whether to log any errors that occur when getting the agent.
                          If True, errors will be logged to the logger. If False, errors
                          will still be raised, but not logged.
        :return: The information about the requested agent.
        """

        try:
            response = self.client.get_agent(agentId=agent_id)
            agent = response["agent"]
        except ClientError as e:
            if log_error:
                logger.error(f"Couldn't get agent {agent_id}. {e}")
            raise
        else:
            return agent

    # snippet-end:[python.example_code.bedrock-agent.GetAgent]

    # snippet-start:[python.example_code.bedrock-agent.ListAgents]
    def list_agents(self):
        """
        List the available Amazon Bedrock Agents.

        :return: The list of available bedrock agents.
        """

        try:
            all_agents = []

            paginator = self.client.get_paginator("list_agents")
            for page in paginator.paginate(PaginationConfig={"PageSize": 10}):
                all_agents.extend(page["agentSummaries"])

        except ClientError as e:
            logger.error(f"Couldn't list agents. {e}")
            raise
        else:
            return all_agents

    # snippet-end:[python.example_code.bedrock-agent.ListAgents]

    # snippet-start:[python.example_code.bedrock-agent.ListAgentActionGroups]
    def list_agent_action_groups(self, agent_id, agent_version):
        """
        List the action groups for a version of an Amazon Bedrock Agent.

        :param agent_id: The unique identifier of the agent.
        :param agent_version: The version of the agent.
        :return: The list of action group summaries for the version of the agent.
        """

        try:
            action_groups = []

            paginator = self.client.get_paginator("list_agent_action_groups")
            for page in paginator.paginate(
                    agentId=agent_id,
                    agentVersion=agent_version,
                    PaginationConfig={"PageSize": 10},
            ):
                action_groups.extend(page["actionGroupSummaries"])

        except ClientError as e:
            logger.error(f"Couldn't list action groups. {e}")
            raise
        else:
            return action_groups

    # snippet-end:[python.example_code.bedrock-agent.ListAgentActionGroups]

    # snippet-start:[python.example_code.bedrock-agent.ListAgentKnowledgeBases]
    def list_agent_knowledge_bases(self, agent_id, agent_version):
        """
        List the knowledge bases associated with a version of an Amazon Bedrock Agent.

        :param agent_id: The unique identifier of the agent.
        :param agent_version: The version of the agent.
        :return: The list of knowledge base summaries for the version of the agent.
        """

        try:
            knowledge_bases = []

            paginator = self.client.get_paginator("list_agent_knowledge_bases")
            for page in paginator.paginate(
                    agentId=agent_id,
                    agentVersion=agent_version,
                    PaginationConfig={"PageSize": 10},
            ):
                knowledge_bases.extend(page["agentKnowledgeBaseSummaries"])

        except ClientError as e:
            logger.error(f"Couldn't list knowledge bases. {e}")
            raise
        else:
            return knowledge_bases

    # snippet-end:[python.example_code.bedrock-agent.ListAgentKnowledgeBases]

    # snippet-start:[python.example_code.bedrock-agent.PrepareAgent]
    def prepare_agent(self, agent_id):
        """
        Creates a DRAFT version of the agent that can be used for internal testing.

        :param agent_id: The unique identifier of the agent to prepare.
        :return: The response from Amazon Bedrock Agents if successful, otherwise raises an exception.
        """
        try:
            prepared_agent_details = self.client.prepare_agent(agentId=agent_id)
        except ClientError as e:
            logger.error(f"Couldn't prepare agent. {e}")
            raise
        else:
            return prepared_agent_details

    # snippet-end:[python.example_code.bedrock-agent.PrepareAgent]

# snippet-end:[python.example_code.bedrock-agent.BedrockAgentWrapper.class]
