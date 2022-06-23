# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for scheduled_lambda.py functions.
"""

import boto3
from botocore.exceptions import ClientError
import pytest

import scheduled_lambda


@pytest.mark.parametrize('failed_target_count,error_code,stop_on_method', [
    (0, None, None),
    (1, None, None),
    (0, 'TestException', 'stub_put_rule'),
    (0, 'TestException', 'stub_add_permission'),
    (0, 'TestException', 'stub_put_targets')
])
def test_schedule_lambda_function(
        make_stubber, stub_runner, failed_target_count, error_code, stop_on_method):
    eventbridge_client = boto3.client('events')
    eventbridge_stubber = make_stubber(eventbridge_client)
    lambda_client = boto3.client('lambda')
    lambda_stubber = make_stubber(lambda_client)

    event_rule_name = 'test-rule'
    event_schedule = 'test-schedule'
    event_rule_arn = f'arn:aws:events:::rules/{event_rule_name}'
    lambda_func_name = 'test-func'
    lambda_func_arn = f'arn:aws:lambda:::functions/{lambda_func_name}'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            eventbridge_stubber.stub_put_rule,
            event_rule_name, event_schedule, event_rule_arn)
        runner.add(
            lambda_stubber.stub_add_permission, lambda_func_name,
            'lambda:InvokeFunction', 'events.amazonaws.com', event_rule_arn)
        runner.add(
            eventbridge_stubber.stub_put_targets,
            event_rule_name, [{'Id': lambda_func_name, 'Arn': lambda_func_arn}],
            failed_count=failed_target_count)

    if error_code is None:
        got_arn = scheduled_lambda.schedule_lambda_function(
            eventbridge_client, event_rule_name, event_schedule,
            lambda_client, lambda_func_name, lambda_func_arn)
        assert got_arn == event_rule_arn
    else:
        with pytest.raises(ClientError) as exc_info:
            scheduled_lambda.schedule_lambda_function(
                eventbridge_client, event_rule_name, event_schedule,
                lambda_client, lambda_func_name, lambda_func_arn)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('enable,error_code', [
    (True, None),
    (False, None),
    (True, 'TestException')
])
def test_update_event(make_stubber, enable, error_code):
    eventbridge_client = boto3.client('events')
    eventbridge_stubber = make_stubber(eventbridge_client)
    event_rule_name = 'test-rule'

    if enable:
        eventbridge_stubber.stub_enable_rule(event_rule_name, error_code=error_code)
    else:
        eventbridge_stubber.stub_disable_rule(event_rule_name, error_code=error_code)

    if error_code is None:
        scheduled_lambda.update_event_rule(eventbridge_client, event_rule_name, enable)
    else:
        with pytest.raises(ClientError) as exc_info:
            scheduled_lambda.update_event_rule(
                eventbridge_client, event_rule_name, enable)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('state,error_code', [
    ('ENABLED', None),
    ('DISABLED', None),
    ('ENABLED', 'TestException')])
def test_get_event_enabled(make_stubber, state, error_code):
    eventbridge_client = boto3.client('events')
    eventbridge_stubber = make_stubber(eventbridge_client)
    event_rule_name = 'test-rule'

    eventbridge_stubber.stub_describe_rule(
        event_rule_name, state, error_code=error_code)

    if error_code is None:
        got_enabled = scheduled_lambda.get_event_rule_enabled(
            eventbridge_client, event_rule_name)
        assert got_enabled == (state == 'ENABLED')
    else:
        with pytest.raises(ClientError) as exc_info:
            scheduled_lambda.get_event_rule_enabled(eventbridge_client, event_rule_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code,stop_on_method', [
    (None, None), ('TestException', 'stub_remove_targets')])
def test_delete_event(make_stubber, stub_runner, error_code, stop_on_method):
    eventbridge_client = boto3.client('events')
    eventbridge_stubber = make_stubber(eventbridge_client)
    event_rule_name = 'test-rule'
    lambda_func_name = 'test-func'

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            eventbridge_stubber.stub_remove_targets,
            event_rule_name, [lambda_func_name])
        runner.add(
            eventbridge_stubber.stub_delete_rule, event_rule_name)

    if error_code is None:
        scheduled_lambda.delete_event_rule(
            eventbridge_client, event_rule_name, lambda_func_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            scheduled_lambda.delete_event_rule(
                eventbridge_client, event_rule_name, lambda_func_name)
            assert exc_info.value.response['Error']['Code'] == error_code
