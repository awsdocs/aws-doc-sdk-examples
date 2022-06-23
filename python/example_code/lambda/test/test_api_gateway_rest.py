# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for api_gateway_rest.py functions.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

import api_gateway_rest


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_rest_api'),
    ('TestException', 'stub_get_resources'),
    ('TestException', 'stub_create_resource'),
    ('TestException', 'stub_put_method'),
    ('TestException', 'stub_put_integration'),
    ('TestException', 'stub_create_deployment'),
    ('TestException', 'stub_add_permission'),
])
def test_create_rest_api(
        make_stubber, make_unique_name, stub_runner, error_code, stop_on_method):
    apig_client = boto3.client('apigateway')
    apig_stubber = make_stubber(apig_client)
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    api_name = make_unique_name('api-')
    api_base_path = 'test-path'
    api_stage = 'test'
    account_id = '123456789012'
    lambda_func_arn = 'aws;arn:lambda:::function/test-func'
    rest_api_id = 'rest-id'
    root_id = 'root-id'
    resource_id = 'resource-id'
    lambda_uri = \
        f'arn:aws:apigateway:{apig_client.meta.region_name}:' \
        f'lambda:path/2015-03-31/functions/{lambda_func_arn}/invocations'
    apig_source_arn = \
        f'arn:aws:execute-api:{apig_client.meta.region_name}:' \
        f'{account_id}:{rest_api_id}/*/*/{api_base_path}'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(apig_stubber.stub_create_rest_api, api_name, rest_api_id)
        runner.add(apig_stubber.stub_get_resources, rest_api_id, [
            {'id': 'not-the-root-id', 'path': '/not-the-root'},
            {'id': root_id, 'path': '/'}])
        runner.add(
            apig_stubber.stub_create_resource, rest_api_id, root_id, api_base_path,
            resource_id)
        runner.add(apig_stubber.stub_put_method, rest_api_id, resource_id)
        runner.add(
            apig_stubber.stub_put_integration, rest_api_id, resource_id, lambda_uri)
        runner.add(apig_stubber.stub_create_deployment, rest_api_id, api_stage)
        runner.add(
            lambda_stubber.stub_add_permission, lambda_func_arn,
            'lambda:InvokeFunction', 'apigateway.amazonaws.com', apig_source_arn)

    if error_code is None:
        got_api_id = api_gateway_rest.create_rest_api(
            apig_client, api_name, api_base_path, api_stage, account_id,
            lambda_client, lambda_func_arn)
        assert got_api_id == rest_api_id
    else:
        with pytest.raises(ClientError) as exc_info:
            api_gateway_rest.create_rest_api(
                apig_client, api_name, api_base_path, api_stage, account_id,
                lambda_client, lambda_func_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_rest_api(make_stubber, error_code):
    apig_client = boto3.client('apigateway')
    apig_stubber = make_stubber(apig_client)
    api_id = 'test-id'

    apig_stubber.stub_delete_rest_api(api_id, error_code=error_code)

    if error_code is None:
        api_gateway_rest.delete_rest_api(apig_client, api_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            api_gateway_rest.delete_rest_api(apig_client, api_id)
        assert exc_info.value.response['Error']['Code'] == error_code
