# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Unit tests for cloudwatch_basics.py
"""

from datetime import datetime, timedelta
from unittest.mock import MagicMock
import boto3
from botocore.exceptions import ClientError
import pytest

from cloudwatch_basics import CloudWatchWrapper


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_list_metrics(make_stubber, error_code):
    cloudwatch_resource = boto3.resource('cloudwatch')
    cloudwatch_stubber = make_stubber(cloudwatch_resource.meta.client)
    cw_wrapper = CloudWatchWrapper(cloudwatch_resource)
    namespace = 'test-namespace'
    name = 'test-name'
    metrics = [cloudwatch_resource.Metric(namespace, name) for _ in range(5)]

    cloudwatch_stubber.stub_list_metrics(
        namespace, name, metrics, recent=True, error_code=error_code)

    if error_code is None:
        got_metric_iter = cw_wrapper.list_metrics(namespace, name, True)
        assert list(got_metric_iter) == metrics
    else:
        with pytest.raises(ClientError) as exc_info:
            list(cw_wrapper.list_metrics(namespace, name, True))
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_put_metric_data(make_stubber, error_code):
    cloudwatch_resource = boto3.resource('cloudwatch')
    cloudwatch_stubber = make_stubber(cloudwatch_resource.meta.client)
    cw_wrapper = CloudWatchWrapper(cloudwatch_resource)
    namespace = 'test-namespace'
    name = 'test-name'
    value = 66
    unit = 'Terabytes'

    cloudwatch_stubber.stub_put_metric_data(
        namespace, name, value, unit, error_code=error_code)

    if error_code is None:
        cw_wrapper.put_metric_data(namespace, name, value, unit)
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.put_metric_data(namespace, name, value, unit)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_put_metric_data_set(make_stubber, error_code):
    cloudwatch_resource = boto3.resource('cloudwatch')
    cloudwatch_stubber = make_stubber(cloudwatch_resource.meta.client)
    cw_wrapper = CloudWatchWrapper(cloudwatch_resource)
    namespace = 'test-namespace'
    name = 'test-name'
    timestamp = datetime.now()
    unit = 'Milliseconds'
    data_set = {
        'values': [1, 2, 3, 4],
        'counts': [5, 6, 7, 8]}

    cloudwatch_stubber.stub_put_metric_data_set(
        namespace, name, timestamp, unit, data_set, error_code=error_code)

    if error_code is None:
        cw_wrapper.put_metric_data_set(
            namespace, name, timestamp, unit, data_set)
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.put_metric_data_set(
                namespace, name, timestamp, unit, data_set)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_metric_statistics(make_stubber, error_code):
    cloudwatch_resource = boto3.resource('cloudwatch')
    cloudwatch_stubber = make_stubber(cloudwatch_resource.meta.client)
    cw_wrapper = CloudWatchWrapper(cloudwatch_resource)
    namespace = 'test-namespace'
    name = 'test-name'
    start = datetime.now() - timedelta(hours=3)
    end = datetime.now() - timedelta(hours=1)
    period = 60
    stat_type = 'Average'
    stats = [1, 2, 3, 4]

    cloudwatch_stubber.stub_get_metric_statistics(
        namespace, name, start, end, period, stat_type, stats, error_code=error_code)

    if error_code is None:
        got_stats = cw_wrapper.get_metric_statistics(
            namespace, name, start, end, period, [stat_type])
        assert got_stats['Label'] == name
        assert [stat[stat_type] for stat in got_stats['Datapoints']] == stats
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.get_metric_statistics(
                namespace, name, start, end, period, [stat_type])
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_metric_alarm(make_stubber, error_code):
    cloudwatch_resource = boto3.resource('cloudwatch')
    cloudwatch_stubber = make_stubber(cloudwatch_resource.meta.client)
    cw_wrapper = CloudWatchWrapper(cloudwatch_resource)
    metric_namespace = 'test-namespace'
    metric_name = 'test-name'
    alarm_name = 'test-alarm'
    stat_type = 'Average'
    period = 60
    eval_periods = 3
    threshold = 66
    comparison_op = 'LessThanThreshold'

    cloudwatch_stubber.stub_put_metric_alarm(
        metric_namespace, metric_name, alarm_name, stat_type, period, eval_periods,
        threshold, comparison_op, error_code=error_code)

    if error_code is None:
        got_alarm = cw_wrapper.create_metric_alarm(
            metric_namespace, metric_name, alarm_name, stat_type, period,
            eval_periods, threshold, comparison_op)
        assert got_alarm.name == alarm_name
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.create_metric_alarm(
                metric_namespace, metric_name, alarm_name, stat_type, period,
                eval_periods, threshold, comparison_op)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_metric_alarms(make_stubber, error_code):
    cloudwatch_resource = boto3.resource('cloudwatch')
    cloudwatch_stubber = make_stubber(cloudwatch_resource.meta.client)
    cw_wrapper = CloudWatchWrapper(cloudwatch_resource)
    namespace = 'test-namespace'
    name = 'test-name'
    alarms = []
    for index in range(5):
        alarm = MagicMock(alarm_arn=f'arn-{index}')
        alarm.name = f'alarm-{index}'
        alarms.append(alarm)

    cloudwatch_stubber.stub_describe_alarms_for_metric(
        namespace, name, alarms, error_code=error_code)

    if error_code is None:
        got_alarms = cw_wrapper.get_metric_alarms(namespace, name)
        assert [{a.name: a.alarm_arn} for a in got_alarms] == [
            {a.name: a.alarm_arn} for a in alarms]
    else:
        with pytest.raises(ClientError) as exc_info:
            got_alarms = cw_wrapper.get_metric_alarms(namespace, name)
            list(got_alarms)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('enable,error_code', [
    (True, None),
    (False, None),
    (True, 'TestException')])
def test_enable_alarm_actions(make_stubber, enable, error_code):
    cloudwatch_resource = boto3.resource('cloudwatch')
    cloudwatch_stubber = make_stubber(cloudwatch_resource.meta.client)
    cw_wrapper = CloudWatchWrapper(cloudwatch_resource)
    alarm_name = 'test-alarm_name'

    if enable:
        cloudwatch_stubber.stub_enable_alarm_actions(
            alarm_name, error_code=error_code)
    else:
        cloudwatch_stubber.stub_disable_alarm_actions(
            alarm_name, error_code=error_code)

    if error_code is None:
        cw_wrapper.enable_alarm_actions(alarm_name, enable)
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.enable_alarm_actions(alarm_name, enable)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_metric_alarms(make_stubber, error_code):
    cloudwatch_resource = boto3.resource('cloudwatch')
    cloudwatch_stubber = make_stubber(cloudwatch_resource.meta.client)
    cw_wrapper = CloudWatchWrapper(cloudwatch_resource)
    namespace = 'test-namespace'
    name = 'test-name'
    alarms = []
    for index in range(5):
        alarm = MagicMock(alarm_arn=f'arn-{index}')
        alarm.name = f'alarm-{index}'
        alarms.append(alarm)

    cloudwatch_stubber.stub_describe_alarms_for_metric(namespace, name, alarms)
    cloudwatch_stubber.stub_delete_alarms(alarms, error_code=error_code)

    if error_code is None:
        cw_wrapper.delete_metric_alarms(namespace, name)
    else:
        with pytest.raises(ClientError) as exc_info:
            cw_wrapper.delete_metric_alarms(namespace, name)
        assert exc_info.value.response['Error']['Code'] == error_code
