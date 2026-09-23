# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the AWS IoT Greengrass V2 basics scenario.

These tests create real AWS resources and verify that the wrapper methods
and scenario flow work correctly against the live service. Resources are
cleaned up in finally blocks to prevent leaks.

Run with:  pytest test_greengrassv2_basics.py -v
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import json
import time
import uuid

import boto3
import pytest
from botocore.exceptions import ClientError

from greengrassv2_wrapper import GreengrassV2Wrapper

COMPONENT_NAME = "com.example.GreengrassBasicsTest"


@pytest.fixture(scope="module")
def greengrassv2_client():
    """Create a shared Greengrass V2 client for all tests."""
    return boto3.client("greengrassv2")


@pytest.fixture(scope="module")
def iot_client():
    """Create a shared IoT client for thing group management."""
    return boto3.client("iot")


@pytest.fixture(scope="module")
def wrapper(greengrassv2_client):
    """Create a wrapper instance for all tests."""
    return GreengrassV2Wrapper(greengrassv2_client)


@pytest.fixture(scope="module")
def unique_suffix():
    """Generate a unique suffix used across the test module."""
    return str(uuid.uuid4())[:8]


def _build_test_recipe(version: str, suffix: str) -> dict:
    """Builds a test recipe."""
    return {
        "RecipeFormatVersion": "2020-01-25",
        "ComponentName": f"{COMPONENT_NAME}-{suffix}",
        "ComponentVersion": version,
        "ComponentDescription": f"Test component v{version}",
        "ComponentPublisher": "Integration Tests",
        "ComponentConfiguration": {
            "DefaultConfiguration": {
                "Message": f"Test message v{version}"
            }
        },
        "Manifests": [
            {
                "Platform": {"os": "linux"},
                "Lifecycle": {
                    "run": 'echo "{configuration:/Message}"'
                },
            }
        ],
    }


