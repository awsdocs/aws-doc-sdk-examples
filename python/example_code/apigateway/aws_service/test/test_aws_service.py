# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for aws_service.py.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from aws_service import ApiGatewayToService


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_rest_api'),
    ('TestException', 'stub_get_resources')])
def test_create_rest_api(make_stubber, stub_runner, error_code, stop_on_method):
    apigateway_client = boto3.client('apigateway')
    apigateway_stubber = make_stubber(apigateway_client)
    api_gtos = ApiGatewayToService(apigateway_client)
    api_name = 'test-api_name'
    api_id = 'test-api-id'
    resources = [{'path': '/', 'id': 'resource-id'}]

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(apigateway_stubber.stub_create_rest_api, api_name, api_id)
        runner.add(apigateway_stubber.stub_get_resources, api_id, resources)

    if error_code is None:
        got_api_id = api_gtos.create_rest_api(api_name)
        assert got_api_id == api_id
        assert api_gtos.root_id == resources[0]['id']
    else:
        with pytest.raises(ClientError) as exc_info:
            api_gtos.create_rest_api(api_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_add_rest_resource(make_stubber, error_code):
    apigateway_client = boto3.client('apigateway')
    apigateway_stubber = make_stubber(apigateway_client)
    api_gtos = ApiGatewayToService(apigateway_client)
    api_gtos.api_id = 'test-api-id'
    parent_id = 'test-parent_id'
    resource_path = '/test'
    resource_id = 'resource-id'

    apigateway_stubber.stub_create_resource(
        api_gtos.api_id, parent_id, resource_path, resource_id, error_code=error_code)

    if error_code is None:
        got_resource_id = api_gtos.add_rest_resource(parent_id, resource_path)
        assert got_resource_id == resource_id
    else:
        with pytest.raises(ClientError) as exc_info:
            api_gtos.add_rest_resource(parent_id, resource_path)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_put_method'),
    ('TestException', 'stub_put_method_response'),
    ('TestException', 'stub_put_integration'),
    ('TestException', 'stub_put_integration_response')])
def test_add_integration_method(make_stubber, stub_runner, error_code, stop_on_method):
    apigateway_client = boto3.client('apigateway')
    apigateway_stubber = make_stubber(apigateway_client)
    api_gtos = ApiGatewayToService(apigateway_client)
    api_gtos.api_id = 'test-api-id'
    resource_id = 'test-resource_id'
    rest_method = 'GET'
    service_endpoint_prefix = 'testservice'
    service_action = 'TestTheThing'
    service_method = 'POST'
    role_arn = 'arn:aws:iam:REGION:123456789012:role/test-role'
    mapping_template = {'test_name': 'test_value'}
    service_uri = (f'arn:aws:apigateway:{apigateway_client.meta.region_name}'
                   f':{service_endpoint_prefix}:action/{service_action}')

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            apigateway_stubber.stub_put_method, api_gtos.api_id, resource_id,
            http_method=rest_method)
        runner.add(
            apigateway_stubber.stub_put_method_response, api_gtos.api_id, resource_id,
            {'application/json': 'Empty'}, http_method=rest_method)
        runner.add(
            apigateway_stubber.stub_put_integration, api_gtos.api_id, resource_id,
            service_uri, http_method=rest_method, integ_type='AWS',
            integ_method=service_method, integ_role_arn=role_arn,
            integ_templates=mapping_template, passthrough='WHEN_NO_TEMPLATES')
        runner.add(
            apigateway_stubber.stub_put_integration_response, api_gtos.api_id,
            resource_id, {'application/json': ''}, http_method=rest_method)

    if error_code is None:
        api_gtos.add_integration_method(
            resource_id, rest_method, service_endpoint_prefix, service_action,
            service_method, role_arn, mapping_template)
    else:
        with pytest.raises(ClientError) as exc_info:
            api_gtos.add_integration_method(
                resource_id, rest_method, service_endpoint_prefix, service_action,
                service_method, role_arn, mapping_template)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_deploy_api(make_stubber, monkeypatch, error_code):
    apigateway_client = boto3.client('apigateway')
    apigateway_stubber = make_stubber(apigateway_client)
    api_gtos = ApiGatewayToService(apigateway_client)
    api_gtos.api_id = 'test-api-id'
    api_url = 'https://test-url'
    monkeypatch.setattr(api_gtos, 'api_url', lambda: api_url)
    stage = 'test-stage'

    apigateway_stubber.stub_create_deployment(
        api_gtos.api_id, stage, error_code=error_code)

    if error_code is None:
        got_api_url = api_gtos.deploy_api(stage)
        assert got_api_url == api_url
    else:
        with pytest.raises(ClientError) as exc_info:
            api_gtos.deploy_api(stage)
        assert exc_info.value.response['Error']['Code'] == error_code


def test_api_url():
    apigateway_client = boto3.client('apigateway')
    api_gtos = ApiGatewayToService(apigateway_client)
    api_gtos.api_id = 'test-api-id'
    api_gtos.stage = 'test'
    url = (f'https://{api_gtos.api_id}.execute-api.{apigateway_client.meta.region_name}'
           f'.amazonaws.com/{api_gtos.stage}')

    assert api_gtos.api_url('thing') == f'{url}/thing'


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_rest_api_id(make_stubber, error_code):
    apigateway_client = boto3.client('apigateway')
    apigateway_stubber = make_stubber(apigateway_client)
    api_gtos = ApiGatewayToService(apigateway_client)
    api_name = 'test-api_name-2'
    api_ids = [{'id': f'test-api-id-{index}', 'name': f'test-api_name-{index}'}
               for index in range(4)]

    apigateway_stubber.stub_get_rest_apis(api_ids, error_code=error_code)

    if error_code is None:
        got_api_id = api_gtos.get_rest_api_id(api_name)
        assert got_api_id == 'test-api-id-2'
    else:
        with pytest.raises(ClientError) as exc_info:
            api_gtos.get_rest_api_id(api_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_rest_api(make_stubber, error_code):
    apigateway_client = boto3.client('apigateway')
    apigateway_stubber = make_stubber(apigateway_client)
    api_gtos = ApiGatewayToService(apigateway_client)
    api_gtos.api_id = 'test-api-id'

    apigateway_stubber.stub_delete_rest_api(api_gtos.api_id, error_code=error_code)

    if error_code is None:
        api_gtos.delete_rest_api()
    else:
        with pytest.raises(ClientError) as exc_info:
            api_gtos.delete_rest_api()
        assert exc_info.value.response['Error']['Code'] == error_code
