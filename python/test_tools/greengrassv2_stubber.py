# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS IoT Greengrass V2 unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from botocore.stub import ANY
from boto3 import client
from datetime import datetime

from test_tools.example_stubber import ExampleStubber


class GreengrassV2Stubber(ExampleStubber):
    """
    A class that implements stub functions used by AWS IoT Greengrass V2 unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, greengrassv2_client: client, use_stubs=True) -> None:
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param greengrassv2_client: A Boto 3 AWS IoT Greengrass V2 client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(greengrassv2_client, use_stubs)

    def stub_list_core_devices(
        self, core_devices: list, status: str = None, error_code: str = None
    ) -> None:
        """Stub the list_core_devices function."""
        expected_params = {}
        if status is not None:
            expected_params["status"] = status
        response = {"coreDevices": core_devices}
        self._stub_bifurcator(
            "list_core_devices", expected_params, response, error_code=error_code
        )

    def stub_create_component_version(
        self,
        arn: str,
        component_name: str,
        component_version: str,
        error_code: str = None,
    ) -> None:
        """Stub the create_component_version function."""
        expected_params = {"inlineRecipe": ANY}
        response = {
            "arn": arn,
            "componentName": component_name,
            "componentVersion": component_version,
            "creationTimestamp": datetime(2026, 1, 1),
            "status": {"componentState": "REQUESTED"},
        }
        self._stub_bifurcator(
            "create_component_version",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_list_component_versions(
        self, arn: str, component_versions: list, error_code: str = None
    ) -> None:
        """Stub the list_component_versions function."""
        expected_params = {"arn": arn}
        response = {"componentVersions": component_versions}
        self._stub_bifurcator(
            "list_component_versions",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_get_component(
        self,
        arn: str,
        recipe: bytes,
        recipe_output_format: str = "JSON",
        error_code: str = None,
    ) -> None:
        """Stub the get_component function."""
        expected_params = {"arn": arn, "recipeOutputFormat": recipe_output_format}
        response = {"recipeOutputFormat": recipe_output_format, "recipe": recipe}
        self._stub_bifurcator(
            "get_component", expected_params, response, error_code=error_code
        )

    def stub_describe_component(
        self,
        arn: str,
        component_name: str,
        component_version: str,
        component_state: str = "DEPLOYABLE",
        error_code: str = None,
    ) -> None:
        """Stub the describe_component function."""
        expected_params = {"arn": arn}
        response = {
            "arn": arn,
            "componentName": component_name,
            "componentVersion": component_version,
            "status": {"componentState": component_state},
        }
        self._stub_bifurcator(
            "describe_component", expected_params, response, error_code=error_code
        )

    def stub_create_deployment(
        self,
        target_arn: str,
        deployment_name: str,
        components: dict,
        deployment_id: str,
        error_code: str = None,
    ) -> None:
        """Stub the create_deployment function."""
        expected_params = {
            "targetArn": target_arn,
            "deploymentName": deployment_name,
            "components": components,
        }
        response = {"deploymentId": deployment_id, "iotJobId": "job-123"}
        self._stub_bifurcator(
            "create_deployment", expected_params, response, error_code=error_code
        )

    def stub_get_deployment(
        self,
        deployment_id: str,
        deployment_status: str = "ACTIVE",
        error_code: str = None,
    ) -> None:
        """Stub the get_deployment function."""
        expected_params = {"deploymentId": deployment_id}
        response = {
            "deploymentId": deployment_id,
            "deploymentStatus": deployment_status,
        }
        self._stub_bifurcator(
            "get_deployment", expected_params, response, error_code=error_code
        )

    def stub_list_deployments(
        self,
        deployments: list,
        target_arn: str = None,
        history_filter: str = "LATEST_ONLY",
        error_code: str = None,
    ) -> None:
        """Stub the list_deployments function."""
        expected_params = {"historyFilter": history_filter}
        if target_arn is not None:
            expected_params["targetArn"] = target_arn
        response = {"deployments": deployments}
        self._stub_bifurcator(
            "list_deployments", expected_params, response, error_code=error_code
        )

    def stub_cancel_deployment(
        self, deployment_id: str, message: str = "Canceled.", error_code: str = None
    ) -> None:
        """Stub the cancel_deployment function."""
        expected_params = {"deploymentId": deployment_id}
        response = {"message": message}
        self._stub_bifurcator(
            "cancel_deployment", expected_params, response, error_code=error_code
        )

    def stub_delete_component(self, arn: str, error_code: str = None) -> None:
        """Stub the delete_component function."""
        expected_params = {"arn": arn}
        response = {}
        self._stub_bifurcator(
            "delete_component", expected_params, response, error_code=error_code
        )
