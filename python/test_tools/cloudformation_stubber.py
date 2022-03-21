# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by AWS CloudFormation unit tests.
"""

from datetime import datetime
from test_tools.example_stubber import ExampleStubber


class CloudFormationStubber(ExampleStubber):
    """
    A class that implements stub functions used by CloudFormation unit tests.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing.

        :param client: A Boto3 CloudFormation client.
        :param use_stubs: When True, use stubs to intercept requests.
        """
        super().__init__(client, use_stubs)

    def stub_create_stack(
            self, stack_name, setup_template, capabilities, stack_id, error_code=None):
        expected_params = {
            'StackName': stack_name,
            'TemplateBody': setup_template,
            'Capabilities': capabilities}
        response = {'StackId': stack_id}
        self._stub_bifurcator(
            'create_stack', expected_params, response, error_code=error_code)

    def stub_describe_stacks(self, stack_name, status, outputs=None, error_code=None):
        expected_params = {'StackName': stack_name}
        response = {'Stacks': [{
            'StackName': stack_name,
            'StackStatus': status,
            'CreationTime': datetime.now()}]}
        if outputs is not None:
            response['Stacks'][0]['Outputs'] = outputs
        self._stub_bifurcator(
            'describe_stacks', expected_params, response, error_code=error_code)

    def stub_list_stack_resources(self, stack_name, resources, error_code=None):
        expected_params = {'StackName': stack_name}
        response = {'StackResourceSummaries': resources}
        self._stub_bifurcator(
            'list_stack_resources', expected_params, response, error_code=error_code)

    def stub_delete_stack(self, stack_name, error_code=None):
        expected_params = {'StackName': stack_name}
        response = {}
        self._stub_bifurcator(
            'delete_stack', expected_params, response, error_code=error_code)
