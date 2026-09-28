# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
AWS IoT Greengrass V2 Basics Scenario

This scenario walks through the complete lifecycle of managing Greengrass
components and deployments from the cloud:

1. List Greengrass core devices
2. Create a component version (v1.0.0)
3. Create a component version (v2.0.0)
4. List component versions
5. Get component recipe
6. Describe component metadata
7. Create a deployment
8. Get deployment details
9. List deployments
10. Cancel deployment
11. Clean up (delete components, delete thing group)

No physical Greengrass core device is required — the scenario focuses on
cloud-side management API operations.
"""

import json
import logging
import os
import sys
import uuid
from typing import Any

import boto3
from botocore.exceptions import ClientError

# Add the repo's `python` folder to the path so demo_tools can be imported
# without a package install, matching the pattern used by comparable examples.
sys.path.append(os.path.join(os.path.dirname(__file__), "..", "..", ".."))
from demo_tools import question as q

from greengrassv2_wrapper import GreengrassV2Wrapper

logger = logging.getLogger(__name__)

COMPONENT_NAME = "com.example.GreengrassBasics"
DASHES = "-" * 80


# snippet-start:[python.example_code.greengrassv2.GreengrassV2Scenario]
class GreengrassV2Scenario:
    """Runs an interactive scenario demonstrating Greengrass V2 operations."""

    def __init__(
        self,
        greengrassv2_wrapper: GreengrassV2Wrapper,
        iot_client: Any,
    ) -> None:
        """
        :param greengrassv2_wrapper: An instance of GreengrassV2Wrapper.
        :param iot_client: A Boto3 IoT client for managing thing groups.
        """
        self.wrapper = greengrassv2_wrapper
        self.iot_client = iot_client
        self.thing_group_name = None
        self.thing_group_arn = None
        self.v1_arn = None
        self.v2_arn = None
        self.component_arn = None
        self.deployment_id = None

    def run_scenario(self) -> None:
        """Runs the full Greengrass V2 basics scenario."""
        print(DASHES)
        print("Welcome to the AWS IoT Greengrass V2 Basics Scenario.")
        print(
            "This scenario demonstrates the component and deployment lifecycle "
            "in Greengrass V2."
        )
        print(DASHES)

        try:
            self._setup()
            self._step1_list_core_devices()
            self._pause()
            self._step2_create_component_v1()
            self._pause()
            self._step3_create_component_v2()
            self._pause()
            self._step4_list_component_versions()
            self._pause()
            self._step5_get_component_recipe()
            self._pause()
            self._step6_describe_component()
            self._pause()
            self._step7_create_deployment()
            self._pause()
            self._step8_get_deployment()
            self._pause()
            self._step9_list_deployments()
            self._pause()
            self._step10_cancel_deployment()
        finally:
            if q.ask(
                "\nDo you want to delete the resources created by this scenario (y/n)? ",
                q.is_yesno,
            ):
                self._cleanup()
            else:
                print(
                    "Skipping cleanup. Remember to delete the components and thing "
                    "group manually to avoid leaving unused resources."
                )

        print(DASHES)
        print("AWS IoT Greengrass V2 Basics scenario complete!")
        print(DASHES)

    @staticmethod
    def _pause() -> None:
        """Pauses between steps so the user can review the output."""
        q.ask("\nPress Enter to continue...")

    def _setup(self) -> None:
        """Creates an IoT thing group used as the deployment target."""
        print("\nSetting up resources...")
        unique_suffix = str(uuid.uuid4())[:8]
        self.thing_group_name = f"GreengrassBasicsGroup-{unique_suffix}"

        try:
            response = self.iot_client.create_thing_group(
                thingGroupName=self.thing_group_name
            )
            self.thing_group_arn = response["thingGroupArn"]
            print(f"Created IoT thing group: {self.thing_group_name}")
            print(f"Thing group ARN: {self.thing_group_arn}")
        except ClientError as err:
            logger.error(
                "Failed to create thing group: %s",
                err.response["Error"]["Message"],
            )
            raise

        print("Setup complete.")
        print(DASHES)

    def _step1_list_core_devices(self) -> None:
        """Step 1: List Greengrass core devices."""
        print(DASHES)
        print("Step 1: Listing Greengrass core devices in your account...\n")

        devices = self.wrapper.list_core_devices()
        if devices:
            print(f"Found {len(devices)} core device(s):")
            for device in devices:
                thing_name = device.get("coreDeviceThingName", "Unknown")
                status = device.get("status", "Unknown")
                last_updated = device.get("lastStatusUpdateTimestamp", "N/A")
                print(
                    f"  - {thing_name} (Status: {status}, Last updated: {last_updated})"
                )
        else:
            print("Found 0 core device(s). No devices are currently registered.")
            print(
                "Note: Core devices appear here after you install the Greengrass Core "
                "software\non an IoT device. This scenario focuses on cloud-side management "
                "and does not\nrequire a physical device."
            )

        print(DASHES)

    def _build_recipe(self, version: str, message: str, log_level: str = None) -> dict:
        """
        Builds a component recipe dictionary.

        :param version: The component version string.
        :param message: The default message configuration value.
        :param log_level: Optional log level configuration value.
        :return: A recipe dictionary.
        """
        default_config = dict()
        default_config["Message"] = message
        if log_level is not None:
            default_config["LogLevel"] = log_level

        recipe = {
            "RecipeFormatVersion": "2020-01-25",
            "ComponentName": COMPONENT_NAME,
            "ComponentVersion": version,
            "ComponentDescription": (
                "Sample component for Greengrass Basics scenario"
                if version == "1.0.0"
                else "Enhanced sample component for Greengrass Basics scenario"
            ),
            "ComponentPublisher": "AWS Code Examples",
            "ComponentConfiguration": {"DefaultConfiguration": default_config},
            "Manifests": [
                {
                    "Platform": {"os": "linux"},
                    "Lifecycle": {"run": 'echo "{configuration:/Message}"'},
                }
            ],
        }
        return recipe

    def _step2_create_component_v1(self) -> None:
        """Step 2: Create component version 1.0.0."""
        print(DASHES)
        print(f"Step 2: Creating component {COMPONENT_NAME} version 1.0.0...\n")

        recipe = self._build_recipe(
            version="1.0.0",
            message="Hello from Greengrass Basics v1.0.0",
        )
        response = self.wrapper.create_component_version(recipe)

        self.v1_arn = response.get("arn")
        # Derive the version-less component ARN (used to list all versions).
        # See GreengrassV2Wrapper.component_arn_from_version_arn for the format.
        self.component_arn = self.wrapper.component_arn_from_version_arn(self.v1_arn)

        print("Component created successfully!")
        print(f"  ARN: {self.v1_arn}")
        print(f"  Name: {response.get('componentName')}")
        print(f"  Version: {response.get('componentVersion')}")
        status = response.get("status", dict())
        print(f"  Status: {status.get('componentState', 'N/A')}")
        print(f"  Created: {response.get('creationTimestamp')}")
        print(DASHES)

    def _step3_create_component_v2(self) -> None:
        """Step 3: Create component version 2.0.0."""
        print(DASHES)
        print(f"Step 3: Creating component {COMPONENT_NAME} version 2.0.0...\n")

        recipe = self._build_recipe(
            version="2.0.0",
            message="Hello from Greengrass Basics v2.0.0 - Enhanced Edition",
            log_level="INFO",
        )
        response = self.wrapper.create_component_version(recipe)

        self.v2_arn = response.get("arn")

        print("Component version 2.0.0 created successfully!")
        print(f"  ARN: {self.v2_arn}")
        print(f"  Name: {response.get('componentName')}")
        print(f"  Version: {response.get('componentVersion')}")
        status = response.get("status", dict())
        print(f"  Status: {status.get('componentState', 'N/A')}")
        print(f"  Created: {response.get('creationTimestamp')}")
        print("\nYou now have two versions of the component available for deployment.")
        print(DASHES)

    def _step4_list_component_versions(self) -> None:
        """Step 4: List component versions."""
        print(DASHES)
        print(f"Step 4: Listing all versions of {COMPONENT_NAME}...\n")

        versions = self.wrapper.list_component_versions(self.component_arn)
        print(f"Found {len(versions)} version(s):")
        for idx, version in enumerate(versions, 1):
            print(
                f"  {idx}. {version.get('componentName')} "
                f"v{version.get('componentVersion')}"
            )
            print(f"     ARN: {version.get('arn')}")

        print("\nNote: Versions are listed with the greatest (newest) version first.")
        print(DASHES)

    def _step5_get_component_recipe(self) -> None:
        """Step 5: Get component recipe for v2.0.0."""
        print(DASHES)
        print(f"Step 5: Retrieving recipe for {COMPONENT_NAME} v2.0.0...\n")

        response = self.wrapper.get_component(self.v2_arn, recipe_output_format="JSON")

        recipe_format = response.get("recipeOutputFormat", "JSON")
        recipe_blob = response.get("recipe")

        # The recipe is returned as bytes; decode it.
        if isinstance(recipe_blob, bytes):
            recipe_text = recipe_blob.decode("utf-8")
        else:
            recipe_text = str(recipe_blob)

        print(f"Recipe format: {recipe_format}")
        print("Recipe content:")
        try:
            recipe_dict = json.loads(recipe_text)
            print(json.dumps(recipe_dict, indent=2, default=str))
        except (json.JSONDecodeError, TypeError):
            print(recipe_text)

        print(DASHES)

    def _step6_describe_component(self) -> None:
        """Step 6: Describe component v2.0.0."""
        print(DASHES)
        print(f"Step 6: Describing component {COMPONENT_NAME} v2.0.0...\n")

        response = self.wrapper.describe_component(self.v2_arn)

        print("Component details:")
        print(f"  ARN: {response.get('arn')}")
        print(f"  Name: {response.get('componentName')}")
        print(f"  Version: {response.get('componentVersion')}")
        print(f"  Publisher: {response.get('publisher', 'N/A')}")
        print(f"  Description: {response.get('description', 'N/A')}")
        status = response.get("status", dict())
        print(f"  Status: {status.get('componentState', 'N/A')}")
        print(f"  Created: {response.get('creationTimestamp')}")

        platforms = response.get("platforms", list())
        if platforms:
            print("  Platforms:")
            for platform in platforms:
                attrs = platform.get("attributes", dict())
                name = platform.get("name", "unnamed")
                os_val = attrs.get("os", "any")
                print(f"    - {name} ({os_val})")

        print(DASHES)

    def _step7_create_deployment(self) -> None:
        """Step 7: Create a deployment targeting the thing group."""
        print(DASHES)
        print("Step 7: Creating deployment to thing group...\n")

        components = {
            COMPONENT_NAME: {
                "componentVersion": "2.0.0",
                "configurationUpdate": {
                    "merge": json.dumps({"Message": "Custom message from deployment"})
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

        response = self.wrapper.create_deployment(
            target_arn=self.thing_group_arn,
            deployment_name="GreengrassBasicsDeployment",
            components=components,
            deployment_policies=deployment_policies,
        )

        self.deployment_id = response.get("deploymentId")

        print("Deployment created successfully!")
        print(f"  Deployment ID: {self.deployment_id}")
        print(f"  IoT Job ID: {response.get('iotJobId', 'N/A')}")
        print(f"  Target: {self.thing_group_arn}")
        print(
            "\nThe deployment targets the thing group. Any Greengrass core device "
            "in this group\nwill receive the component with the custom configuration."
        )
        print(f"  - Component: {COMPONENT_NAME} v2.0.0")
        print("  - Configuration override: Custom message from deployment")
        print("  - Failure policy: ROLLBACK")
        print(DASHES)

    def _step8_get_deployment(self) -> None:
        """Step 8: Get deployment details."""
        print(DASHES)
        print("Step 8: Getting deployment details...\n")

        response = self.wrapper.get_deployment(self.deployment_id)

        print("Deployment details:")
        print(f"  ID: {response.get('deploymentId')}")
        print(f"  Name: {response.get('deploymentName', 'N/A')}")
        print(f"  Status: {response.get('deploymentStatus')}")
        print(f"  Target ARN: {response.get('targetArn')}")
        print(f"  Revision: {response.get('revisionId', 'N/A')}")
        print(f"  IoT Job ID: {response.get('iotJobId', 'N/A')}")
        print(f"  Created: {response.get('creationTimestamp')}")

        components = response.get("components", dict())
        if components:
            print("\n  Components:")
            for comp_name, comp_config in components.items():
                print(f"    {comp_name}:")
                print(f"      Version: {comp_config.get('componentVersion', 'N/A')}")
                config_update = comp_config.get("configurationUpdate", dict())
                merge_val = config_update.get("merge", None)
                if merge_val is not None:
                    print(f"      Configuration merge: {merge_val}")

        policies = response.get("deploymentPolicies", dict())
        if policies:
            print("\n  Deployment policies:")
            print(
                f"    Failure handling: {policies.get('failureHandlingPolicy', 'N/A')}"
            )
            update_policy = policies.get("componentUpdatePolicy", dict())
            if update_policy:
                print(
                    f"    Component update: {update_policy.get('action', 'N/A')} "
                    f"(timeout: {update_policy.get('timeoutInSeconds', 'N/A')}s)"
                )

        print("\nDeployment status meanings:")
        print("  ACTIVE - Deployment is in progress")
        print("  COMPLETED - All targeted devices received the deployment")
        print("  CANCELED - Deployment was canceled")
        print("  FAILED - Deployment failed")
        print("  INACTIVE - Deployment was replaced by a newer revision")
        print(DASHES)

    def _step9_list_deployments(self) -> None:
        """Step 9: List deployments for the thing group."""
        print(DASHES)
        print("Step 9: Listing deployments for the thing group...\n")

        deployments = self.wrapper.list_deployments(
            target_arn=self.thing_group_arn,
            history_filter="ALL",
        )

        group_name = self.thing_group_name or "unknown"
        print(f"Found {len(deployments)} deployment(s) targeting {group_name}:")
        for idx, dep in enumerate(deployments, 1):
            print(f"  {idx}. {dep.get('deploymentName', 'N/A')}")
            print(f"     ID: {dep.get('deploymentId')}")
            print(f"     Status: {dep.get('deploymentStatus')}")
            print(f"     Created: {dep.get('creationTimestamp')}")
            print(f"     Latest for target: {dep.get('isLatestForTarget', 'N/A')}")

        print(DASHES)

    def _step10_cancel_deployment(self) -> None:
        """Step 10: Cancel the deployment."""
        print(DASHES)
        print("Step 10: Canceling the deployment...\n")

        response = self.wrapper.cancel_deployment(self.deployment_id)

        message = response.get("message", "Deployment has been canceled.")
        print("Deployment canceled successfully!")
        print(f"  Message: {message}")
        print(
            "\nNote: Cancellation only affects devices that haven't yet received "
            "the deployment.\nDevices that already applied it will not be rolled back "
            "by this action."
        )
        print(DASHES)

    def _cleanup(self) -> None:
        """Cleans up all resources created during the scenario."""
        print(DASHES)
        print("Cleaning up resources...\n")

        # Delete component v2.0.0
        if self.v2_arn is not None:
            try:
                print(
                    f"Deleting component {COMPONENT_NAME} v2.0.0...",
                    end=" ",
                )
                self.wrapper.delete_component(self.v2_arn)
                print("Done.")
            except ClientError as err:
                error_code = err.response["Error"]["Code"]
                if error_code == "ResourceNotFoundException":
                    print("Already deleted.")
                else:
                    print(f"Error: {err.response['Error']['Message']}")

        # Delete component v1.0.0
        if self.v1_arn is not None:
            try:
                print(
                    f"Deleting component {COMPONENT_NAME} v1.0.0...",
                    end=" ",
                )
                self.wrapper.delete_component(self.v1_arn)
                print("Done.")
            except ClientError as err:
                error_code = err.response["Error"]["Code"]
                if error_code == "ResourceNotFoundException":
                    print("Already deleted.")
                else:
                    print(f"Error: {err.response['Error']['Message']}")

        # Delete the IoT thing group
        if self.thing_group_name is not None:
            try:
                print(
                    f"Deleting IoT thing group {self.thing_group_name}...",
                    end=" ",
                )
                self.iot_client.delete_thing_group(thingGroupName=self.thing_group_name)
                print("Done.")
            except ClientError as err:
                error_code = err.response["Error"]["Code"]
                if error_code == "ResourceNotFoundException":
                    print("Already deleted.")
                else:
                    print(f"Error: {err.response['Error']['Message']}")

        print("\nAll resources cleaned up successfully.")
        print(DASHES)


def main() -> None:
    """Entry point for the Greengrass V2 basics scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    greengrassv2_client = boto3.client("greengrassv2")
    iot_client = boto3.client("iot")
    wrapper = GreengrassV2Wrapper(greengrassv2_client)
    scenario = GreengrassV2Scenario(wrapper, iot_client)
    try:
        scenario.run_scenario()
    except Exception:
        logging.exception("Something went wrong running the scenario.")


if __name__ == "__main__":
    main()
# snippet-end:[python.example_code.greengrassv2.GreengrassV2Scenario]
