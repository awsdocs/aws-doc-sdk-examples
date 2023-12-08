import io
import json
import logging
import sys
import zipfile

import boto3
from botocore.exceptions import ClientError

# Add relative path to include demo_tools in this code example without needing to set up.
sys.path.append("../..")
import demo_tools.retries as r

logger = logging.getLogger(__name__)


class LambdaFunction:
    created_resources = {}
    policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"

    def __init__(self, lambda_client, iam, postfix):
        self.lambda_client = lambda_client
        self.iam = iam
        self.role_name = "AmazonBedrockExecutionRoleForLambda_" + postfix
        self.function_name = "AmazonBedrockExampleFunction_" + postfix

    def create(self):
        role = self._create_iam_role()
        print(role)

        function_arn = self._create_function(role)

        return function_arn

    def add_permission(self, agent_arn):
        try:
            self.lambda_client.add_permission(
                FunctionName=self.function_name,
                StatementId="BedrockAccess",
                Action="lambda:InvokeFunction",
                Principal="bedrock.amazonaws.com",
                SourceArn=agent_arn
            )
        except ClientError as e:
            logger.error(f"Couldn't add Bedrock permission to the Lambda function. Here's why: {e}")
            raise


    def delete_created_resources(self):
        print(f"Deleting role '{self.role_name}'...")
        try:
            role = self.iam.Role(self.role_name)
            for policy in role.attached_policies.all():
                policy.detach_role(RoleName=self.role_name)
            role.delete()
        except ClientError as e:
            logger.error(f"Couldn't delete role {self.role_name}. Here's why: {e}")
            raise

        print(f"Deleting Lambda function '{self.role_name}'...")
        try:
            self.lambda_client.delete_function(FunctionName=self.function_name)
        except ClientError as e:
            logger.error(f"Couldn't delete function {self.function_name}. Here's why: {e}")
            raise

    def _create_iam_role(self):
        print("Creating execution role for Lambda function...")
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
            role = self.iam.create_role(
                RoleName=self.role_name,
                AssumeRolePolicyDocument=json.dumps(trust_policy),
            )
            role.attach_policy(PolicyArn=self.policy_arn)
            r.wait(10)
        except ClientError as e:
            logger.error(f"Couldn't create role or attach policy. Here's why: {e}")
            raise
        else:
            return role

    def _create_function(self, role):
        print("Creating Lambda function...")

        deployment_package = self._create_deployment_package(
            "./scenario_resources/lambda_function_code.py", f"{self.function_name}.py"
        )

        try:
            response = self.lambda_client.create_function(
                FunctionName=self.function_name,
                Description="Lambda function for Amazon bedrock example",
                Runtime="python3.11",
                Role=role.arn,
                Handler=f"{self.function_name}.lambda_handler",
                Code={"ZipFile": deployment_package},
                Publish=True,
            )
            function_arn = response["FunctionArn"]
            waiter = self.lambda_client.get_waiter("function_active_v2")
            waiter.wait(FunctionName=self.function_name)
            print(f"Created function with ARN '{function_arn}'.")
        except ClientError as e:
            logger.error(f"Couldn't create function {self.function_name}. Here's why: {e}")
            raise
        else:
            return function_arn

    @staticmethod
    def _create_deployment_package(source_file, destination_file):
        buffer = io.BytesIO()
        with zipfile.ZipFile(buffer, "w") as zipped:
            zipped.write(source_file, destination_file)
        buffer.seek(0)
        return buffer.read()
