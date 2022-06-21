# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for action_wrapper.py.
"""

from datetime import datetime
import boto3
from botocore.exceptions import ClientError
import pytest

from action_wrapper import AutoScalingWrapper


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_create_group(make_stubber, error_code):
    autoscaling_client = boto3.client('autoscaling')
    autoscaling_stubber = make_stubber(autoscaling_client)
    wrapper = AutoScalingWrapper(autoscaling_client)
    group_name = 'test-group_name'
    zones = ['test-zone']
    launch_template_name = 'test-template'
    min_size = 1
    max_size = 3

    autoscaling_stubber.stub_create_auto_scaling_group(
        group_name, zones, launch_template_name, min_size, max_size, error_code=error_code)

    if error_code is None:
        wrapper.create_group(group_name, zones, launch_template_name, min_size, max_size)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_group(group_name, zones, launch_template_name, min_size, max_size)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_update_group(make_stubber, error_code):
    autoscaling_client = boto3.client('autoscaling')
    autoscaling_stubber = make_stubber(autoscaling_client)
    wrapper = AutoScalingWrapper(autoscaling_client)
    group_name = 'test-group_name'
    min_size = 1
    max_size = 3

    autoscaling_stubber.stub_update_auto_scaling_group(group_name, min_size, max_size, error_code=error_code)

    if error_code is None:
        wrapper.update_group(group_name, MinSize=min_size, MaxSize=max_size)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.update_group(group_name, MinSize=min_size, MaxSize=max_size)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_delete_group(make_stubber, error_code):
    autoscaling_client = boto3.client('autoscaling')
    autoscaling_stubber = make_stubber(autoscaling_client)
    wrapper = AutoScalingWrapper(autoscaling_client)
    group_name = 'test-group_name'

    autoscaling_stubber.stub_delete_auto_scaling_group(group_name, error_code=error_code)

    if error_code is None:
        wrapper.delete_group(group_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_group(group_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('groups, error_code', [
    ([{
        'AutoScalingGroupName': 'group-name-response',
        'MinSize': 1, 'MaxSize': 3, 'DesiredCapacity': 2, 'DefaultCooldown': 10,
        'AvailabilityZones': ['test-zone'], 'HealthCheckType': 'ECS', 'CreatedTime': datetime.now()
    }], None),
    ([], None),
    ([], 'TestException')])
def test_describe_group(make_stubber, groups, error_code):
    autoscaling_client = boto3.client('autoscaling')
    autoscaling_stubber = make_stubber(autoscaling_client)
    wrapper = AutoScalingWrapper(autoscaling_client)
    group_name = 'test-group_name'

    autoscaling_stubber.stub_describe_auto_scaling_groups(
        [group_name], groups, error_code=error_code)

    if error_code is None:
        got_group = wrapper.describe_group(group_name)
        if len(groups) > 0:
            assert got_group == groups[0]
        else:
            assert got_group is None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_group(group_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_terminate_instance(make_stubber, error_code):
    autoscaling_client = boto3.client('autoscaling')
    autoscaling_stubber = make_stubber(autoscaling_client)
    wrapper = AutoScalingWrapper(autoscaling_client)
    instance_id = 'test-instance_id'
    decrement = True
    activity = {
        'ActivityId': 'test-id', 'AutoScalingGroupName': 'test-group',
        'Cause': 'RunningTest', 'StartTime': datetime.now(), 'StatusCode': 'Testing'}

    autoscaling_stubber.stub_terminate_instance_in_auto_scaling_group(
        instance_id, decrement, activity, error_code=error_code)

    if error_code is None:
        got_activity = wrapper.terminate_instance(instance_id, decrement)
        assert got_activity == activity
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.terminate_instance(instance_id, decrement)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_set_desired_capacity(make_stubber, error_code):
    autoscaling_client = boto3.client('autoscaling')
    autoscaling_stubber = make_stubber(autoscaling_client)
    wrapper = AutoScalingWrapper(autoscaling_client)
    group_name = 'test-group_name'
    capacity = 3

    autoscaling_stubber.stub_set_desired_capacity(group_name, capacity, error_code=error_code)

    if error_code is None:
        wrapper.set_desired_capacity(group_name, capacity)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.set_desired_capacity(group_name, capacity)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_instances(make_stubber, error_code):
    autoscaling_client = boto3.client('autoscaling')
    autoscaling_stubber = make_stubber(autoscaling_client)
    wrapper = AutoScalingWrapper(autoscaling_client)
    instance_ids = [f'test-{index}' for index in range(3)]
    instances = [{
        'InstanceId': instance_ids[index],
        'AutoScalingGroupName': 'test-group',
        'AvailabilityZone': 'test-zone',
        'LifecycleState': 'testing',
        'HealthStatus': 'health-test',
        'ProtectedFromScaleIn': False
    } for index in range(3)]

    autoscaling_stubber.stub_describe_auto_scaling_instances(instance_ids, instances, error_code=error_code)

    if error_code is None:
        got_instances = wrapper.describe_instances(instance_ids)
        assert got_instances == instances
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_instances(instance_ids)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_describe_scaling_activities(make_stubber, error_code):
    autoscaling_client = boto3.client('autoscaling')
    autoscaling_stubber = make_stubber(autoscaling_client)
    wrapper = AutoScalingWrapper(autoscaling_client)
    group_name = 'test-group_name'
    activities = [{
        'ActivityId': f'test-id-{index}', 'AutoScalingGroupName': 'test-group',
        'Cause': 'RunningTest', 'StartTime': datetime.now(), 'StatusCode': 'Testing'
    } for index in range(3)]

    autoscaling_stubber.stub_describe_scaling_activities(group_name, activities, error_code=error_code)

    if error_code is None:
        got_activities = wrapper.describe_scaling_activities(group_name)
        assert got_activities == activities
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_scaling_activities(group_name)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_enable_metrics(make_stubber, error_code):
    autoscaling_client = boto3.client('autoscaling')
    autoscaling_stubber = make_stubber(autoscaling_client)
    wrapper = AutoScalingWrapper(autoscaling_client)
    group_name = 'test-group_name'
    metrics = ['test-metric-1', 'test-metric-2']

    autoscaling_stubber.stub_enable_metrics_collection(
        group_name, metrics, error_code=error_code)

    if error_code is None:
        wrapper.enable_metrics(group_name, metrics)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.enable_metrics(group_name, metrics)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_disable_metrics(make_stubber, error_code):
    autoscaling_client = boto3.client('autoscaling')
    autoscaling_stubber = make_stubber(autoscaling_client)
    wrapper = AutoScalingWrapper(autoscaling_client)
    group_name = 'test-group_name'

    autoscaling_stubber.stub_disable_metrics_collection(group_name, error_code=error_code)

    if error_code is None:
        wrapper.disable_metrics(group_name)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.disable_metrics(group_name)
        assert exc_info.value.response['Error']['Code'] == error_code
