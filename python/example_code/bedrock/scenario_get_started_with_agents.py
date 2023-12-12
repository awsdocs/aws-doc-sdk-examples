# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

This end-to-end scenario demonstrates how to use Agents for Amazon Bedrock with
the AWS SDK for Python (Boto3). It covers the following steps:

1. Creating an execution role for the Bedrock agent.
2. Creating the Bedrock agent and deploying a DRAFT version.
3. Creating a Lambda function and its associated execution role.
4. Assigning IAM permissions to enable the agent to call the Lambda function.
   Important: Ensure permissions are configured for both the agent and the Lambda function.
5. Creating an action group that connects the agent with the Lambda function.
6. Deploying the fully configured agent using a designated alias.
7. Invoking the agent with prompts provided by the user.
8. Deleting all created resources.
"""

import asyncio
import io
import json
import logging
import random
import re
import string
import sys
import yaml
import zipfile

import boto3
from botocore.exceptions import ClientError

from bedrock_agent_wrapper import BedrockAgentWrapper

# Add relative path to include demo_tools in this code example without needing to set up.
sys.path.append("../..")
import demo_tools.question as q
from demo_tools.retries import wait

REGION = "us-east-1"
ROLE_POLICY_NAME = "agent_permissions"
logger = logging.getLogger(__name__)


class BedrockAgentScenarioWrapper:
    def __init__(self, bedrock_agent_client, lambda_client, iam_resource, postfix):
        self.iam_resource = iam_resource
        self.lambda_client = lambda_client
        self.bedrock_agent_client = bedrock_agent_client
        self.postfix = postfix

        self.bedrock_wrapper = BedrockAgentWrapper(bedrock_agent_client)

        self.agent = None
        self.agent_alias = None
        self.agent_role = None
        self.agent_version = None
        self.lambda_role = None
        self.lambda_function = None

    # snippet-start:[python.example_code.bedrock.Scenario_GettingStartedBedrockAgents]
    def run_scenario(self):
        print("=" * 88)
        print("Welcome to the Amazon Bedrock Agents demo.")
        print("=" * 88)

        # Query input from user
        print("Let's start with creating an agent:")
        print("-" * 40)
        name, model_id = self._request_name_and_model_from_user()
        print("-" * 40)

        # Create an execution role for the agent
        self.agent_role = self._create_agent_role(model_id)

        # Create the agent
        self.agent = self._create_agent(name, model_id)

        # Prepare a DRAFT version of the agent
        self.agent_version = self._prepare_agent()

        # Create the lambda function
        self.lambda_function = self._create_lambda_function()

        # Configure permissions for the agent to invoke the Lambda function
        self._allow_agent_to_invoke_function(self.lambda_function)
        self._let_function_accept_invocations_from_agent(self.agent)

        # Create an action group that connects the agent to the Lambda function
        self._create_agent_action_group()

        # Prepare the DRAFT version of the agent, including the action group
        self.agent_version = self._prepare_agent()

        # Create an agent alias
        self.agent_alias = self._create_agent_alias()

        # Test the agent
        self._chat_with_agent()

        print("=" * 88)
        print("Thanks for running the demo!\n")

        if q.ask("Do you want to delete the created resources? [y/N] ", q.is_yesno):
            self._delete_resources()
            print("=" * 88)
            print("All demo resources have been deleted. Thanks again for running the demo!")
        else:
            self._list_resources()
            print("=" * 88)
            print("Thanks again for running the demo!")

    # snippet-end:[python.example_code.bedrock.Scenario_GettingStartedBedrockAgents]

    def _request_name_and_model_from_user(self):
        existing_agent_names = [agent["agentName"] for agent in self.bedrock_wrapper.list_agents()]

        while True:
            name = q.ask("Enter an agent name: ", self.is_valid_agent_name)
            if name.lower() not in [n.lower() for n in existing_agent_names]:
                break
            print(f"Agent {name} conflicts with an existing agent. Please use a different name.")

        models = ["anthropic.claude-instant-v1", "anthropic.claude-v2"]
        model_id = models[q.choose("Which foundation model would you like to use? ", models)]

        return name, model_id

    def _create_agent_role(self, model_id):
        role_name = f"AmazonBedrockExecutionRoleForAgents_{self.postfix}"
        model_arn = f"arn:aws:bedrock:{REGION}::foundation-model/{model_id}*"

        print("Creating an an execution role for the agent...")

        try:
            role = self.iam_resource.create_role(
                RoleName=role_name, AssumeRolePolicyDocument=json.dumps({
                    "Version": "2012-10-17",
                    "Statement": [{
                        "Effect": "Allow",
                        "Principal": {"Service": "bedrock.amazonaws.com"},
                        "Action": "sts:AssumeRole",
                    }],
                })
            )

            role.Policy(ROLE_POLICY_NAME).put(
                PolicyDocument=json.dumps(
                    {
                        "Version": "2012-10-17",
                        "Statement": [{
                            "Effect": "Allow",
                            "Action": "bedrock:InvokeModel",
                            "Resource": model_arn,
                        }],
                    }
                )
            )
        except ClientError as e:
            logger.error(f"Couldn't create role {role_name}. Here's why: {e}")
            raise

        return role

    def _create_agent(self, name, model_id):

        print("Creating the agent...")

        instruction = """
            You are a friendly chat bot. You have access to a function called that returns
            information about the current date and time. When responding with date or time,
            please make sure to add the timezone UTC.
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

        print("Preparing the agent...")

        agent_id = self.agent["agentId"]
        version = self.bedrock_wrapper.prepare_agent(agent_id)["agentVersion"]
        self._wait_for_agent_status(agent_id, "PREPARED")

        return version

    def _create_lambda_function(self):

        print("Creating the Lambda function...")

        function_name = f"AmazonBedrockExampleFunction_{self.postfix}"

        self.lambda_role = self._create_lambda_role()

        try:
            deployment_package = self._create_deployment_package(function_name)

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
            logger.error(f"Couldn't create Lambda function {function_name}. Here's why: {e}")
            raise

        return lambda_function

    def _create_lambda_role(self):

        print("Creating an execution role for the Lambda function...")

        role_name = f"AmazonBedrockExecutionRoleForLambda_{self.postfix}"

        try:
            role = self.iam_resource.create_role(
                RoleName=role_name, AssumeRolePolicyDocument=json.dumps({
                    "Version": "2012-10-17",
                    "Statement": [{
                        "Effect": "Allow",
                        "Principal": {"Service": "lambda.amazonaws.com"},
                        "Action": "sts:AssumeRole",
                    }],
                })
            )
            logger.info(f"Created role {role_name}")
        except ClientError as e:
            logger.error(f"Couldn't create role {role_name}. Here's why: {e}")
            raise

        print("Waiting for the execution role to be fully propagated...")
        wait(10)

        return role

    def _allow_agent_to_invoke_function(self, lambda_function):
        policy = self.iam_resource.RolePolicy(self.agent_role.role_name, ROLE_POLICY_NAME)
        doc = policy.policy_document
        doc["Statement"].append({
            "Effect": "Allow",
            "Action": "lambda:InvokeFunction",
            "Resource": lambda_function["FunctionArn"],
        })
        self.agent_role.Policy(ROLE_POLICY_NAME).put(
            PolicyDocument=json.dumps(doc)
        )

    def _let_function_accept_invocations_from_agent(self, agent):
        try:
            self.lambda_client.add_permission(
                FunctionName=self.lambda_function["FunctionName"],
                StatementId="BedrockAccess",
                Action="lambda:InvokeFunction",
                Principal="bedrock.amazonaws.com",
                SourceArn=agent["agentArn"],
            )
        except ClientError as e:
            logger.error(f"Couldn't grant Bedrock permission to invoke the Lambda function. Here's why: {e}")
            raise

    def _create_agent_action_group(self):

        print("Creating an action group for the agent...")

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

    def _create_agent_alias(self):

        print("Creating an agent alias...")

        agent_alias_name = "test_agent_alias"
        agent_alias = self.bedrock_wrapper.create_agent_alias(
            agent_alias_name,
            self.agent["agentId"]
        )

        self._wait_for_agent_status(self.agent["agentId"], "PREPARED")

        return agent_alias

    def _wait_for_agent_status(self, agent_id, status):
        while self.bedrock_wrapper.get_agent(agent_id)["agentStatus"] != status:
            wait(2)

    def _chat_with_agent(self):

        print("-" * 88)
        print("The agent is ready to chat.")
        print("Try asking for the date or time. Type 'exit' to quit.")

        while True:

            prompt = q.ask("Prompt: ", q.non_empty)

            if prompt == "exit":
                break

            response = asyncio.run(self._invoke_agent(prompt))

            print(f"Agent: {response}")

    async def _invoke_agent(self, prompt):
        client = boto3.client(
            service_name="bedrock-agent-runtime",
            region_name="us-east-1"
        )

        response = client.invoke_agent(
            agentId=self.agent["agentId"],
            agentAliasId=self.agent_alias["agentAliasId"],
            sessionId="Session",
            inputText=prompt
        )

        completion = ""

        for event in response.get("completion"):
            chunk = event["chunk"]
            completion += chunk["bytes"].decode()

        return completion

    def _delete_resources(self):
        if self.agent:
            agent_id = self.agent["agentId"]

            if self.agent_alias:
                agent_alias_id = self.agent_alias["agentAliasId"]
                agent_alias_name = self.agent_alias["agentAliasName"]
                print(f"Deleting agent alias '{agent_alias_name}'...")
                self.bedrock_wrapper.delete_agent_alias(agent_id, agent_alias_id)

            agent_name = self.agent["agentName"]
            print(f"Deleting Bedrock agent '{agent_name}'...")
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
            self.agent_role.Policy(ROLE_POLICY_NAME).delete()
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

    def _list_resources(self):
        print("-" * 40)
        print(f"Here is the list of created resources in '{REGION}'.")
        print("Make sure you delete them once you're done to avoid unnecessary costs.")
        if self.agent:
            print(f"Bedrock Agent:   {self.agent["agentName"]}")
        if self.lambda_function:
            print(f"Lambda function: {self.lambda_function["FunctionName"]}")
        if self.agent_role:
            print(f"IAM role:        {self.agent_role.role_name}")
        if self.lambda_role:
            print(f"IAM role:        {self.lambda_role.role_name}")

    @staticmethod
    def is_valid_agent_name(answer):
        valid_regex = r"^[a-zA-Z0-9_-]{1,100}$"
        return (
            answer
            if answer and len(answer) <= 100 and re.match(valid_regex, answer)
            else None,
            "I need a name for the agent, please. Valid characters are a-z, A-Z, 0-9, _ (underscore) and - (hyphen).",
        )

    @staticmethod
    def _create_deployment_package(function_name):
        buffer = io.BytesIO()
        with zipfile.ZipFile(buffer, "w") as zipped:
            zipped.write(
                "./scenario_resources/lambda_function_code.py", f"{function_name}.py"
            )
        buffer.seek(0)
        return buffer.read()


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    postfix = "".join(
        random.choice(string.ascii_lowercase + "0123456789") for _ in range(8)
    )
    scenario = BedrockAgentScenarioWrapper(
        bedrock_agent_client=boto3.client(service_name="bedrock-agent", region_name=REGION),
        lambda_client=boto3.client(service_name="lambda", region_name=REGION),
        iam_resource=boto3.resource("iam"),
        postfix=postfix,
    )
    try:
        scenario.run_scenario()
    except Exception as e:
        logging.exception(f"Something went wrong with the demo. Here's what: {e}")
