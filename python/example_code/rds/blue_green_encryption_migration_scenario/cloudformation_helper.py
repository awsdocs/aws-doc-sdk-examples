# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Shows how to use the AWS SDK for Python (Boto3) with AWS CloudFormation to
    deploy, read outputs from, and delete the stack that creates the AWS KMS
    keys used by the Amazon RDS Blue/Green encryption migration scenario.

    The stack template (rds_blue_green_kms_keys_template.yaml) is synthesized
    from the AWS CDK app in resources/cdk/rds_blue_green_kms_keys.
"""

import logging
import os

import boto3
from botocore.exceptions import ClientError, WaiterError

logger = logging.getLogger(__name__)

TEMPLATE_FILE = "rds_blue_green_kms_keys_template.yaml"
DEFAULT_TEMPLATE_PATH = os.path.join(
    os.path.dirname(os.path.abspath(__file__)), TEMPLATE_FILE
)


# snippet-start:[python.example_code.rds.BlueGreenCloudFormationHelper]
class CloudFormationHelper:
    """Encapsulates the AWS CloudFormation operations used by the scenario."""

    def __init__(self, cf_client, template_path: str = DEFAULT_TEMPLATE_PATH):
        """
        Initializes the CloudFormationHelper.

        :param cf_client: A Boto3 AWS CloudFormation client.
        :param template_path: The path to the CloudFormation template file.
        """
        self.cf_client = cf_client
        self.template_path = template_path

    @classmethod
    def from_client(cls) -> "CloudFormationHelper":
        """
        Creates a CloudFormationHelper using a default CloudFormation client.

        :return: A new CloudFormationHelper instance.
        """
        return cls(boto3.client("cloudformation"))

    def stack_exists(self, stack_name: str) -> bool:
        """
        Checks whether a CloudFormation stack exists.

        :param stack_name: The name of the stack.
        :return: True when the stack exists; otherwise, False.
        """
        try:
            response = self.cf_client.describe_stacks(StackName=stack_name)
            return len(response.get("Stacks", [])) > 0
        except ClientError as err:
            # CloudFormation returns ValidationError when the stack doesn't exist.
            if err.response["Error"]["Code"] == "ValidationError":
                return False
            logger.error(
                "Couldn't check whether stack '%s' exists. Here's why: %s: %s",
                stack_name,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    def deploy_stack(self, stack_name: str) -> bool:
        """
        Deploys the CloudFormation stack if it doesn't already exist, and waits
        for the stack creation to complete.

        :param stack_name: The name of the stack.
        :return: True when the stack was created; False when it already existed.
        """
        if self.stack_exists(stack_name):
            logger.info("Stack '%s' already exists.", stack_name)
            return False

        with open(self.template_path, encoding="utf-8") as template_file:
            template_body = template_file.read()

        try:
            response = self.cf_client.create_stack(
                StackName=stack_name,
                TemplateBody=template_body,
                Capabilities=["CAPABILITY_IAM"],
            )
            logger.info(
                "Stack creation requested for '%s'. Stack ID: %s",
                stack_name,
                response["StackId"],
            )
            waiter = self.cf_client.get_waiter("stack_create_complete")
            waiter.wait(
                StackName=stack_name, WaiterConfig={"Delay": 10, "MaxAttempts": 60}
            )
            logger.info("Stack '%s' created successfully.", stack_name)
            return True
        except ClientError as err:
            logger.error(
                "Couldn't create stack '%s'. Here's why: %s: %s",
                stack_name,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise
        except WaiterError as err:
            logger.error(
                "Stack creation did not complete for '%s'. Here's why: %s",
                stack_name,
                err,
            )
            raise

    def get_stack_outputs(self, stack_name: str) -> dict:
        """
        Gets the outputs of a CloudFormation stack.

        :param stack_name: The name of the stack.
        :return: A dictionary that maps output keys to output values.
        """
        try:
            response = self.cf_client.describe_stacks(StackName=stack_name)
        except ClientError as err:
            logger.error(
                "Couldn't get outputs for stack '%s'. Here's why: %s: %s",
                stack_name,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise
        stacks = response.get("Stacks", [])
        if not stacks:
            raise RuntimeError(f"Stack not found: {stack_name}")
        return {
            output["OutputKey"]: output["OutputValue"]
            for output in stacks[0].get("Outputs", [])
        }

    def destroy_stack(self, stack_name: str) -> None:
        """
        Deletes a CloudFormation stack and waits for the deletion to complete.

        :param stack_name: The name of the stack.
        """
        try:
            self.cf_client.delete_stack(StackName=stack_name)
            logger.info("Stack deletion requested for '%s'.", stack_name)
            waiter = self.cf_client.get_waiter("stack_delete_complete")
            waiter.wait(
                StackName=stack_name, WaiterConfig={"Delay": 10, "MaxAttempts": 60}
            )
            logger.info("Stack '%s' deleted successfully.", stack_name)
        except ClientError as err:
            logger.error(
                "Couldn't delete stack '%s'. Here's why: %s: %s",
                stack_name,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise
        except WaiterError as err:
            logger.error(
                "Stack deletion did not complete for '%s'. Here's why: %s",
                stack_name,
                err,
            )
            raise


# snippet-end:[python.example_code.rds.BlueGreenCloudFormationHelper]
