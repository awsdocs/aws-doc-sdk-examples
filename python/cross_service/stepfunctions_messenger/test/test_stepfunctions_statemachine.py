# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for stepfunctions_statemachine.py
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from stepfunctions_statemachine import StepFunctionsStateMachine


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create(make_stubber, error_code):
    stepfunctions_client = boto3.client('stepfunctions')
    stepfunctions_stubber = make_stubber(stepfunctions_client)
    state_machine = StepFunctionsStateMachine(stepfunctions_client)
    name = 'test-name'
    definition = {'Comment': 'test-definition'}
    role_arn = 'test-role-arn'
    arn = 'test-arn'

    stepfunctions_stubber.stub_create_state_machine(
        name, definition, role_arn, arn, error_code=error_code)

    if error_code is None:
        got_arn = state_machine.create(name, definition, role_arn)
        assert got_arn == arn
        assert state_machine.state_machine_name == name
        assert state_machine.state_machine_arn == arn
    else:
        with pytest.raises(ClientError) as exc_info:
            state_machine.create(name, definition, role_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('role_arn,error_code', [
    (None, None), ('test-role-arn', None), (None, 'TestException')])
def test_update(make_stubber, role_arn, error_code):
    stepfunctions_client = boto3.client('stepfunctions')
    stepfunctions_stubber = make_stubber(stepfunctions_client)
    state_machine = StepFunctionsStateMachine(stepfunctions_client)
    state_machine.state_machine_arn = 'test-arn'
    definition = {'Comment': 'test-definition'}

    stepfunctions_stubber.stub_update_state_machine(
        state_machine.state_machine_arn, definition, role_arn, error_code=error_code)

    if error_code is None:
        state_machine.update(definition, role_arn)
    else:
        with pytest.raises(ClientError) as exc_info:
            state_machine.update(definition, role_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete(make_stubber, error_code):
    stepfunctions_client = boto3.client('stepfunctions')
    stepfunctions_stubber = make_stubber(stepfunctions_client)
    state_machine = StepFunctionsStateMachine(stepfunctions_client)
    state_machine.state_machine_arn = 'test-state_machine_arn'

    stepfunctions_stubber.stub_delete_state_machine(
        state_machine.state_machine_arn, error_code=error_code)

    if error_code is None:
        state_machine.delete()
    else:
        with pytest.raises(ClientError) as exc_info:
            state_machine.delete()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('found,error_code', [
    (True, None), (False, None), (True, 'TestException')])
def test_find(make_stubber, found, error_code):
    stepfunctions_client = boto3.client('stepfunctions')
    stepfunctions_stubber = make_stubber(stepfunctions_client)
    state_machine = StepFunctionsStateMachine(stepfunctions_client)
    state_machine_name = 'test-state_machine_name'
    state_machine_arn = 'test-arn'
    machine_data = [('wrong-name', 'wrong-arn')]
    if found:
        machine_data.append((state_machine_name, state_machine_arn))
    state_machines = [
        {'name': name, 'stateMachineArn': arn} for name, arn in machine_data]

    stepfunctions_stubber.stub_list_state_machines(
        state_machines, error_code=error_code)

    if error_code is None:
        got_state_machine_arn = state_machine.find(state_machine_name)
        if found:
            assert got_state_machine_arn == state_machine_arn
        else:
            assert got_state_machine_arn is None
    else:
        with pytest.raises(ClientError) as exc_info:
            state_machine.find(state_machine_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe(make_stubber, error_code):
    stepfunctions_client = boto3.client('stepfunctions')
    stepfunctions_stubber = make_stubber(stepfunctions_client)
    state_machine = StepFunctionsStateMachine(stepfunctions_client)
    state_machine.state_machine_arn = 'test-state_machine_arn'
    name = 'test-name'
    definition = 'test-definition'
    role_arn = 'test-role_arn'

    stepfunctions_stubber.stub_describe_state_machine(
        state_machine.state_machine_arn, name, definition, role_arn,
        error_code=error_code)

    if error_code is None:
        got_response = state_machine.describe()
        assert got_response['name'] == name
        assert got_response['definition'] == definition
        assert got_response['roleArn'] == role_arn
        assert got_response['stateMachineArn'] == state_machine.state_machine_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            state_machine.describe()
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('run_input,error_code', [
    ({'test-key': 'test-value'}, None),
    (None, None),
    ({'test-key': 'test-value'}, 'TestException')])
def test_start_run(make_stubber, run_input, error_code):
    stepfunctions_client = boto3.client('stepfunctions')
    stepfunctions_stubber = make_stubber(stepfunctions_client)
    state_machine = StepFunctionsStateMachine(stepfunctions_client)
    state_machine.state_machine_arn = 'test-arn'
    run_name = 'test-run_name'
    run_arn = 'test-run_arn'

    stepfunctions_stubber.stub_start_execution(
        state_machine.state_machine_arn, run_name, run_arn, run_input,
        error_code=error_code)

    if error_code is None:
        got_run_arn = state_machine.start_run(run_name, run_input)
        assert got_run_arn == run_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            state_machine.start_run(run_name, run_input)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('run_status,error_code', [
    ('test-run_status', None), (None, None), ('test-run_status', 'TestException')])
def test_list_runs(make_stubber, run_status, error_code):
    stepfunctions_client = boto3.client('stepfunctions')
    stepfunctions_stubber = make_stubber(stepfunctions_client)
    state_machine = StepFunctionsStateMachine(stepfunctions_client)
    state_machine.state_machine_arn = 'test-arn'
    runs = [{'name': name, 'executionArn': arn} for name, arn in
            [('run-name-1', 'run-arn-1'), ('run-name-2', 'run-arn-2')]]

    stepfunctions_stubber.stub_list_executions(
        state_machine.state_machine_arn, runs, run_status, error_code=error_code)

    if error_code is None:
        got_runs = state_machine.list_runs(run_status)
        assert [{'name': run['name'], 'executionArn': run['executionArn']}
                for run in got_runs] == runs
    else:
        with pytest.raises(ClientError) as exc_info:
            state_machine.list_runs(run_status)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_stop_run(make_stubber, error_code):
    stepfunctions_client = boto3.client('stepfunctions')
    stepfunctions_stubber = make_stubber(stepfunctions_client)
    state_machine = StepFunctionsStateMachine(stepfunctions_client)
    run_arn = 'test-run_arn'
    cause = 'test cause'

    stepfunctions_stubber.stub_stop_execution(run_arn, cause, error_code=error_code)

    if error_code is None:
        state_machine.stop_run(run_arn, cause)
    else:
        with pytest.raises(ClientError) as exc_info:
            state_machine.stop_run(run_arn, cause)
        assert exc_info.value.response['Error']['Code'] == error_code
