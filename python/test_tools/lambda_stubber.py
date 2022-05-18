# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Lambda unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from botocore.stub import ANY

from test_tools.example_stubber import ExampleStubber


class LambdaStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    AWS Lambda unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto 3 Lambda client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_function(self, function_name, function_arn, role_arn,
                             handler, zip_contents=ANY, error_code=None):
        expected_params = {
            'FunctionName': function_name,
            'Runtime': ANY,
            'Role': role_arn,
            'Handler': handler,
            'Code': {'ZipFile': zip_contents},
            'Description': ANY,
            'Publish': True
        }
        response = {'FunctionArn': function_arn}
        self._stub_bifurcator(
            'create_function', expected_params, response, error_code=error_code)

    def stub_get_function(
            self, function_name, state=None, update_status=None, error_code=None):
        expected_params = {'FunctionName': function_name}
        response = {'Configuration': {'FunctionName': function_name}}
        if state is not None:
            response['Configuration']['State'] = state
        if update_status is not None:
            response['Configuration']['LastUpdateStatus'] = update_status
        self._stub_bifurcator(
            'get_function', expected_params, response, error_code=error_code)

    def stub_delete_function(self, function_name, error_code=None):
        self._stub_bifurcator(
            'delete_function',
            expected_params={'FunctionName': function_name},
            error_code=error_code)

    def stub_invoke(
            self, function_name, in_payload, out_payload, log_type=None, log_result=None,
            error_code=None):
        expected_params = {
            'FunctionName': function_name,
            'Payload': in_payload
        }
        if log_type is not None:
            expected_params['LogType'] = log_type
        response = {'Payload': out_payload}
        if log_result is not None:
            response['LogResult'] = log_result
        self._stub_bifurcator(
            'invoke', expected_params, response, error_code=error_code)

    def stub_add_permission(
            self, func_name, action, principal, source_arn=ANY,
            error_code=None):
        expected_params = {
            'FunctionName': func_name, 'StatementId': ANY, 'Action': action,
            'Principal': principal, 'SourceArn': source_arn}
        self._stub_bifurcator(
            'add_permission', expected_params, error_code=error_code)

    def stub_update_function_code(
            self, func_name, update_status, package=ANY, error_code=None):
        expected_params = {'FunctionName': func_name, 'ZipFile': package}
        response = {'FunctionName': func_name, 'LastUpdateStatus': update_status}
        self._stub_bifurcator(
            'update_function_code', expected_params, response, error_code=error_code)

    def stub_update_function_configuration(self, func_name, env_vars, error_code=None):
        expected_params = {
            'FunctionName': func_name, 'Environment': {'Variables': env_vars}}
        response = {'FunctionName': func_name}
        self._stub_bifurcator(
            'update_function_configuration', expected_params, response, error_code=error_code)

    def stub_list_functions(self, funcs, error_code=None):
        expected_params = {}
        response = {'Functions': funcs}
        self._stub_bifurcator(
            'list_functions', expected_params, response, error_code=error_code)
