# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon API Gateway v2 unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from botocore.stub import ANY

from test_tools.example_stubber import ExampleStubber


class ApiGatewayV2Stubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon API Gateway v2 unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 API Gateway v2 client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_api(
            self, api_name, protocol, route_selection, api_id, api_endpoint,
            error_code=None):
        expected_params = {
            'Name': api_name,
            'ProtocolType': protocol,
            'RouteSelectionExpression': route_selection}
        response = {'ApiId': api_id, 'ApiEndpoint': api_endpoint}
        self._stub_bifurcator(
            'create_api', expected_params, response, error_code=error_code)

    def stub_create_integration(
            self, api_id, integration_id, integration_uri=ANY, error_code=None):
        expected_params = {
            'ApiId': api_id,
            'IntegrationType': 'AWS_PROXY',
            'IntegrationMethod': 'POST',
            'IntegrationUri': integration_uri
        }
        response = {'IntegrationId': integration_id}
        self._stub_bifurcator(
            'create_integration', expected_params, response, error_code=error_code)

    def stub_create_route(self, api_id, route_name, target, route_id, error_code=None):
        expected_params = {
            'ApiId': api_id, 'RouteKey': route_name, 'Target': target}
        response = {'RouteId': route_id}
        self._stub_bifurcator(
            'create_route', expected_params, response, error_code=error_code)

    def stub_create_stage(self, api_id, stage, error_code=None):
        expected_params = {'ApiId': api_id, 'AutoDeploy': True, 'StageName': stage}
        response = {}
        self._stub_bifurcator(
            'create_stage', expected_params, response, error_code=error_code)

    def stub_get_apis(self, apis, error_code=None):
        expected_params = {}
        response = {'Items': apis}
        for item in response['Items']:
            item.update({
                'RouteSelectionExpression': 'test_expr',
                'ProtocolType': 'test-protocol'})
        self._stub_bifurcator(
            'get_apis', expected_params, response, error_code=error_code)

    def stub_delete_api(self, api_id, error_code=None):
        expected_params = {'ApiId': api_id}
        response = {}
        self._stub_bifurcator(
            'delete_api', expected_params, response, error_code=error_code)
