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

import logging
import re
import sys
import time

import boto3
from botocore.exceptions import ClientError

from bedrock_agent_wrapper import BedrockAgentWrapper

# Add relative path to include demo_tools in this code example without needing to set up.
sys.path.append("../..")
import demo_tools.question as q
import demo_tools.retries as r

logger = logging.getLogger(__name__)


def run_scenario(bedrock_client):
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    print("-" * 88)
    print("Welcome to the Amazon Bedrock Agents demo.")
    print("-" * 88)

    bedrock_wrapper = BedrockAgentWrapper(bedrock_client)
    models = [
        "anthropic.claude-instant-v1",
        "anthropic.claude-v2"
    ]

    print("Let's start with creating an agent.")

    role_arn = "arn:aws:iam::424086380854:role/AmazonBedrockExecutionRoleForAgents_traubd"

    existing_agent_names = [agent["agentName"] for agent in bedrock_wrapper.list_agents()]
    name, valid_name = "", False
    while not valid_name:
        name = q.ask("Enter an agent name: ", is_valid_agent_name)
        if name.lower() in [n.lower() for n in existing_agent_names]:
            valid_name = False
            print(f"Agent {name} conflicts with an existing agent. Please use a different name.")
        else:
            valid_name = True

    model = models[
        q.choose("Which the foundation model do you want to use? ", models)
    ]

    print("Creating the agent...")
    agent = bedrock_wrapper.create_agent(name, model, role_arn)["agent"]

    agent_id = agent["agentId"]
    agent_name = agent["agentName"]
    agent_status = agent["agentStatus"]

    while agent_status == "CREATING":
        r.wait(2)
        agent_status = bedrock_wrapper.get_agent(agent_id)["agent"]["agentStatus"]
    print(f"Successfully created agent {agent_name} with the id {agent_id}.")
    print(f"Its current status is: {agent_status}")

    print("-" * 88)
    print("Thanks for running the demo!\n")
    delete = q.ask("Do you want to delete the created agent? [y/N] ", q.is_yesno)

    if delete:
        print(f"Deleting Bedrock Agent '{agent_name}'...")
        agent_status = bedrock_client.delete_agent(agentId=agent_id)["agentStatus"]
        while agent_status == "DELETING":
            r.wait(2)
            try:
                agent_status = bedrock_wrapper.get_agent(agent_id, log_error=False)["agent"]["agentStatus"]
                print(agent_status)
            except ClientError as err:
                if err.response["Error"]["Code"] == "ResourceNotFoundException":
                    agent_status = "DELETED"
                    print(f"Bedrock Agent '{agent_name}' successfully deleted.")


def is_valid_agent_name(answer):
    valid_regex = r"^[a-zA-Z0-9_-]{1,100}$"
    return (
        answer if answer and len(answer) <= 100 and re.match(valid_regex, answer) else None,
        "I need a name for the agent, please. Valid characters are a-z, A-Z, 0-9, _ (underscore) and - (hyphen)."
    )


def main():
    bedrock_client = boto3.client(service_name="bedrock-agent", region_name="us-east-1")
    try:
        run_scenario(bedrock_client)
    except Exception:
        logging.exception("Something went wrong with the demo.")


if __name__ == "__main__":
    main()
