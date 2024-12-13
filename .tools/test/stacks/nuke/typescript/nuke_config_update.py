# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Python class responsible for updating the nuke generic config, based on exceptions to be filtered,
and also updates dynamically the region attribute passed in from the StepFunctions invocation.
This should be modified to suit your needs.
"""

import argparse
import copy
import logging
from typing import Any, Dict, List, Tuple

import boto3
import yaml
from boto3.exceptions import ClientError

GLOBAL_RESOURCE_EXCEPTIONS = [
    {"property": "tag:DoNotNuke", "value": "True"},
    {"property": "tag:Permanent", "value": "True"},
    {"type": "regex", "value": ".*auto-account-cleanser*.*"},
    {"type": "regex", "value": ".*nuke-account-cleanser*.*"},
    {"type": "regex", "value": ".*securityhub*.*"},
    {"type": "regex", "value": ".*aws-prod*.*"},
]


class StackInfo:
    """
    Class responsible for managing StackInfo operations.

    Attributes:
        session (boto3.Session): AWS session object.
        regions (List[str]): List of target regions.
        resources (Dict[str, List[Dict[str, str]]]): Dictionary of resources and their exceptions.
        config (Dict[str, Any]): Configuration dictionary.
        account (str): AWS account ID.
    """

    def __init__(self, account: str, target_regions: List[str]) -> None:
        """
        Initialize StackInfo object.

        Args:
            account (str): AWS account ID.
            target_regions (List[str]): List of target regions.
        """
        self.session = boto3.Session(profile_name="nuke")
        self.regions = target_regions
        self.resources: Dict[str, List[Dict[str, str]]] = {}
        self.config: Dict[str, Any] = {}
        self.account = account

    def populate(self) -> None:
        """
        Populate resources and override the default configuration.
        """
        self.update_cfn_stack_list()
        self.override_default_config()

    def update_cfn_stack_list(self) -> None:
        """
        Update the list of CloudFormation stacks and resources.
        """
        try:
            for region in self.regions:
                cfn_client = self.session.client("cloudformation", region_name=region)
                stack_paginator = cfn_client.get_paginator("list_stacks")
                responses = stack_paginator.paginate(
                    StackStatusFilter=[
                        "CREATE_COMPLETE",
                        "UPDATE_COMPLETE",
                        "UPDATE_ROLLBACK_COMPLETE",
                        "IMPORT_COMPLETE",
                        "IMPORT_ROLLBACK_COMPLETE",
                    ]
                )
                for page in responses:
                    for stack in page.get("StackSummaries", []):
                        self.get_cfn_resources(stack, cfn_client)
                self.build_iam_exclusion_list(region)
        except ClientError as e:
            logging.error(f"Error in calling update_cfn_stack_list: {e}")

    def get_cfn_resources(self, stack: Dict[str, Any], cfn_client) -> None:
        """
        Get resources from a CloudFormation stack.

        Args:
            stack (Dict[str, Any]): CloudFormation stack details.
            cfn_client: CloudFormation client object.
        """
        try:
            stack_name = stack.get("StackName") or stack.get("PhysicalResourceId")

            if stack_name is None:
                return

            stack_description = cfn_client.describe_stacks(StackName=stack_name)
            tags = stack_description.get("Stacks", [{}])[0].get("Tags", [])
            for tag in tags:
                key = tag.get("Key")
                value = tag.get("Value")
                if key == "AWS_Solutions" and value == "LandingZoneStackSet":
                    if "CloudFormationStack" in self.resources:
                        self.resources["CloudFormationStack"].append(
                            stack.get("StackName")
                        )
                    else:
                        self.resources["CloudFormationStack"] = [stack.get("StackName")]
                    stack_resources = cfn_client.list_stack_resources(
                        StackName=stack_name
                    )
                    for resource in stack_resources.get("StackResourceSummaries", []):
                        if resource.get("ResourceType") == "AWS::CloudFormation::Stack":
                            self.get_cfn_resources(resource, cfn_client)
                        else:
                            nuke_type = self.update_resource_name(
                                resource["ResourceType"]
                            )
                            if nuke_type in self.resources:
                                self.resources[nuke_type].append(
                                    {
                                        "type": "regex",
                                        "value": resource["PhysicalResourceId"],
                                    }
                                )
                            else:
                                self.resources[nuke_type] = [
                                    {
                                        "type": "regex",
                                        "value": resource["PhysicalResourceId"],
                                    }
                                ]
        except ClientError as e:
            logging.error(f"Error calling get_cfn_resources: {e}")

    def update_resource_name(self, resource: str) -> str:
        """
        Update the resource name to match the nuke resource type.

        Args:
            resource (str): Resource name.

        Returns:
            str: Updated resource name.
        """
        nuke_type = resource.replace("AWS::", "")
        nuke_type = nuke_type.replace("::", "")
        nuke_type = nuke_type.replace("Config", "ConfigService", 1)
        return nuke_type

    def build_iam_exclusion_list(self, region: str) -> None:
        """
        Build the IAM role exclusion list for the given region.

        Args:
            region (str): AWS region.
        """
        try:
            iam_client = self.session.client("iam", region_name=region)
            iam_paginator = iam_client.get_paginator("list_roles")
            responses = iam_paginator.paginate()
            for page in responses:
                for role in page.get("Roles", []):
                    assume_role_policy_document = role.get("AssumeRolePolicyDocument")
                    if assume_role_policy_document:
                        for statement in assume_role_policy_document.get(
                            "Statement", []
                        ):
                            if statement.get("Principal", {}).get("Federated"):
                                if "IAMRole" in self.resources:
                                    self.resources["IAMRole"].append(
                                        role.get("RoleName")
                                    )
                                else:
                                    self.resources["IAMRole"] = [role.get("RoleName")]
        except ClientError as e:
            logging.error(f"Error building IAM exclusion list: {e}")

    def override_default_config(self) -> None:
        """
        Override the default configuration with captured resources and exclusions.
        """
        try:
            with open("nuke_generic_config.yaml") as config_file:
                self.config = yaml.safe_load(config_file)

                # Not all resources handled by the tool, but we will add them to the exclusion anyhow.
                for resource, exceptions in self.resources.items():
                    account_filters = self.config["accounts"]["ACCOUNT"]["filters"]
                    if resource in account_filters:
                        account_filters[resource].extend(exceptions)
                    else:
                        account_filters[resource] = exceptions

                self.config["accounts"][self.account] = copy.deepcopy(
                    self.config["accounts"]["ACCOUNT"]
                )
                self.config["accounts"].pop("ACCOUNT", None)

            # Global exclusions apply to every type of resource
            for resource, exceptions in self.config["accounts"][self.account][
                "filters"
            ].items():
                for exception in GLOBAL_RESOURCE_EXCEPTIONS:
                    exceptions.append(exception.copy())
        except ClientError as e:
            logging.error(f"Failed merging nuke-config-test.yaml with error {e}")

    def write_config(self) -> None:
        """
        Write the configuration to separate files for each target region.
        """
        try:
            for region in self.config["regions"]:
                local_config = self.config.copy()
                local_config["regions"] = [region]
                filename = f"nuke_config_{region}.yaml"
                with open(filename, "w") as output_file:
                    yaml.safe_dump(local_config, output_file)
                logging.info(f"Successfully wrote config to {filename}")
        except KeyError:
            logging.error("No 'regions' key found in the config dictionary")
        except ClientError as e:
            logging.error(f"An unexpected error occurred: {e}")


def parse_arguments() -> Tuple[str, str]:
    """
    Parse command-line arguments.

    Returns:
        Tuple[str, str]: AWS account ID and target region.
    """
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--account", dest="account", help="Account to nuke", required=True
    )
    parser.add_argument(
        "--region", dest="region", help="Region to target for nuke", required=True
    )
    args = parser.parse_args()
    return args.account, args.region


def main() -> None:
    """
    Main entry point of the script.
    """
    account, region = parse_arguments()
    stack_info = StackInfo(account, [region])
    stack_info.populate()
    stack_info.write_config()


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    main()