@pytest.mark.integ
class TestGreengrassV2Wrapper:
    """Integration tests for the GreengrassV2Wrapper methods."""

    @pytest.mark.integ
    def test_list_core_devices(self, wrapper):
        """Test listing core devices (may return empty list)."""
        devices = wrapper.list_core_devices()
        assert isinstance(devices, list)

    @pytest.mark.integ
    def test_list_core_devices_with_status_filter(self, wrapper):
        """Test listing core devices with a status filter."""
        devices = wrapper.list_core_devices(status="HEALTHY")
        assert isinstance(devices, list)

    @pytest.mark.integ
    def test_component_lifecycle(self, wrapper, unique_suffix):
        """
        Test creating, listing, getting, describing, and deleting components.
        This tests the full component lifecycle in one test to ensure proper
        ordering and cleanup.
        """
        component_full_name = f"{COMPONENT_NAME}-{unique_suffix}"
        v1_arn = None
        v2_arn = None

        try:
            # Create component v1.0.0
            recipe_v1 = _build_test_recipe("1.0.0", unique_suffix)
            response_v1 = wrapper.create_component_version(recipe_v1)
            v1_arn = response_v1.get("arn")
            assert v1_arn is not None
            assert response_v1.get("componentName") == component_full_name
            assert response_v1.get("componentVersion") == "1.0.0"

            # Create component v2.0.0
            recipe_v2 = _build_test_recipe("2.0.0", unique_suffix)
            response_v2 = wrapper.create_component_version(recipe_v2)
            v2_arn = response_v2.get("arn")
            assert v2_arn is not None
            assert response_v2.get("componentVersion") == "2.0.0"

            # Derive component ARN (without version) for listing
            component_arn = v1_arn.rsplit(":versions:", 1)[0]

            # List component versions
            versions = wrapper.list_component_versions(component_arn)
            assert len(versions) >= 2
            version_strings = [v.get("componentVersion") for v in versions]
            assert "1.0.0" in version_strings
            assert "2.0.0" in version_strings

            # Get component recipe for v2.0.0
            get_response = wrapper.get_component(v2_arn, recipe_output_format="JSON")
            assert get_response.get("recipeOutputFormat") == "JSON"
            recipe_blob = get_response.get("recipe")
            assert recipe_blob is not None
            if isinstance(recipe_blob, bytes):
                recipe_text = recipe_blob.decode("utf-8")
            else:
                recipe_text = str(recipe_blob)
            recipe_dict = json.loads(recipe_text)
            assert recipe_dict.get("ComponentName") == component_full_name

            # Describe component v2.0.0
            describe_response = wrapper.describe_component(v2_arn)
            assert describe_response.get("componentName") == component_full_name
            assert describe_response.get("componentVersion") == "2.0.0"
            assert "status" in describe_response

        finally:
            # Cleanup: delete both component versions
            if v2_arn is not None:
                try:
                    wrapper.delete_component(v2_arn)
                except ClientError:
                    pass
            if v1_arn is not None:
                try:
                    wrapper.delete_component(v1_arn)
                except ClientError:
                    pass

    @pytest.mark.integ
    def test_deployment_lifecycle(self, wrapper, iot_client, unique_suffix):
        """
        Test creating, getting, listing, and canceling a deployment.
        Creates a component and thing group, deploys the component,
        then cleans everything up.
        """
        component_full_name = f"{COMPONENT_NAME}-{unique_suffix}-deploy"
        thing_group_name = f"TestGGGroup-{unique_suffix}"
        thing_group_arn = None
        comp_arn = None
        deployment_id = None

        try:
            # Create a thing group
            tg_response = iot_client.create_thing_group(
                thingGroupName=thing_group_name
            )
            thing_group_arn = tg_response["thingGroupArn"]

            # Create a component for deployment
            recipe = {
                "RecipeFormatVersion": "2020-01-25",
                "ComponentName": component_full_name,
                "ComponentVersion": "1.0.0",
                "ComponentDescription": "Deployment test component",
                "ComponentPublisher": "Integration Tests",
                "ComponentConfiguration": {
                    "DefaultConfiguration": {"Message": "Deploy test"}
                },
                "Manifests": [
                    {
                        "Platform": {"os": "linux"},
                        "Lifecycle": {
                            "run": 'echo "{configuration:/Message}"'
                        },
                    }
                ],
            }
            comp_response = wrapper.create_component_version(recipe)
            comp_arn = comp_response.get("arn")

            # Allow time for the component to become DEPLOYABLE
            time.sleep(3)

            # Create a deployment
            components = {
                component_full_name: {
                    "componentVersion": "1.0.0",
                    "configurationUpdate": {
                        "merge": json.dumps({"Message": "Custom deploy message"})
                    },
                }
            }
            deployment_policies = {
                "failureHandlingPolicy": "ROLLBACK",
                "componentUpdatePolicy": {
                    "action": "NOTIFY_COMPONENTS",
                    "timeoutInSeconds": 60,
                },
            }
            deploy_response = wrapper.create_deployment(
                target_arn=thing_group_arn,
                deployment_name="TestDeployment",
                components=components,
                deployment_policies=deployment_policies,
            )
            deployment_id = deploy_response.get("deploymentId")
            assert deployment_id is not None

            # Get deployment details
            get_deploy = wrapper.get_deployment(deployment_id)
            assert get_deploy.get("deploymentId") == deployment_id
            assert get_deploy.get("deploymentStatus") in [
                "ACTIVE",
                "COMPLETED",
                "INACTIVE",
            ]

            # List deployments for the thing group
            deployments = wrapper.list_deployments(
                target_arn=thing_group_arn,
                history_filter="ALL",
            )
            assert len(deployments) >= 1
            deployment_ids = [d.get("deploymentId") for d in deployments]
            assert deployment_id in deployment_ids

            # Cancel the deployment
            cancel_response = wrapper.cancel_deployment(deployment_id)
            assert cancel_response is not None

        finally:
            # Cleanup: delete component, thing group
            if comp_arn is not None:
                try:
                    wrapper.delete_component(comp_arn)
                except ClientError:
                    pass
            if thing_group_name is not None:
                try:
                    iot_client.delete_thing_group(
                        thingGroupName=thing_group_name
                    )
                except ClientError:
                    pass

    @pytest.mark.integ
    def test_list_component_versions_not_found(self, wrapper):
        """Test that listing versions for a nonexistent component raises an error."""
        fake_arn = (
            "arn:aws:greengrass:us-east-1:000000000000:components:"
            "com.example.DoesNotExist"
        )
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_component_versions(fake_arn)
        assert (
            exc_info.value.response["Error"]["Code"] == "ResourceNotFoundException"
            or exc_info.value.response["Error"]["Code"] == "AccessDeniedException"
        )
