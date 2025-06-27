# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Control Catalog unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from botocore.stub import ANY
from boto3 import client

from test_tools.example_stubber import ExampleStubber


class ControlCatalogStubber(ExampleStubber):
    """
    A class that implements stub functions used by AWS Control Catalog unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, controlcatalog_client: client, use_stubs=True) -> None:
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param controlcatalog_client: A Boto 3 AWS Control Catalog client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(controlcatalog_client, use_stubs)

    def stub_list_controls(self, controls: list, error_code: str = None) -> None:
        """
        Stub the list_controls function.

        :param controls: List of controls to return.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {}
        response = {
            "Controls": controls
        }
        self._stub_bifurcator(
            "list_controls", expected_params, response, error_code=error_code
        )

    def stub_get_control(self, control_arn: str, control_details: dict, error_code: str = None) -> None:
        """
        Stub the get_control function.

        :param control_arn: The ARN of the control.
        :param control_details: The details of the control.
        :param error_code: Simulated error code to raise.
        """
        expected_params = {
            "ControlArn": control_arn
        }
        response = control_details
        self._stub_bifurcator(
            "get_control", expected_params, response, error_code=error_code
        )