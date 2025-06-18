# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import boto3
import time

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.controltower.ControlTowerWrapper.class]
# snippet-start:[python.example_code.controltower.ControlTowerWrapper.decl]


class ControlTowerWrapper:
    """Encapsulates AWS Control Tower and Control Catalog functionality."""

    def __init__(self, controltower_client, controlcatalog_client):
        """
        :param controltower_client: A Boto3 Amazon ControlTower client.
        :param controlcatalog_client: A Boto3 Amazon ControlCatalog client.
        """
        self.controltower_client = controltower_client
        self.controlcatalog_client = controlcatalog_client

    @classmethod
    def from_client(cls):
        controltower_client = boto3.client("controltower")
        controlcatalog_client = boto3.client("controlcatalog")
        return cls(controltower_client, controlcatalog_client)

    # snippet-end:[python.example_code.controltower.ControlTowerWrapper.decl]

    # snippet-start:[python.example_code.controltower.ListBaselines]
    def list_baselines(self):
        """
        Lists all baselines.

        :return: List of baselines.
        :raises ClientError: If the listing operation fails.
        """
        try:
            paginator = self.controltower_client.get_paginator('list_baselines')
            baselines = []
            for page in paginator.paginate():
                baselines.extend(page['baselines'])
            return baselines

        except ClientError as err:
            if err.response["Error"]["Code"] == "AccessDeniedException":
                logger.error("Access denied. Please ensure you have the necessary permissions.")
            else:
                logger.error(
                    "Couldn't list baselines. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"]
                )
            raise

    # snippet-end:[python.example_code.controltower.ListBaselines]

    # snippet-start:[python.example_code.controltower.EnableBaseline]
    def enable_baseline(self, target_identifier, identity_center_baseline, baseline_identifier, baseline_version):
        """
        Enables a baseline for the specified target if it's not already enabled.

        :param target_identifier: The ARN of the target.
        :param baseline_identifier: The identifier of baseline to enable.
        :param identity_center_baseline: The identifier of identity center baseline if it is enabled.
        :param baseline_version: The version of baseline to enable.
        :return: The enabled baseline ARN or None if already enabled.
        :raises ClientError: If enabling the baseline fails for reasons other than it being already enabled.
        """
        try:
            response = self.controltower_client.enable_baseline(
                baselineIdentifier=baseline_identifier,
                baselineVersion=baseline_version,
                targetIdentifier=target_identifier,
                parameters=[
                    {
                        "key": "IdentityCenterEnabledBaselineArn",
                        "value": identity_center_baseline
                    }
                ]
            )

            operation_id = response['operationIdentifier']
            while True:
                status = self.get_baseline_operation(operation_id)
                print(f"Baseline operation status: {status}")
                if status in ['SUCCEEDED', 'FAILED']:
                    break
                time.sleep(30)

            return response['arn']
        except ClientError as err:
            if err.response["Error"]["Code"] == "ValidationException":
                if "already enabled" in err.response["Error"]["Message"]:
                    print("Baseline is already enabled for this target")
                    return None
                else:
                    print("Unable to enable baseline due to validation exception: %s: %s",
                          err.response["Error"]["Code"],
                          err.response["Error"]["Message"])
            logger.error(
                "Couldn't enable baseline. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"]
            )
            raise
    # snippet-end:[python.example_code.controltower.EnableBaseline]

    # snippet-start:[python.example_code.controltower.ListControls]
    def list_controls(self):
        """
        Lists all controls in the Control Tower control catalog.

        :return: List of controls.
        :raises ClientError: If the listing operation fails.
        """
        try:
            paginator = self.controlcatalog_client.get_paginator('list_controls')
            controls = []
            for page in paginator.paginate():
                controls.extend(page['Controls'])
            return controls

        except ClientError as err:
            if err.response["Error"]["Code"] == "AccessDeniedException":
                logger.error("Access denied. Please ensure you have the necessary permissions.")
            else:
                logger.error(
                    "Couldn't list controls. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"]
                )
            raise

    # snippet-end:[python.example_code.controltower.ListControls]

    # snippet-start:[python.example_code.controltower.EnableControl]
    def enable_control(self, control_arn, target_identifier):
        """
        Enables a control for a specified target.

        :param control_arn: The ARN of the control to enable.
        :param target_identifier: The identifier of the target (e.g., OU ARN).
        :return: The operation ID.
        :raises ClientError: If enabling the control fails.
        """
        try:
            print(control_arn)
            print(target_identifier)
            response = self.controltower_client.enable_control(
                controlIdentifier=control_arn,
                targetIdentifier=target_identifier
            )

            operation_id = response['operationIdentifier']
            while True:
                status = self.get_control_operation(operation_id)
                print(f"Control operation status: {status}")
                if status in ['SUCCEEDED', 'FAILED']:
                    break
                time.sleep(30)

            return operation_id

        except ClientError as err:
            if (err.response["Error"]["Code"] == "ValidationException" and
                    "already enabled" in err.response["Error"][
                "Message"]):
                logger.info("Control is already enabled for this target")
                return None
            logger.error(
                "Couldn't enable control. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"]
            )
            raise

    # snippet-end:[python.example_code.controltower.EnableControl]

    # snippet-start:[python.example_code.controltower.GetControlOperation]
    def get_control_operation(self, operation_id):
        """
        Gets the status of a control operation.

        :param operation_id: The ID of the control operation.
        :return: The operation status.
        :raises ClientError: If getting the operation status fails.
        """
        try:
            response = self.controltower_client.get_control_operation(
                operationIdentifier=operation_id
            )
            return response['controlOperation']['status']
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Operation not found.")
            else:
                logger.error(
                    "Couldn't get control operation status. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"]
                )
            raise

    # snippet-end:[python.example_code.controltower.GetControlOperation]

    # snippet-start:[python.example_code.controltower.GetBaselineOperation]
    def get_baseline_operation(self, operation_id):
        """
        Gets the status of a baseline operation.

        :param operation_id: The ID of the baseline operation.
        :return: The operation status.
        :raises ClientError: If getting the operation status fails.
        """
        try:
            response = self.controltower_client.get_baseline_operation(
                operationIdentifier=operation_id
            )
            return response['baselineOperation']['status']
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Operation not found.")
            else:
                logger.error(
                    "Couldn't get baseline operation status. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"]
                )
            raise

    # snippet-end:[python.example_code.controltower.GetBaselineOperation]

    # snippet-start:[python.example_code.controltower.DisableControl]
    def disable_control(self, control_arn, target_identifier):
        """
        Disables a control for a specified target.

        :param control_arn: The ARN of the control to disable.
        :param target_identifier: The identifier of the target (e.g., OU ARN).
        :return: The operation ID.
        :raises ClientError: If disabling the control fails.
        """
        try:
            response = self.controltower_client.disable_control(
                controlIdentifier=control_arn,
                targetIdentifier=target_identifier
            )

            operation_id = response['operationIdentifier']
            while True:
                status = self.get_control_operation(operation_id)
                print(f"Control operation status: {status}")
                if status in ['SUCCEEDED', 'FAILED']:
                    break
                time.sleep(30)

            return operation_id
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Control not found.")
            else:
                logger.error(
                    "Couldn't disable control. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"]
                )
            raise

    # snippet-end:[python.example_code.controltower.DisableControl]

    # snippet-start:[python.example_code.controltower.ListLandingZones]
    def list_landing_zones(self):
        """
        Lists all landing zones.

        :return: List of landing zones.
        :raises ClientError: If the listing operation fails.
        """
        try:
            paginator = self.controltower_client.get_paginator('list_landing_zones')
            landing_zones = []
            for page in paginator.paginate():
                landing_zones.extend(page['landingZones'])
            return landing_zones

        except ClientError as err:
            if err.response["Error"]["Code"] == "AccessDeniedException":
                logger.error("Access denied. Please ensure you have the necessary permissions.")
            else:
                logger.error(
                    "Couldn't list landing zones. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"]
                )
            raise
    # snippet-end:[python.example_code.controltower.ListLandingZones]

    # snippet-start:[python.example_code.controltower.ListEnabledBaselines]
    def list_enabled_baselines(self):
        """
        Lists all enabled baselines.

        :return: List of enabled baselines.
        :raises ClientError: If the listing operation fails.
        """
        try:
            paginator = self.controltower_client.get_paginator('list_enabled_baselines')
            enabled_baselines = []
            for page in paginator.paginate():
                enabled_baselines.extend(page['enabledBaselines'])
            return enabled_baselines

        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Target not found.")
            else:
                logger.error(
                    "Couldn't list enabled baselines. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"]
                )
            raise
    # snippet-end:[python.example_code.controltower.ListEnabledBaselines]
    
    # snippet-start:[python.example_code.controltower.ResetEnabledBaseline]
    def reset_enabled_baseline(self, enabled_baseline_identifier):
        """
        Resets an enabled baseline for a specific target.

        :param enabled_baseline_identifier: The identifier of the enabled baseline to reset.
        :return: The operation ID.
        :raises ClientError: If resetting the baseline fails.
        """
        try:
            response = self.controltower_client.reset_enabled_baseline(
                enabledBaselineIdentifier=enabled_baseline_identifier
            )
            operation_id = response['operationIdentifier']
            while True:
                status = self.get_baseline_operation(operation_id)
                print(f"Baseline operation status: {status}")
                if status in ['SUCCEEDED', 'FAILED']:
                    break
                time.sleep(30)
            return operation_id
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Target not found.")
            else:
                logger.error(
                    "Couldn't reset enabled baseline. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"]
                )
            raise
    # snippet-end:[python.example_code.controltower.ResetEnabledBaseline]
    
    # snippet-start:[python.example_code.controltower.DisableBaseline]
    def disable_baseline(self, enabled_baseline_identifier):
        """
        Disables a baseline for a specific target and waits for the operation to complete.

        :param enabled_baseline_identifier: The identifier of the baseline to disable.
        :return: The operation ID.
        :raises ClientError: If disabling the baseline fails.
        """
        try:
            response = self.controltower_client.disable_baseline(
                enabledBaselineIdentifier=enabled_baseline_identifier
            )

            operation_id = response['operationIdentifier']
            while True:
                status = self.get_baseline_operation(operation_id)
                print(f"Baseline operation status: {status}")
                if status in ['SUCCEEDED', 'FAILED']:
                    break
                time.sleep(30)

            return response['operationIdentifier']
        except ClientError as err:
            if err.response["Error"]["Code"] == "ConflictException":
                print(f"Conflict disabling baseline: {err.response['Error']['Message']}. Skipping disable step." )
                return None
            else:
                logger.error(
                    "Couldn't disable baseline. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"]
                )
                raise
    # snippet-end:[python.example_code.controltower.DisableBaseline]
    
    # snippet-start:[python.example_code.controltower.ListEnabledControls]
    def list_enabled_controls(self, target_identifier):
        """
        Lists all enabled controls for a specific target.

        :param target_identifier: The identifier of the target (e.g., OU ARN).
        :return: List of enabled controls.
        :raises ClientError: If the listing operation fails.
        """
        try:
            paginator = self.controltower_client.get_paginator('list_enabled_controls')
            enabled_controls = []
            for page in paginator.paginate(targetIdentifier=target_identifier):
                enabled_controls.extend(page['enabledControls'])
            return enabled_controls

        except ClientError as err:
            if err.response["Error"]["Code"] == "AccessDeniedException":
                logger.error("Access denied. Please ensure you have the necessary permissions.")
            else:
                logger.error(
                    "Couldn't list enabled controls. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"]
                )
            raise
    # snippet-end:[python.example_code.controltower.ListEnabledControls]

# snippet-end:[python.example_code.controltower.ControlTowerWrapper.class]