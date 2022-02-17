# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for websocket_chat.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from websocket_chat import ApiGatewayWebsocket


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_api(make_stubber, error_code):
    apigatewayv2_client = boto3.client('apigatewayv2')
    apigatewayv2_stubber = make_stubber(apigatewayv2_client)
    api_name = 'test-api_name'
    sock_gate = ApiGatewayWebsocket(api_name, apigatewayv2_client)
    route_selection = 'test-route_selection'
    api_id = 'test-api_id'
    api_endpoint = 'test-api_endpoint'

    apigatewayv2_stubber.stub_create_api(
        api_name, 'WEBSOCKET', route_selection, api_id, api_endpoint,
        error_code=error_code)

    if error_code is None:
        got_api_id = sock_gate.create_api(route_selection)
        assert got_api_id == api_id
        assert sock_gate.api_id == api_id
        assert sock_gate.api_endpoint == api_endpoint
    else:
        with pytest.raises(ClientError) as exc_info:
            sock_gate.create_api(route_selection)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_policy'),
    ('TestException', 'stub_attach_role_policy')])
def test_add_connection_permissions(
        make_stubber, stub_runner, error_code, stop_on_method):
    apigatewayv2_client = boto3.client('apigatewayv2')
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    sock_gate = ApiGatewayWebsocket('test-api', apigatewayv2_client)
    sock_gate.api_id = 'test-api_id'
    account = 'test-account'
    role_name = 'test-lambda-role'
    policy_name = f'{role_name}-{sock_gate.permission_policy_suffix}'
    policy_arn = f':arn:aws:iam:REGION:123456789012:policy/{policy_name}'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(iam_stubber.stub_create_policy, policy_name, policy_arn)
        runner.add(iam_stubber.stub_attach_role_policy, role_name, policy_arn)
        runner.add(iam_stubber.stub_get_policy, policy_arn)

    if error_code is not None and stop_on_method != 'stub_create_policy':
        iam_stubber.stub_delete_policy(policy_arn)

    if error_code is None:
        sock_gate.add_connection_permissions(account, role_name, iam_resource)
    else:
        with pytest.raises(ClientError) as exc_info:
            sock_gate.add_connection_permissions(account, role_name, iam_resource)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_list_attached_role_policies'),
    ('TestException', 'stub_get_policy'),
    ('TestException', 'stub_detach_role_policy'),
    ('TestException', 'stub_delete_policy')])
def test_remove_connection_permissions(
        make_stubber, stub_runner, error_code, stop_on_method):
    apigatewayv2_client = boto3.client('apigatewayv2')
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    sock_gate = ApiGatewayWebsocket('test-api', apigatewayv2_client)
    role_name = 'test-role_name'
    policy_name = f'{role_name}-{sock_gate.permission_policy_suffix}'
    policy_arn = f'arn:aws:iam:REGION:123456789012:{policy_name}'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            iam_stubber.stub_list_attached_role_policies, role_name,
            {policy_name: policy_arn})
        runner.add(iam_stubber.stub_get_policy, policy_arn)
        runner.add(iam_stubber.stub_detach_role_policy, role_name, policy_arn)
        runner.add(iam_stubber.stub_delete_policy, policy_arn)

    if error_code is None:
        sock_gate.remove_connection_permissions(iam_resource.Role(role_name))
    else:
        with pytest.raises(ClientError) as exc_info:
            sock_gate.remove_connection_permissions(iam_resource.Role(role_name))
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_integration'),
    ('TestException', 'stub_create_route'),
    ('TestException', 'stub_add_permission')])
def test_add_route_and_integration(
        make_stubber, stub_runner, error_code, stop_on_method):
    apigatewayv2_client = boto3.client('apigatewayv2')
    apigatewayv2_stubber = make_stubber(apigatewayv2_client)
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    sock_gate = ApiGatewayWebsocket('test-api', apigatewayv2_client)
    sock_gate.api_id = 'test-api_id'
    integration_id = 'test-integration_id'
    route_name = 'test-route_name'
    lambda_func = {
        'FunctionName': 'test-function-name',
        'FunctionArn': 'arn:aws:lambda:REGION:12345679012:function/test'}
    route_id = 'test-route_id'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            apigatewayv2_stubber.stub_create_integration, sock_gate.api_id,
            integration_id)
        runner.add(
            apigatewayv2_stubber.stub_create_route, sock_gate.api_id, route_name,
            f'integrations/{integration_id}', route_id)
        runner.add(
            lambda_stubber.stub_add_permission, lambda_func['FunctionName'],
            'lambda:InvokeFunction', 'apigateway.amazonaws.com')

    if error_code is None:
        got_route_id = sock_gate.add_route_and_integration(
            route_name, lambda_func, lambda_client)
        assert got_route_id == route_id
    else:
        with pytest.raises(ClientError) as exc_info:
            sock_gate.add_route_and_integration(
                route_name, lambda_func, lambda_client)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_deploy_api(make_stubber, error_code):
    apigatewayv2_client = boto3.client('apigatewayv2')
    apigatewayv2_stubber = make_stubber(apigatewayv2_client)
    sock_gate = ApiGatewayWebsocket('test-api', apigatewayv2_client)
    sock_gate.api_id = 'test-api_id'
    sock_gate.api_endpoint = 'test-api_endpoint'
    stage = 'test-stage'
    uri = f'{sock_gate.api_endpoint}/{stage}'

    apigatewayv2_stubber.stub_create_stage(
        sock_gate.api_id, stage, error_code=error_code)

    if error_code is None:
        got_uri = sock_gate.deploy_api(stage)
        assert got_uri == uri
    else:
        with pytest.raises(ClientError) as exc_info:
            sock_gate.deploy_api(stage)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_websocket_api_info(make_stubber, error_code):
    apigatewayv2_client = boto3.client('apigatewayv2')
    apigatewayv2_stubber = make_stubber(apigatewayv2_client)
    sock_gate = ApiGatewayWebsocket('test-api', apigatewayv2_client)
    api_id = 'test-api_id'
    api_endpoint = 'test-api_endpoint'

    apigatewayv2_stubber.stub_get_apis([
        {'Name': sock_gate.api_name, 'ApiId': api_id, 'ApiEndpoint': api_endpoint}],
        error_code=error_code)

    if error_code is None:
        got_api_id, got_api_endpoint = sock_gate.get_websocket_api_info()
        assert got_api_id == api_id
        assert got_api_endpoint == api_endpoint
        assert sock_gate.api_id == api_id
        assert sock_gate.api_endpoint == api_endpoint
    else:
        with pytest.raises(ClientError) as exc_info:
            sock_gate.get_websocket_api_info()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_api(make_stubber, error_code):
    apigatewayv2_client = boto3.client('apigatewayv2')
    apigatewayv2_stubber = make_stubber(apigatewayv2_client)
    sock_gate = ApiGatewayWebsocket('test-api', apigatewayv2_client)
    api_id = 'test-api_id'

    apigatewayv2_stubber.stub_get_apis([
        {'Name': sock_gate.api_name, 'ApiId': api_id, 'ApiEndpoint': ''}])
    apigatewayv2_stubber.stub_delete_api(api_id, error_code=error_code)

    if error_code is None:
        sock_gate.delete_api()
    else:
        with pytest.raises(ClientError) as exc_info:
            sock_gate.delete_api()
        assert exc_info.value.response['Error']['Code'] == error_code
