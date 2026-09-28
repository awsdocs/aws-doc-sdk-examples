# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
AWS IoT Greengrass V2 wrapper class for managing components and deployments.

This module provides a GreengrassV2Wrapper class that encapsulates AWS IoT
Greengrass V2 operations including component creation, versioning, deployment,
and lifecycle management.
"""

import json
import logging
import time
from typing import Any, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.greengrassv2.GreengrassV2Wrapper.decl]
class GreengrassV2Wrapper:
    """Encapsulates AWS IoT Greengrass V2 operations."""

    def __init__(self, greengrassv2_client: Any) -> None:
        """
        Initializes the GreengrassV2Wrapper with a Greengrass V2 client.

        :param greengrassv2_client: A Boto3 Greengrass V2 client.
        """
        self.client = greengrassv2_client

    @classmethod
    def from_client(cls) -> "GreengrassV2Wrapper":
        """
        Instantiates the wrapper with a default Boto3 Greengrass V2 client.

        :return: An instance of GreengrassV2Wrapper.
        """
        greengrassv2_client = boto3.client("greengrassv2")
        return cls(greengrassv2_client)

    @staticmethod
    def component_arn_from_version_arn(component_version_arn: str) -> str:
        """
        Derives the version-less component ARN from a component *version* ARN.

        A component version ARN has the form::

            arn:aws:greengrass:<region>:<account-id>:components:<name>:versions:<version>

        The ``list_component_versions`` API expects the version-less form::

            arn:aws:greengrass:<region>:<account-id>:components:<name>

        This helper splits on the ``:versions:`` delimiter. If the delimiter is
        not present (the input is already version-less, or has an unexpected
        shape), the original ARN is returned unchanged as a safe fallback.

        :param component_version_arn: A component version ARN.
        :return: The version-less component ARN.
        """
        return component_version_arn.rsplit(":versions:", 1)[0]

    # snippet-end:[python.example_code.greengrassv2.GreengrassV2Wrapper.decl]

    # snippet-start:[python.example_code.greengrassv2.ListCoreDevices]
    def list_core_devices(self, status: Optional[str] = None) -> list[dict[str, Any]]:
        """
        Lists Greengrass core devices registered in the account.
        Uses a paginator to handle large result sets.

        :param status: Optional filter by device status ('HEALTHY' or 'UNHEALTHY').
        :return: A list of core device dictionaries.
        """
        try:
            devices = list()
            paginator = self.client.get_paginator("list_core_devices")
            params = dict()
            if status is not None:
                params["status"] = status
            for page in paginator.paginate(**params):
                devices.extend(page.get("coreDevices", list()))
            logger.info("Listed %d core device(s).", len(devices))
            return devices
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.error(
                    "Invalid request parameters for ListCoreDevices. "
                    "Verify the status filter value is HEALTHY or UNHEALTHY. %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.greengrassv2.ListCoreDevices]

    # snippet-start:[python.example_code.greengrassv2.CreateComponentVersion]
    def create_component_version(
        self, recipe: dict[str, Any], tags: Optional[dict[str, str]] = None
    ) -> dict[str, Any]:
        """
        Creates a component version from an inline JSON recipe.

        :param recipe: A dictionary representing the component recipe.
        :param tags: Optional tags to associate with the component.
        :return: A dictionary containing the component version details.
        """
        try:
            recipe_bytes = json.dumps(recipe).encode("utf-8")
            params = dict()
            params["inlineRecipe"] = recipe_bytes
            if tags is not None:
                params["tags"] = tags
            response = self.client.create_component_version(**params)
            logger.info(
                "Created component %s version %s.",
                response.get("componentName"),
                response.get("componentVersion"),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ConflictException":
                logger.error(
                    "A component version with the same name and version already exists. "
                    "Use a different version number or delete the existing version first. %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.greengrassv2.CreateComponentVersion]

    # snippet-start:[python.example_code.greengrassv2.ListComponentVersions]
    def list_component_versions(self, component_arn: str) -> list[dict[str, Any]]:
        """
        Lists all versions of a component. Uses a paginator to retrieve all pages.

        :param component_arn: The ARN of the component (without version suffix).
        :return: A list of component version dictionaries.
        """
        try:
            versions = list()
            paginator = self.client.get_paginator("list_component_versions")
            for page in paginator.paginate(arn=component_arn):
                versions.extend(page.get("componentVersions", list()))
            logger.info(
                "Listed %d version(s) for component %s.",
                len(versions),
                component_arn,
            )
            return versions
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Component not found. Verify the component ARN is correct. %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.greengrassv2.ListComponentVersions]

    # snippet-start:[python.example_code.greengrassv2.GetComponent]
    def get_component(
        self, component_version_arn: str, recipe_output_format: str = "JSON"
    ) -> dict[str, Any]:
        """
        Gets the recipe for a specific component version.

        :param component_version_arn: The ARN of the specific component version.
        :param recipe_output_format: The format for the recipe ('JSON' or 'YAML').
        :return: A dictionary containing the recipe and metadata.
        """
        try:
            response = self.client.get_component(
                arn=component_version_arn,
                recipeOutputFormat=recipe_output_format,
            )
            logger.info("Retrieved component recipe for %s.", component_version_arn)
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Component version not found. Verify the component version ARN is correct. %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.greengrassv2.GetComponent]

    # snippet-start:[python.example_code.greengrassv2.DescribeComponent]
    def describe_component(self, component_version_arn: str) -> dict[str, Any]:
        """
        Retrieves metadata for a specific component version.

        :param component_version_arn: The ARN of the specific component version.
        :return: A dictionary containing the component metadata.
        """
        try:
            response = self.client.describe_component(arn=component_version_arn)
            logger.info(
                "Described component %s version %s.",
                response.get("componentName"),
                response.get("componentVersion"),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Component version not found. Verify the component version ARN is correct. %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.greengrassv2.DescribeComponent]

    def wait_for_component_deployable(
        self,
        component_version_arn: str,
        max_attempts: int = 20,
        delay_seconds: float = 2.0,
    ) -> bool:
        """
        Polls a component version until it reaches the DEPLOYABLE state.

        This replaces fixed sleeps with a bounded polling loop so callers do not
        rely on a component becoming ready within an arbitrary fixed time.

        :param component_version_arn: The ARN of the specific component version.
        :param max_attempts: Maximum number of polling attempts.
        :param delay_seconds: Delay between polling attempts, in seconds.
        :return: True if the component became DEPLOYABLE within the timeout;
                 otherwise False.
        """
        for _ in range(max_attempts):
            response = self.describe_component(component_version_arn)
            state = response.get("status", dict()).get("componentState")
            if state == "DEPLOYABLE":
                return True
            if state in ("FAILED", "BROKEN"):
                logger.error(
                    "Component %s entered terminal state %s.",
                    component_version_arn,
                    state,
                )
                return False
            time.sleep(delay_seconds)
        logger.warning(
            "Component %s did not become DEPLOYABLE within %d attempts.",
            component_version_arn,
            max_attempts,
        )
        return False

    # snippet-start:[python.example_code.greengrassv2.CreateDeployment]
    def create_deployment(
        self,
        target_arn: str,
        deployment_name: str,
        components: dict[str, Any],
        deployment_policies: Optional[dict[str, Any]] = None,
    ) -> dict[str, Any]:
        """
        Creates a deployment targeting a thing group or individual device.

        :param target_arn: The ARN of the target thing or thing group.
        :param deployment_name: A human-readable name for the deployment.
        :param components: A map of component names to deployment configurations.
        :param deployment_policies: Optional deployment policies (failure handling, etc.).
        :return: A dictionary containing the deployment ID and IoT job details.
        """
        try:
            params = dict()
            params["targetArn"] = target_arn
            params["deploymentName"] = deployment_name
            params["components"] = components
            if deployment_policies is not None:
                params["deploymentPolicies"] = deployment_policies
            response = self.client.create_deployment(**params)
            logger.info(
                "Created deployment %s targeting %s.",
                response.get("deploymentId"),
                target_arn,
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.error(
                    "Invalid deployment parameters. Check that the targetArn is a valid "
                    "thing or thing group ARN and component versions exist. %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.greengrassv2.CreateDeployment]

    # snippet-start:[python.example_code.greengrassv2.GetDeployment]
    def get_deployment(self, deployment_id: str) -> dict[str, Any]:
        """
        Gets the full details of a deployment.

        :param deployment_id: The ID of the deployment.
        :return: A dictionary containing deployment details.
        """
        try:
            response = self.client.get_deployment(deploymentId=deployment_id)
            logger.info(
                "Retrieved deployment %s with status %s.",
                deployment_id,
                response.get("deploymentStatus"),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Deployment not found. Verify the deployment ID is correct. %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.greengrassv2.GetDeployment]

    # snippet-start:[python.example_code.greengrassv2.ListDeployments]
    def list_deployments(
        self,
        target_arn: Optional[str] = None,
        history_filter: str = "LATEST_ONLY",
    ) -> list[dict[str, Any]]:
        """
        Lists deployments, optionally filtered by target ARN.
        Uses a paginator to retrieve all pages.

        :param target_arn: Optional ARN of the target thing or thing group.
        :param history_filter: 'ALL' or 'LATEST_ONLY' (default).
        :return: A list of deployment dictionaries.
        """
        try:
            deployments = list()
            paginator = self.client.get_paginator("list_deployments")
            params = dict()
            params["historyFilter"] = history_filter
            if target_arn is not None:
                params["targetArn"] = target_arn
            for page in paginator.paginate(**params):
                deployments.extend(page.get("deployments", list()))
            logger.info("Listed %d deployment(s).", len(deployments))
            return deployments
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                logger.error(
                    "Invalid request parameters for ListDeployments. "
                    "Verify the targetArn and historyFilter values. %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.greengrassv2.ListDeployments]

    # snippet-start:[python.example_code.greengrassv2.CancelDeployment]
    def cancel_deployment(self, deployment_id: str) -> dict[str, Any]:
        """
        Cancels an active deployment.

        :param deployment_id: The ID of the deployment to cancel.
        :return: A dictionary containing the cancellation response message.
        """
        try:
            response = self.client.cancel_deployment(deploymentId=deployment_id)
            logger.info("Canceled deployment %s.", deployment_id)
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ConflictException":
                logger.error(
                    "Deployment cannot be canceled because it is in a conflicting state "
                    "(e.g., already canceled or completed). %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.greengrassv2.CancelDeployment]

    # snippet-start:[python.example_code.greengrassv2.DeleteComponent]
    def delete_component(self, component_version_arn: str) -> None:
        """
        Deletes a specific version of a component.

        :param component_version_arn: The ARN of the component version to delete.
        """
        try:
            self.client.delete_component(arn=component_version_arn)
            logger.info("Deleted component version %s.", component_version_arn)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ConflictException":
                logger.error(
                    "Component version cannot be deleted because it is referenced by an "
                    "active deployment. Cancel or update the deployment first. %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.greengrassv2.DeleteComponent]
