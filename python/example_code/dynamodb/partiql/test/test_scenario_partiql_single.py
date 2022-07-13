# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Tests for scenario_partiql_single.py.
"""

from unittest.mock import patch

import boto3
from botocore.exceptions import ClientError
from botocore.stub import ANY
import pytest

from scaffold import Scaffold
import scenario_partiql_single as scenario


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_execute_statement')])
def test_run_scenario(make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    dyn_resource = boto3.resource('dynamodb')
    dyn_stubber = make_stubber(dyn_resource.meta.client)
    table_name = 'test-table'
    scaff = Scaffold(dyn_resource)
    wrapper = scenario.PartiQLWrapper(dyn_resource)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            dyn_stubber.stub_create_table, table_name,
            [{'name': 'year', 'type': 'N', 'key_type': 'HASH'},
             {'name': 'title', 'type': 'S', 'key_type': 'RANGE'}],
            {'read': 10, 'write': 10})
        runner.add(dyn_stubber.stub_describe_table, table_name)
        runner.add(dyn_stubber.stub_execute_statement, ANY, ANY, [])
        runner.add(dyn_stubber.stub_execute_statement, ANY, ANY, [{
            'title': {'S': 'test'}, 'year': {'N': '2000'}}])
        runner.add(dyn_stubber.stub_execute_statement, ANY, ANY, [])
        runner.add(dyn_stubber.stub_execute_statement, ANY, ANY, [{
            'title': {'S': 'test'}, 'year': {'N': '2000'}}])
        runner.add(dyn_stubber.stub_execute_statement, ANY, ANY, [])
        runner.add(dyn_stubber.stub_delete_table, table_name)

    if error_code is None:
        scenario.run_scenario(scaff, wrapper, table_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.run_scenario(scaff, wrapper, table_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.integ
def test_run_scenario_integ(monkeypatch):
    dyn_resource = boto3.resource('dynamodb')
    table_name = 'partiql-single-scenario-test-table'
    scaff = Scaffold(dyn_resource)
    wrapper = scenario.PartiQLWrapper(dyn_resource)

    with patch('builtins.print') as mock_print:
        scenario.run_scenario(scaff, wrapper, table_name)
        mock_print.assert_any_call("\nThanks for watching!")
