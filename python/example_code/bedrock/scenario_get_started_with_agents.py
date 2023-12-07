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


class BedrockAgentScenarioWrapper:
    created_resources = {}

    def __init__(self, bedrock_agent_client, iam_client):
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
            You are a friendly chatbot. When asked for the date and time, 
            your answer in a natural way, including the full name of the weekday.
            """
        agent = self.bedrock_wrapper.create_agent(
            name,
            model_id,
            agent_role.arn,
            instruction
        )["agent"]

        agent_id = agent["agentId"]
        agent_status = agent["agentStatus"]
        while agent_status == "CREATING":
            r.wait(2)
            agent_status = self.bedrock_wrapper.get_agent(agent_id)["agent"]["agentStatus"]

        print(f"Bedrock Agent '{name}' with id '{agent_id}' created.")
        self.created_resources["agent"] = agent

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
                    agent_status = self.bedrock_wrapper.get_agent(agent["agentId"], log_error=False)["agent"][
                        "agentStatus"]
                except ClientError as err:
                    if err.response["Error"]["Code"] == "ResourceNotFoundException":
                        agent_status = "DELETED"

        if "agent_role" in self.created_resources:
            agent_role = self.created_resources["agent_role"]
            print(f"Deleting role '{agent_role.role_name}'...")
            agent_role.Policy(AGENT_ROLE_POLICY_NAME).delete()
            agent_role.delete()

    def create_agent(self):
        print("Creating the agent...")
        agent = self.bedrock_wrapper.create_agent(name, model, agent_role.arn)["agent"]
        self.created_resources["agent"] = agent

        agent_id = agent["agentId"]
        agent_status = agent["agentStatus"]

        while agent_status == "CREATING":
            r.wait(2)
            agent_status = self.bedrock_wrapper.get_agent(agent_id)["agent"]["agentStatus"]

        print(f"Successfully created agent {name} with id {agent_id}.")
        print(f"Its current status is: {agent["agentStatus"]}")

        return agent

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
                        "Action": "lambda:*",
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
