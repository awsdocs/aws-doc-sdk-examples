# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Control Tower unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from botocore.stub import ANY
from boto3 import client

from test_tools.example_stubber import ExampleStubber


class ControlTowerStubber(ExampleStubber):
    """
    A class that implements stub functions used by AWS Control Tower unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, controltower_client: client, use_stubs=True) -> None:
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param controltower_client: A Boto 3 AWS Control Tower client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(controltower_client, use_stubs)

    def stub_list_landing_zones(self, landing_zones: list, error_code: str = None) -> None:
        """
        Stub the list_landing_zones function.

        :param landing_zones: List of landing zones to return.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {}
        response = {
            "landingZones": landing_zones
        }
        self._stub_bifurcator(
            "list_landing_zones", expected_params, response, error_code=error_code
        )

    def stub_list_baselines(self, baselines: list, error_code: str = None) -> None:
        """
        Stub the list_baselines function.

        :param baselines: List of baselines to return.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {}
        response = {
            "baselines": baselines
        }
        self._stub_bifurcator(
            "list_baselines", expected_params, response, error_code=error_code
        )
        
    def stub_list_enabled_baselines(self, target_identifier: str, enabled_baselines: list, error_code: str = None) -> None:
        """
        Stub the list_enabled_baselines function.

        :param target_identifier: The identifier of the target.
        :param enabled_baselines: List of enabled baselines to return.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "targetIdentifier": target_identifier
        }
        response = {
            "enabledBaselines": enabled_baselines
        }
        self._stub_bifurcator(
            "list_enabled_baselines", expected_params, response, error_code=error_code
        )
        
    def stub_reset_enabled_baseline(self, target_identifier: str, baseline_identifier: str, operation_identifier: str, error_code: str = None) -> None:
        """
        Stub the reset_enabled_baseline function.

        :param target_identifier: The identifier of the target.
        :param baseline_identifier: The identifier of the baseline to reset.
        :param operation_identifier: The identifier of the operation.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "targetIdentifier": target_identifier,
            "baselineIdentifier": baseline_identifier
        }
        response = {
            "operationIdentifier": operation_identifier
        }
        self._stub_bifurcator(
            "reset_enabled_baseline", expected_params, response, error_code=error_code
        )
        
    def stub_disable_baseline(self, target_identifier: str, baseline_identifier: str, operation_identifier: str, error_code: str = None) -> None:
        """
        Stub the disable_baseline function.

        :param target_identifier: The identifier of the target.
        :param baseline_identifier: The identifier of the baseline to disable.
        :param operation_identifier: The identifier of the operation.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "targetIdentifier": target_identifier,
            "baselineIdentifier": baseline_identifier
        }
        response = {
            "operationIdentifier": operation_identifier
        }
        self._stub_bifurcator(
            "disable_baseline", expected_params, response, error_code=error_code
        )
        
    def stub_list_enabled_controls(self, target_identifier: str, enabled_controls: list, error_code: str = None) -> None:
        """
        Stub the list_enabled_controls function.

        :param target_identifier: The identifier of the target.
        :param enabled_controls: List of enabled controls to return.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "targetIdentifier": target_identifier
        }
        response = {
            "enabledControls": enabled_controls
        }
        self._stub_bifurcator(
            "list_enabled_controls", expected_params, response, error_code=error_code
        )

    def stub_enable_baseline(self, baseline_identifier: str, baseline_version: str, target_identifier: str, arn: str, operation_identifier: str, error_code: str = None) -> None:
        """
        Stub the enable_baseline function.

        :param baseline_identifier: The identifier of the baseline.
        :param baseline_version: The version of the baseline.
        :param target_identifier: The identifier of the target.
        :param arn: The ARN of the enabled baseline.
        :param operation_identifier: The operation identifier of the enable operation.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "baselineIdentifier": baseline_identifier,
            "baselineVersion": baseline_version,
            "targetIdentifier": target_identifier
        }
        response = {
            "arn": arn,
            "operationIdentifier": operation_identifier,
        }
        self._stub_bifurcator(
            "enable_baseline", expected_params, response, error_code=error_code
        )

    def stub_enable_control(self, control_identifier: str, target_identifier: str, operation_identifier: str, error_code: str = None) -> None:
        """
        Stub the enable_control function.

        :param control_identifier: The identifier of the control.
        :param target_identifier: The identifier of the target.
        :param operation_identifier: The identifier of the operation.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "controlIdentifier": control_identifier,
            "targetIdentifier": target_identifier
        }
        response = {
            "operationIdentifier": operation_identifier
        }
        self._stub_bifurcator(
            "enable_control", expected_params, response, error_code=error_code
        )

    def stub_disable_control(self, control_identifier: str, target_identifier: str, operation_id: str, error_code: str = None) -> None:
        """
        Stub the disable_control function.

        :param control_identifier: The identifier of the control.
        :param target_identifier: The identifier of the target.
        :param operation_id: The ID of the operation.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "controlIdentifier": control_identifier,
            "targetIdentifier": target_identifier
        }
        response = {
            "operationIdentifier": operation_id
        }
        self._stub_bifurcator(
            "disable_control", expected_params, response, error_code=error_code
        )

    def stub_get_control_operation(self, operation_identifier: str, status: str, error_code: str = None) -> None:
        """
        Stub the get_control_operation function.

        :param operation_identifier: The identifier of the operation.
        :param status: The status of the operation.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "operationIdentifier": operation_identifier
        }
        response = {
            "controlOperation": {
                "status": status,
            }
        }
        self._stub_bifurcator(
            "get_control_operation", expected_params, response, error_code=error_code
        )