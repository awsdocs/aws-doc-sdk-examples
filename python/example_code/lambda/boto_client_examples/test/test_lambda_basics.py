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

import lambda_basics


def test_create_lambda_deployment_package(monkeypatch):
    monkeypatch.setattr(zipfile.ZipFile, 'write', lambda x, y: None)

    got_package = lambda_basics.create_lambda_deployment_package('test-file')
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
    role_name = make_unique_name('role-')

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(iam_stubber.stub_create_role, role_name)
        runner.add(iam_stubber.stub_get_role, role_name)
        runner.add(
            iam_stubber.stub_attach_role_policy, role_name,
            'arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole')

    if error_code is None:
        got_role = lambda_basics.create_iam_role_for_lambda(
            iam_resource, role_name)
        assert got_role.name == role_name
    else:
        with pytest.raises(ClientError) as exc_info:
            lambda_basics.create_iam_role_for_lambda(
                iam_resource, role_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_deploy_lambda_function(make_stubber, make_unique_name, error_code):
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    func_name = make_unique_name('func-')
    handler_name = make_unique_name('handler-')
    iam_role = unittest.mock.MagicMock(arn='arn:aws:iam:::role/test-role')
    test_package = 'test-package'
    func_arn = f'arn:aws:lambda:::function/{func_name}'

    lambda_stubber.stub_create_function(
        func_name, func_arn, iam_role.arn, handler_name, test_package,
        error_code=error_code)

    if error_code is None:
        got_arn = lambda_basics.deploy_lambda_function(
            lambda_client, func_name, handler_name, iam_role, test_package)
        assert got_arn == func_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            lambda_basics.deploy_lambda_function(
                lambda_client, func_name, handler_name, iam_role, test_package)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_lambda_function(make_stubber, make_unique_name, error_code):
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    func_name = make_unique_name('func-')

    lambda_stubber.stub_delete_function(func_name, error_code=error_code)

    if error_code is None:
        lambda_basics.delete_lambda_function(lambda_client, func_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            lambda_basics.delete_lambda_function(lambda_client, func_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_invoke_lambda_function(make_stubber, make_unique_name, error_code):
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    func_name = make_unique_name('func-')
    func_params = {'param1': 'test', 'param2': 35}
    response_payload = 'ahoy there'

    lambda_stubber.stub_invoke(
        func_name, json.dumps(func_params).encode(), response_payload,
        error_code=error_code)

    if error_code is None:
        response = lambda_basics.invoke_lambda_function(
            lambda_client, func_name, func_params)
        assert response['Payload'] == response_payload
    else:
        with pytest.raises(ClientError) as exc_info:
            lambda_basics.invoke_lambda_function(
                lambda_client, func_name, func_params)
        assert exc_info.value.response['Error']['Code'] == error_code
