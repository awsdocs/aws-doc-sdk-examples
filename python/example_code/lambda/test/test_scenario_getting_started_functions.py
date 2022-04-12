# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for scenario_getting_started_functions.py functions.
"""

import base64
import json
from unittest.mock import patch, MagicMock
import zipfile
import boto3
from botocore.exceptions import ClientError
import pytest

import scenario_getting_started_functions as scenario


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_role'),
    ('TestException', 'stub_attach_role_policy'),
    ('TestException', 'stub_get_role'),
    ('TestException', 'stub_create_function'),
    ('TestException', 'stub_invoke'),
    ('TestException', 'stub_update_function_code'),
    ('TestException', 'stub_update_function_configuration'),
    ('TestException', 'stub_list_functions'),
    ('TestException', 'stub_list_attached_role_policies'),
    ('TestException', 'stub_detach_role_policy'),
    ('TestException', 'stub_delete_role'),
    ('TestException', 'stub_delete_function'),
])
def test_run_scenario(make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)
    iam_resource = boto3.resource('iam')
    iam_stubber = make_stubber(iam_resource.meta.client)
    basic_file = 'test_basic_handler.py'
    calculator_file = 'test_calc_handler.py'
    lambda_name = 'test_lambda_name'
    func_arn = f'arn:aws:lambda:::function/{lambda_name}'
    iam_role = MagicMock(arn='arn:aws:iam:::role/test-role')
    inc_func_params = {'action': 'increment', 'number': 5}
    inc_response_payload = MagicMock(read=lambda: json.dumps({'result': 6}))
    env_vars = {'LOG_LEVEL': 'DEBUG'}
    plus_func_params = {'action': 'plus', 'x': 3, 'y': 6}
    plus_response_payload = MagicMock(read=lambda: json.dumps({'result': 9}))
    funcs = [{
        'FunctionName': f'test-func-{index}',
        'Description': f'test description {index}',
        'Runtime': f'test-runtime-{index}',
        'Handler': f'test-handler-{index}'}
        for index in range(3)]
    policy_arn = 'arn:aws:iam::111122223333:policy/test-policy'

    inputs = ['5', '', '1', '3', '6', '', 'n', 'y', 'y']
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))
    monkeypatch.setattr(scenario, 'wait', lambda x: None)
    monkeypatch.setattr(zipfile.ZipFile, 'write', lambda x, y, z: None)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(iam_stubber.stub_get_role, lambda_name, error_code='NoSuchEntity')
        runner.add(iam_stubber.stub_create_role, lambda_name, role_arn=iam_role.arn)
        runner.add(
            iam_stubber.stub_attach_role_policy, lambda_name,
            'arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole')
        runner.add(
            lambda_stubber.stub_get_function, lambda_name,
            error_code='ResourceNotFoundException')
        runner.add(iam_stubber.stub_get_role, lambda_name, role_arn=iam_role.arn)
        runner.add(
            lambda_stubber.stub_create_function,
            lambda_name, func_arn, iam_role.arn, f'{lambda_name}.lambda_handler')
        runner.add(lambda_stubber.stub_get_function, lambda_name, 'Active')
        runner.add(
            lambda_stubber.stub_invoke,
            lambda_name, json.dumps(inc_func_params), inc_response_payload,
            log_type='None')
        runner.add(lambda_stubber.stub_update_function_code, lambda_name, 'InProgress')
        runner.add(
            lambda_stubber.stub_get_function, lambda_name, update_status='Successful')
        runner.add(lambda_stubber.stub_update_function_configuration, lambda_name, env_vars)
        runner.add(
            lambda_stubber.stub_invoke,
            lambda_name, json.dumps(plus_func_params), plus_response_payload,
            log_type='Tail', log_result=base64.b64encode(b'test log result').decode())
        runner.add(lambda_stubber.stub_list_functions, funcs)
        runner.add(
            iam_stubber.stub_list_attached_role_policies, lambda_name,
            {'test-policy': policy_arn})
        runner.add(iam_stubber.stub_detach_role_policy, lambda_name, policy_arn)
        runner.add(iam_stubber.stub_delete_role, lambda_name)
        runner.add(lambda_stubber.stub_delete_function, lambda_name)

    if error_code is None:
        scenario.run_scenario(
            lambda_client, iam_resource, basic_file, calculator_file, lambda_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.run_scenario(
                lambda_client, iam_resource, basic_file, calculator_file, lambda_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.integ
def test_run_scenario_integ(monkeypatch):
    lambda_client = boto3.client('lambda')
    iam_resource = boto3.resource('iam')
    basic_file = 'lambda_handler_basic.py'
    calculator_file = 'lambda_handler_calculator.py'
    lambda_name = 'doc_example_lambda_calculator'

    inputs = ['5', '', '1', '3', '6', '', 'n', 'y', 'y']
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    with patch('builtins.print') as mock_print:
        scenario.run_scenario(
            lambda_client, iam_resource, basic_file, calculator_file, lambda_name)
        mock_print.assert_any_call("\nThanks for watching!")
