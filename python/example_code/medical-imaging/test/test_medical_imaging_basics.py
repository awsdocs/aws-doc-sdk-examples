# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for lambda_basics.py functions.
"""

import json
import unittest.mock
import boto3
from botocore.exceptions import ClientError
import pytest

from medical_imaging_basics import MedicalImagingWrapper



@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_function(make_stubber, make_unique_name, error_code):
    medical_imaging_client = boto3.client('medical-imaging')
    medical_imaging_stubber = make_stubber(medical_imaging_client)
    wrapper = MedicalImagingWrapper(medical_imaging_client, None)
    datastore_name = make_unique_name('datastore-')
    datastore_id = ''

    medical_imaging_stubber.stub_create_datastore(
        datastore_name,
        error_code=error_code)

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
