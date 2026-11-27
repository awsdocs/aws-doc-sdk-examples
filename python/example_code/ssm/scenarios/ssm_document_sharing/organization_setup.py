# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Helper class that prepares the AWS Organizations resources needed by the SSM
document sharing scenario. It can:

* Find out whether the current account belongs to an organization.
* Use an AWS CloudFormation stack to create an organization (only if none exists)
  and two organizational units (OUs).
* Turn on AWS Resource Access Manager (RAM) sharing with the organization.
* Delete the stack when the scenario is done.
"""

import json
import logging
import uuid
from typing import Any, Dict, Optional

import boto3
from botocore.exceptions import ClientError, WaiterError

logger = logging.getLogger(__name__)


class OrganizationSetup:
    """Creates and removes the organization resources used by the scenario."""

    def __init__(
        self,
        organizations_client: boto3.client,
        cloudformation_client: boto3.client,
        ram_client: boto3.client,
    ):
        """
        :param organizations_client: A Boto3 AWS Organizations client.
        :param cloudformation_client: A Boto3 AWS CloudFormation client.
        :param ram_client: A Boto3 AWS RAM client.
        """
        self.organizations_client = organizations_client
        self.cloudformation_client = cloudformation_client
        self.ram_client = ram_client

    @classmethod
    def from_client(cls) -> "OrganizationSetup":
        """
        Creates an OrganizationSetup instance with default clients.

        :return: An instance of OrganizationSetup.
        """
        return cls(
            boto3.client("organizations"),
            boto3.client("cloudformation"),
            boto3.client("ram"),
        )

    def get_organization(self) -> Optional[Dict[str, Any]]:
        """
        Gets the organization that the current account belongs to.

        :return: The organization description, or None if the account is not part
                 of an organization.
        :raise ClientError: If the call fails for any reason other than the account
                            not being in an organization.
        """
        try:
            return self.organizations_client.describe_organization()["Organization"]
        except ClientError as err:
            if err.response["Error"]["Code"] == "AWSOrganizationsNotInUseException":
                return None
            logger.error(
                "Couldn't describe the organization. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    def get_root_id(self) -> str:
        """
        Gets the ID of the root of the current organization.

        :return: The root ID.
        """
        try:
            return self.organizations_client.list_roots()["Roots"][0]["Id"]
        except ClientError as err:
            logger.error(
                "Couldn't list the organization roots. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    @staticmethod
    def _build_template(existing_root_id: Optional[str]) -> Dict[str, Any]:
        """
        Builds the CloudFormation template.

        When existing_root_id is None the template creates an organization and two
        OUs under its root. Otherwise it creates only the two OUs under the
        specified root of the existing organization.

        :param existing_root_id: The root ID of an existing organization, or None.
        :return: The template as a dictionary.
        """
        suffix = uuid.uuid4().hex[:8]
        resources: Dict[str, Any] = dict()
        if existing_root_id is None:
            resources["Organization"] = {
                "Type": "AWS::Organizations::Organization",
                "Properties": {"FeatureSet": "ALL"},
            }
            parent_id: Any = {"Fn::GetAtt": ["Organization", "RootId"]}
        else:
            parent_id = existing_root_id

        for logical_id, name in (
            ("PrimaryOu", f"SsmSharingPrimary-{suffix}"),
            ("SecondaryOu", f"SsmSharingSecondary-{suffix}"),
        ):
            resources[logical_id] = {
                "Type": "AWS::Organizations::OrganizationalUnit",
                "Properties": {"Name": name, "ParentId": parent_id},
            }

        return {
            "AWSTemplateFormatVersion": "2010-09-09",
            "Description": "Organization resources for the SSM document sharing scenario.",
            "Resources": resources,
            "Outputs": {
                "PrimaryOuArn": {"Value": {"Fn::GetAtt": ["PrimaryOu", "Arn"]}},
                "SecondaryOuArn": {"Value": {"Fn::GetAtt": ["SecondaryOu", "Arn"]}},
            },
        }

    def deploy_stack(
        self, stack_name: str, existing_root_id: Optional[str] = None
    ) -> Dict[str, str]:
        """
        Creates a stack and waits for it to finish.

        :param stack_name: The name of the stack.
        :param existing_root_id: The root ID of an existing organization. If None,
                                 the stack also creates the organization.
        :return: The stack outputs, keyed by output name.
        :raise ClientError: If the stack can't be created.
        :raise WaiterError: If the stack doesn't reach CREATE_COMPLETE.
        """
        template = self._build_template(existing_root_id)
        try:
            self.cloudformation_client.create_stack(
                StackName=stack_name, TemplateBody=json.dumps(template)
            )
            print(f"Creating stack '{stack_name}'. This can take a few minutes...")
            self.cloudformation_client.get_waiter("stack_create_complete").wait(
                StackName=stack_name, WaiterConfig={"Delay": 5, "MaxAttempts": 60}
            )
            stack = self.cloudformation_client.describe_stacks(StackName=stack_name)[
                "Stacks"
            ][0]
        except ClientError as err:
            logger.error(
                "Couldn't create stack %s. Here's why: %s: %s",
                stack_name,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise
        except WaiterError as err:
            logger.error("Stack %s didn't finish creating: %s", stack_name, err)
            raise
        return {o["OutputKey"]: o["OutputValue"] for o in stack.get("Outputs", list())}

    def delete_stack(self, stack_name: str) -> None:
        """
        Deletes a stack and waits for it to finish. If the stack created the
        organization, the organization is deleted with it.

        :param stack_name: The name of the stack.
        :raise ClientError: If the stack can't be deleted.
        :raise WaiterError: If the stack doesn't reach DELETE_COMPLETE.
        """
        try:
            self.cloudformation_client.delete_stack(StackName=stack_name)
            self.cloudformation_client.get_waiter("stack_delete_complete").wait(
                StackName=stack_name, WaiterConfig={"Delay": 5, "MaxAttempts": 60}
            )
        except ClientError as err:
            logger.error(
                "Couldn't delete stack %s. Here's why: %s: %s",
                stack_name,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise
        except WaiterError as err:
            logger.error("Stack %s didn't finish deleting: %s", stack_name, err)
            raise

    def enable_ram_sharing(self) -> bool:
        """
        Turns on RAM sharing with AWS Organizations. This must be called from the
        organization's management account.

        :return: True if sharing is enabled.
        :raise ClientError: If the call fails.
        """
        try:
            response = self.ram_client.enable_sharing_with_aws_organization()
        except ClientError as err:
            logger.error(
                "Couldn't enable RAM sharing with the organization. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise
        return response["returnValue"]
