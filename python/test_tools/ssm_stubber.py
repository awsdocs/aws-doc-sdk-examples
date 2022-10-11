# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Systems Manager unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


class SsmStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    AWS Systems Manager unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Systems Manager client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_send_command(self, instance_ids, commands, command_id, error_code=None):
        expected_parameters = {
            'InstanceIds': instance_ids,
            'DocumentName': 'AWS-RunShellScript',
            'Parameters': {'commands': commands},
            'TimeoutSeconds': 3600}
        response = {'Command': {'CommandId': command_id}}
        self._stub_bifurcator(
            'send_command', expected_parameters, response, error_code=error_code)

    def stub_list_commands(self, command_id, status_details, error_code=None):
        expected_parameters = {'CommandId': command_id}
        response = {'Commands': [{
            'CommandId': command_id, 'StatusDetails': status_details}]}
        self._stub_bifurcator(
            'list_commands', expected_parameters, response, error_code=error_code)

    def stub_get_parameters_by_path(self, names, values, path=ANY, error_code=None):
        expected_params = {'Path': path}
        response = {'Parameters': [{'Name': name, 'Value': value} for name, value in zip(names, values)]}
        self._stub_bifurcator(
            'get_parameters_by_path', expected_params, response, error_code=error_code)
