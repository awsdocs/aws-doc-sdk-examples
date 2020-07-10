# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon API Gateway unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from test_tools.example_stubber import ExampleStubber


class ApiGatewayStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon API Gateway unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 API Gateway client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_rest_api(self, api_name, api_id, error_code=None):
        expected_params = {'name': api_name}
        response = {'id': api_id}
        self._stub_bifurcator(
            'create_rest_api', expected_params, response, error_code=error_code)

    def stub_get_resources(self, api_id, resources, error_code=None):
        expected_params = {'restApiId': api_id}
        response = {'items': resources}
        self._stub_bifurcator(
            'get_resources', expected_params, response, error_code=error_code)

    def stub_create_resource(
            self, api_id, parent_id, path, resource_id, error_code=None):
        expected_params = {'restApiId': api_id, 'parentId': parent_id, 'pathPart': path}
        response = {'id': resource_id}
        self._stub_bifurcator(
            'create_resource', expected_params, response, error_code=error_code)

    def stub_put_method(self, api_id, resource_id, error_code=None):
        expected_params = {
            'restApiId': api_id, 'resourceId': resource_id, 'httpMethod': 'ANY',
            'authorizationType': 'NONE'}
        self._stub_bifurcator(
            'put_method', expected_params, error_code=error_code)

    def stub_put_method_response(
            self, api_id, resource_id, response_models, error_code=None):
        expected_params = {
            'restApiId': api_id, 'resourceId': resource_id, 'httpMethod': 'ANY',
            'statusCode': '200', 'responseModels': response_models}
        self._stub_bifurcator(
            'put_method_response', expected_params, error_code=error_code)

    def stub_put_integration(self, api_id, resource_id, uri, error_code=None):
        expected_params = {
            'restApiId': api_id, 'resourceId': resource_id, 'httpMethod': 'ANY',
            'type': 'AWS_PROXY', 'integrationHttpMethod': 'POST', 'uri': uri}
        self._stub_bifurcator(
            'put_integration', expected_params, error_code=error_code)

    def stub_put_integration_response(
            self, api_id, resource_id, response_templates, error_code=None):
        expected_params = {
            'restApiId': api_id, 'resourceId': resource_id, 'httpMethod': 'ANY',
            'statusCode': '200', 'responseTemplates': response_templates}
        self._stub_bifurcator(
            'put_integration_response', expected_params, error_code=error_code)

    def stub_create_deployment(self, api_id, api_stage, error_code=None):
        expected_params = {'restApiId': api_id, 'stageName': api_stage}
        self._stub_bifurcator(
            'create_deployment', expected_params, error_code=error_code)

    def stub_delete_rest_api(self, api_id, error_code=None):
        expected_params = {'restApiId': api_id}
        self._stub_bifurcator(
            'delete_rest_api', expected_params, error_code=error_code)
