# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows you how to use the AWS SDK for Python (Boto3) with Amazon Cognito to
do the following:

1. xxx

This scenario requires the following resources:

* An existing xxx.
* A xxx.
"""

import json
import logging
import random
import re
import string
import sys
import time

import boto3
from botocore.exceptions import ClientError

from bedrock_agent_wrapper import BedrockAgentWrapper

# Add relative path to include demo_tools in this code example without needing to set up.
sys.path.append("..")
sys.path.append("../..")
import demo_tools.question as q
import demo_tools.retries as r

logger = logging.getLogger(__name__)

AGENT_ROLE_POLICY_NAME = "agent_role_policy"

api_schema = """
    openapi: 3.0.0
    info: 
      title: Time API 
      version: 1.0.0 
      description: API to get the current date and time.
    paths: 
      /get-current-date-and-time: 
        get: 
          summary: Gets the current date and time. 
          description: Gets the current date and time.
          operationId: getDateAndTime
          responses: 
            '200': 
              description: Gets the current date and time. 
              content: 
                'application/json': 
                  schema: 
                    type: object 
                    properties:
                      date:
                        type: string
                        description: The current date
                      time:
                        type: string
                        description: The current time
    """

class BedrockAgentScenarioWrapper:
    created_resources = {}

    def __init__(self, bedrock_agent_client, iam_client):
        self.bedrock_agent_client = bedrock_agent_client
        self.bedrock_wrapper = BedrockAgentWrapper(bedrock_agent_client)
        self.iam_client = iam_client
        logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    def run(self):
        print("=" * 88)
        print("Welcome to the Amazon Bedrock Agents demo.")
        print("=" * 88)

        # Query values from user
        print("Let's start with creating an agent:")
        print("-" * 40)
        name, model_id = self.input_agent_params()
        print("-" * 40)

        # Create an execution role for the agent
        print("Creating an execution role...")
        agent_role = self.create_role_for_agent()
        print(f"Role {agent_role.role_name} created.")
        self.created_resources["agent_role"] = agent_role

        # Create the agent
        print("Creating the agent...")
        instruction = """
            You are a friendly chat bot. You have access to a function, called 
            'current_date_and_time' that returns information about the current 
            date and time. When responding with date or time, please always add
            that the timezone is EST.
            """
        agent = self.bedrock_wrapper.create_agent(
            name,
            model_id,
            agent_role.arn,
            instruction
        )

        agent_id = agent["agentId"]
        agent_status = agent["agentStatus"]
        while agent_status != "NOT_PREPARED":
            r.wait(2)
            agent_status = self.bedrock_wrapper.get_agent(agent_id)["agentStatus"]

        self.created_resources["agent"] = agent

        # Prepare the agent
        print("Creating a DRAFT version of the agent...")
        prepared_agent_data = self.bedrock_wrapper.prepare_agent(agent_id)
        agent_status = prepared_agent_data["agentStatus"]
        while agent_status != "PREPARED":
            r.wait(2)
            agent_status = self.bedrock_wrapper.get_agent(agent_id)["agentStatus"]

        agent_version = prepared_agent_data["agentVersion"]

        # Create an action group
        print("Creating an action group for the agent...")
        action_group_name = "current_date_and_time"
        description = "Gets the current date and time."
        function_arn = "arn:aws:lambda:us-east-1:424086380854:function:DateTime"
        action_group = self.bedrock_wrapper.create_agent_action_group(
            action_group_name,
            description,
            agent_id,
            agent_version,
            function_arn,
            api_schema
        )

        # prepare_agent needs to be called again to activate the action group
        self.bedrock_wrapper.prepare_agent(agent_id)

        print("=" * 88)
        print("Thanks for running the demo!\n")
        delete = q.ask("Do you want to delete the agent? [y/N] ", q.is_yesno)

        if delete:
            self.delete_resources()

    def input_agent_params(self):
        existing_agent_names = [agent["agentName"] for agent in self.bedrock_wrapper.list_agents()]

        name = ""
        valid_name = False
        while not valid_name:
            name = q.ask("Enter an agent name: ", self.is_valid_agent_name)
            if name.lower() in [n.lower() for n in existing_agent_names]:
                valid_name = False
                print(f"Agent {name} conflicts with an existing agent. Please use a different name.")
            else:
                valid_name = True

        model_ids = ["anthropic.claude-instant-v1", "anthropic.claude-v2"]
        model_id = model_ids[q.choose("Which foundation model would you like to use? ", model_ids)]

        return name, model_id

    def delete_resources(self):
        if "agent" in self.created_resources:
            agent = self.created_resources["agent"]
            print(f"Deleting Bedrock agent '{agent["agentName"]}'...")
            agent_status = self.bedrock_wrapper.delete_agent(agent["agentId"])["agentStatus"]
            while agent_status == "DELETING":
                r.wait(2)
                try:
                    agent_status = self.bedrock_wrapper.get_agent(agent["agentId"], log_error=False)["agentStatus"]
                except ClientError as err:
                    if err.response["Error"]["Code"] == "ResourceNotFoundException":
                        agent_status = "DELETED"

        if "agent_role" in self.created_resources:
            agent_role = self.created_resources["agent_role"]
            print(f"Deleting role '{agent_role.role_name}'...")
            agent_role.Policy(AGENT_ROLE_POLICY_NAME).delete()
            agent_role.delete()

    def create_role_for_agent(self):
        postfix = "".join(random.choice(string.ascii_uppercase + "0123456789") for _ in range(8))
        name = f"AmazonBedrockExecutionRoleForAgents_{postfix}"
        trust_policy = json.dumps({
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "bedrock.amazonaws.com"
                    },
                    "Action": "sts:AssumeRole"
                }
            ]
        })

        execution_role = self.iam_client.create_role(
            RoleName=name,
            AssumeRolePolicyDocument=trust_policy
        )
        execution_role.Policy(AGENT_ROLE_POLICY_NAME).put(
            PolicyDocument=json.dumps({
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Action": "*",
                        "Resource": "*"
                    }
                ]
            })
        )

        return execution_role

    @staticmethod
    def is_valid_agent_name(answer):
        valid_regex = r"^[a-zA-Z0-9_-]{1,100}$"
        return (
            answer if answer and len(answer) <= 100 and re.match(valid_regex, answer) else None,
            "I need a name for the agent, please. Valid characters are a-z, A-Z, 0-9, _ (underscore) and - (hyphen)."
        )


def main():
    bedrock_agent_client = boto3.client(service_name="bedrock-agent", region_name="us-east-1")
    iam_client = boto3.resource("iam")
    scenario = BedrockAgentScenarioWrapper(bedrock_agent_client, iam_client)
    try:
        scenario.run()
    except Exception:
        logging.exception("Something went wrong with the demo.")


if __name__ == "__main__":
    main()
