# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for lambda_basics.py functions.
"""

import json
import unittest.mock
import zipfile
import boto3
from botocore.exceptions import ClientError
import pytest

from lambda_basics import LambdaWrapper


def test_create_lambda_deployment_package(monkeypatch):
    monkeypatch.setattr(zipfile.ZipFile, 'write', lambda x, y, z: None)
    wrapper = LambdaWrapper(None, None)

    got_package = wrapper.create_deployment_package('test-file', 'other-file')
    assert got_package is not None


@pytest.mark.parametrize(
    'error_code,stop_on_method', [
        (None, None),
        ('TestException', 'stub_create_role'),
        ('TestException', 'stub_attach_role_policy')
    ])
def test_create_iam_role_for_lambda(
        make_stubber, make_unique_name, stub_runner, error_code, stop_on_method):
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    wrapper = LambdaWrapper(None, iam_resource)
    role_name = make_unique_name('role-')

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(iam_stubber.stub_get_role, role_name, error_code='NoSuchEntity')
        runner.add(iam_stubber.stub_create_role, role_name)
        runner.add(
            iam_stubber.stub_attach_role_policy, role_name,
            'arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole')

    if error_code is None:
        got_role, got_created = wrapper.create_iam_role_for_lambda(role_name)
        assert got_role.name == role_name
        assert got_created
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_iam_role_for_lambda(role_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_function(make_stubber, make_unique_name, error_code):
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    wrapper = LambdaWrapper(lambda_client, None)
    func_name = make_unique_name('func-')
    handler_name = make_unique_name('handler-')
    iam_role = unittest.mock.MagicMock(arn='arn:aws:iam:::role/test-role')
    test_package = 'test-package'
    func_arn = f'arn:aws:lambda:::function/{func_name}'

    lambda_stubber.stub_create_function(
        func_name, func_arn, iam_role.arn, handler_name, test_package,
        error_code=error_code)
    if error_code is None:
        lambda_stubber.stub_get_function(func_name, 'Active')

    if error_code is None:
        got_arn = wrapper.create_function(func_name, handler_name, iam_role, test_package)
        assert got_arn == func_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_function(
                func_name, handler_name, iam_role, test_package)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [
    None, 'TestException', 'ResourceNotFoundException'])
def test_get_function(make_stubber, error_code):
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    wrapper = LambdaWrapper(lambda_client, None)
    func_name = 'test-func_name'

    lambda_stubber.stub_get_function(func_name, error_code=error_code)

    if error_code in (None, 'ResourceNotFoundException'):
        wrapper.get_function(func_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_function(func_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_function(make_stubber, make_unique_name, error_code):
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    wrapper = LambdaWrapper(lambda_client, None)
    func_name = make_unique_name('func-')

    lambda_stubber.stub_delete_function(func_name, error_code=error_code)

    if error_code is None:
        wrapper.delete_function(func_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_function(func_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_invoke_function(make_stubber, make_unique_name, error_code):
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    wrapper = LambdaWrapper(lambda_client, None)
    func_name = make_unique_name('func-')
    func_params = {'param1': 'test', 'param2': 35}
    response_payload = 'ahoy there'

    lambda_stubber.stub_invoke(
        func_name, json.dumps(func_params), response_payload, log_type='None',
        error_code=error_code)

    if error_code is None:
        response = wrapper.invoke_function(func_name, func_params)
        assert response['Payload'] == response_payload
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.invoke_function(func_name, func_params)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_update_function_code(make_stubber, error_code):
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    wrapper = LambdaWrapper(lambda_client, None)
    func_name = 'test-func_name'
    package = 'test-package'
    update_status = 'InProgress'

    lambda_stubber.stub_update_function_code(
        func_name, update_status, package=package, error_code=error_code)

    if error_code is None:
        got_response = wrapper.update_function_code(func_name, package)
        assert got_response['LastUpdateStatus'] == update_status
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.update_function_code(func_name, package)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_update_function_configuration(make_stubber, error_code):
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    wrapper = LambdaWrapper(lambda_client, None)
    func_name = 'test-func_name'
    env_vars = {'test-key': 'test-val'}

    lambda_stubber.stub_update_function_configuration(
        func_name, env_vars, error_code=error_code)

    if error_code is None:
        got_response = wrapper.update_function_configuration(func_name, env_vars)
        assert got_response
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.update_function_configuration(func_name, env_vars)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_functions(make_stubber, error_code):
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    wrapper = LambdaWrapper(lambda_client, None)
    funcs = [{
        'FunctionName': f'test-func-{index}',
        'Description': f'test description {index}',
        'Runtime': f'test-runtime-{index}',
        'Handler': f'test-handler-{index}'}
        for index in range(3)]

    lambda_stubber.stub_list_functions(funcs, error_code=error_code)

    if error_code is None:
        wrapper.list_functions()
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_functions()
        assert exc_info.value.response['Error']['Code'] == error_code
