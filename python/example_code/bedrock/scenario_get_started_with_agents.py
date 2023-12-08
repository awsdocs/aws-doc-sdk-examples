# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows you how to use Agents for Amazon Bedrock with the AWS SDK for Python (Boto3)
to do the following:

1. Create an AWS Identity and Access Management (IAM) role that grants Bedrock
permission to invoke a Lambda function.

This scenario requires the following resources:

* An existing xxx.
* A xxx.
"""

import io
import json
import logging
import random
import re
import string
import sys
import time
import yaml
import zipfile

import boto3
from botocore.exceptions import ClientError

from bedrock_agent_wrapper import BedrockAgentWrapper


# Add relative path to include demo_tools in this code example without needing to set up.
sys.path.append("../..")
import demo_tools.question as q
from demo_tools.retries import wait

logger = logging.getLogger(__name__)


class BedrockAgentScenarioWrapper:
    def __init__(self, bedrock_agent_client, lambda_client, iam_resource, postfix):
        self.agent = None
        self.agent_role = None
        self.agent_version = None
        self.lambda_role = None
        self.lambda_function = None

        self.iam_resource = iam_resource
        self.lambda_client = lambda_client

        self.bedrock_agent_client = bedrock_agent_client
        self.bedrock_wrapper = BedrockAgentWrapper(bedrock_agent_client)
        self.postfix = postfix

        logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    def run(self):
        print("=" * 88)
        print("Welcome to the Amazon Bedrock Agents demo.")
        print("=" * 88)

        # Query input from user
        print("Let's start with creating an agent:")
        print("-" * 40)
        name, model_id = self.input_agent_params()
        print("-" * 40)

        print("Creating an an execution role for the agent...")
        self.agent_role = self._create_agent_role()

        # Create the agent
        print("Creating the agent...")
        self.agent = self._create_bedrock_agent(name, model_id)

        print("Preparing a DRAFT version of the agent...")
        self.agent_version = self._prepare_agent()

        print("Creating an execution role for the Lambda function...")
        self.lambda_role = self._create_lambda_role()

        print("Waiting for the execution role to propagate...")
        wait(10)

        print("Creating the Lambda function...")
        self.lambda_function = self._create_lambda_function()

        # Grant the agent permissions to invoke the Lambda function
        # This requires permissions on both the agent and the function:
        self._allow_agent_to_invoke(self.lambda_function)
        self._let_lambda_function_accept_invocations_from(self.agent)

        print("Creating an action group for the agent...")
        self._create_agent_action_group()

        print(
            "Preparing a new DRAFT version of the agent, including the action group..."
        )
        self.bedrock_wrapper.prepare_agent(self.agent["agentId"])

        print("=" * 88)
        print("Thanks for running the demo!\n")
        delete = q.ask(
            "Do you want to delete the created resources? [y/N] ", q.is_yesno
        )

        if delete:
            self.delete_resources()

    def input_agent_params(self):
        existing_agent_names = [
            agent["agentName"] for agent in self.bedrock_wrapper.list_agents()
        ]
        name = ""
        valid_name = False
        while not valid_name:
            name = q.ask("Enter an agent name: ", self.is_valid_agent_name)
            if name.lower() in [n.lower() for n in existing_agent_names]:
                valid_name = False
                print(
                    f"Agent {name} conflicts with an existing agent. Please use a different name."
                )
            else:
                valid_name = True

        model_ids = ["anthropic.claude-instant-v1", "anthropic.claude-v2"]
        model_id = model_ids[
            q.choose("Which foundation model would you like to use? ", model_ids)
        ]

        return name, model_id

    def delete_resources(self):
        if self.agent:
            agent_name = self.agent["agentName"]
            print(f"Deleting Bedrock agent '{agent_name}'...")
            agent_id = self.agent["agentId"]
            agent_status = self.bedrock_wrapper.delete_agent(agent_id)["agentStatus"]
            while agent_status == "DELETING":
                wait(5)
                try:
                    agent_status = self.bedrock_wrapper.get_agent(
                        agent_id, log_error=False
                    )["agentStatus"]
                except ClientError as err:
                    if err.response["Error"]["Code"] == "ResourceNotFoundException":
                        agent_status = "DELETED"

        if self.agent_role:
            print(f"Deleting role '{self.agent_role.role_name}'...")
            self.agent_role.Policy("agent_role_policy").delete()
            self.agent_role.delete()

        if self.lambda_role:
            print(f"Deleting role '{self.lambda_role.role_name}'...")
            for policy in self.lambda_role.attached_policies.all():
                policy.detach_role(RoleName=self.lambda_role.role_name)
            self.lambda_role.delete()

        if self.lambda_function:
            name = self.lambda_function["FunctionName"]
            print(f"Deleting function '{name}'...")
            self.lambda_client.delete_function(FunctionName=name)

    def _create_agent_role(self):
        role_name = f"AmazonBedrockExecutionRoleForAgents_{self.postfix}"
        trust_policy = {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": {"Service": "bedrock.amazonaws.com"},
                    "Action": "sts:AssumeRole",
                }
            ],
        }

        try:
            role = self.iam_resource.create_role(
                RoleName=role_name, AssumeRolePolicyDocument=json.dumps(trust_policy)
            )
            logger.info(f"Created role {role_name}")
        except ClientError as e:
            logger.error(f"Couldn't create role {role_name}. Here's why: {e}")
            raise
        else:
            return role

    def _create_lambda_role(self):
        role_name = f"AmazonBedrockExecutionRoleForLambda_{self.postfix}"
        trust_policy = {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": {"Service": "lambda.amazonaws.com"},
                    "Action": "sts:AssumeRole",
                }
            ],
        }

        try:
            role = self.iam_resource.create_role(
                RoleName=role_name, AssumeRolePolicyDocument=json.dumps(trust_policy)
            )
            logger.info(f"Created role {role_name}")
        except ClientError as e:
            logger.error(f"Couldn't create role {role_name}. Here's why: {e}")
            raise
        else:
            return role

    def _create_bedrock_agent(self, name, model_id):
        instruction = """
            You are a friendly chat bot. You have access to a function, called
            'current_date_and_time' that returns information about the current
            date and time. When responding with date or time, please always add
            that the timezone is EST.
            """
        agent = self.bedrock_wrapper.create_agent(
            agent_name=name,
            foundation_model=model_id,
            instruction=instruction,
            role_arn=self.agent_role.arn,
        )
        self._wait_for_agent_status(agent["agentId"], "NOT_PREPARED")
        return agent

    def _prepare_agent(self):
        agent_id = self.agent["agentId"]
        version = self.bedrock_wrapper.prepare_agent(agent_id)["agentVersion"]
        # Allow for the preparation process to finish
        self._wait_for_agent_status(agent_id, "PREPARED")
        return version

    def _wait_for_agent_status(self, agent_id, status):
        while self.bedrock_wrapper.get_agent(agent_id)["agentStatus"] != status:
            wait(2)

    def _allow_agent_to_invoke(self, lambda_function):
        self.agent_role.Policy("agent_role_policy").put(
            PolicyDocument=json.dumps(
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Action": "lambda:InvokeFunction",
                            "Resource": lambda_function["FunctionArn"],
                        }
                    ],
                }
            )
        )

    def _create_lambda_function(self):
        function_name = f"AmazonBedrockExampleFunction_{self.postfix}"
        deployment_package = self._create_deployment_package(function_name)

        try:
            lambda_function = self.lambda_client.create_function(
                FunctionName=function_name,
                Description="Lambda function for Amazon Bedrock example",
                Runtime="python3.11",
                Role=self.lambda_role.arn,
                Handler=f"{function_name}.lambda_handler",
                Code={"ZipFile": deployment_package},
                Publish=True,
            )
            waiter = self.lambda_client.get_waiter("function_active_v2")
            waiter.wait(FunctionName=function_name)
        except ClientError as e:
            logger.error(
                f"Couldn't create Lambda function {function_name}. Here's why: {e}"
            )
            raise
        else:
            return lambda_function

    def _create_agent_action_group(self):
        try:
            with open("./scenario_resources/api_schema.yaml") as file:
                self.bedrock_wrapper.create_agent_action_group(
                    name="current_date_and_time",
                    description="Gets the current date and time.",
                    agent_id=self.agent["agentId"],
                    agent_version=self.agent_version,
                    function_arn=self.lambda_function["FunctionArn"],
                    api_schema=json.dumps(yaml.safe_load(file)),
                )
        except ClientError as e:
            logger.error(f"Couldn't create agent action group. Here's why: {e}")
            raise
        else:
            return self.lambda_function

    def _let_lambda_function_accept_invocations_from(self, agent):
        try:
            self.lambda_client.add_permission(
                FunctionName=self.lambda_function["FunctionName"],
                StatementId="BedrockAccess",
                Action="lambda:InvokeFunction",
                Principal="bedrock.amazonaws.com",
                SourceArn=agent["agentArn"],
            )
        except ClientError as e:
            logger.error(
                f"Couldn't grant Bedrock permission to invoke the Lambda function. Here's why: {e}"
            )
            raise

    @staticmethod
    def _create_deployment_package(function_name):
        buffer = io.BytesIO()
        with zipfile.ZipFile(buffer, "w") as zipped:
            zipped.write(
                "./scenario_resources/lambda_function_code.py", f"{function_name}.py"
            )
        buffer.seek(0)
        return buffer.read()

    @staticmethod
    def is_valid_agent_name(answer):
        valid_regex = r"^[a-zA-Z0-9_-]{1,100}$"
        return (
            answer
            if answer and len(answer) <= 100 and re.match(valid_regex, answer)
            else None,
            "I need a name for the agent, please. Valid characters are a-z, A-Z, 0-9, _ (underscore) and - (hyphen).",
        )


def main():
    region = "us-east-1"

    bedrock_client = boto3.client(service_name="bedrock-agent", region_name=region)
    lambda_client = boto3.client(service_name="lambda", region_name=region)
    iam_resource = boto3.resource("iam")

    postfix = "".join(
        random.choice(string.ascii_lowercase + "0123456789") for _ in range(8)
    )
    scenario = BedrockAgentScenarioWrapper(
        bedrock_agent_client=bedrock_client,
        lambda_client=lambda_client,
        iam_resource=iam_resource,
        postfix=postfix,
    )
    try:
        scenario.run()
    except Exception:
        logging.exception("Something went wrong with the demo.")


if __name__ == "__main__":
    main()
