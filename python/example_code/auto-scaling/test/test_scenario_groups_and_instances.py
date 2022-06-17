# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from datetime import datetime
from unittest.mock import MagicMock, patch
import boto3
from botocore.exceptions import ClientError
from botocore.stub import ANY
import pytest

import scenario_groups_and_instances as scenario


@pytest.mark.parametrize('error_code, stop_on_method', [
    (None, None),
    ('TestException', 'stub_create_launch_template'),
    ('TestException', 'stub_describe_availability_zones'),
    ('TestException', 'stub_create_auto_scaling_group'),
    ('TestException', 'stub_describe_auto_scaling_groups'),
    ('TestException', 'stub_describe_auto_scaling_instances'),
    ('TestException', 'stub_enable_metrics_collection'),
    ('TestException', 'stub_update_auto_scaling_group'),
    ('TestException', 'stub_set_desired_capacity'),
    ('TestException', 'stub_terminate_instance_in_auto_scaling_group'),
    ('TestException', 'stub_describe_scaling_activities'),
    ('TestException', 'stub_list_metrics'),
    ('TestException', 'stub_get_metric_statistics'),
    ('TestException', 'stub_disable_metrics_collection'),
    ('TestException', 'stub_delete_auto_scaling_group'),
    ('TestException', 'stub_delete_launch_template')
])
def test_get_launch_template(make_stubber, stub_runner, monkeypatch, error_code, stop_on_method):
    as_client = boto3.client('autoscaling')
    as_stubber = make_stubber(as_client)
    ec2_client = boto3.client('ec2')
    ec2_stubber = make_stubber(ec2_client)
    cw_resource = boto3.resource('cloudwatch')
    cw_stubber = make_stubber(cw_resource.meta.client)
    as_wrapper = scenario.AutoScalingWrapper(as_client)
    svc_helper = scenario.ServiceHelper(ec2_client, cw_resource)
    template_name = 'test-template'
    zone = 'test-zone'
    group_name = 'test-group'
    instance_ids = [f'test-{index}' for index in range(3)]
    group_instances = [{
        'InstanceId': instance_ids[index],
        'AvailabilityZone': 'test-zone',
        'LifecycleState': 'InService',
        'HealthStatus': 'health-test',
        'ProtectedFromScaleIn': False
    } for index in range(3)]
    instances = []
    for i in group_instances:
        icopy = i.copy()
        icopy['AutoScalingGroupName'] = group_name
        instances.append(icopy)
    group = {
        'AutoScalingGroupName': group_name, 'LaunchTemplate': {'LaunchTemplateName': template_name},
        'MinSize': 1, 'MaxSize': 3,
        'DesiredCapacity': 2, 'DefaultCooldown': 10, 'AvailabilityZones': [zone],
        'HealthCheckType': 'ECS',  'CreatedTime': datetime.now(),
        'Instances': group_instances}
    activities = [{
        'ActivityId': f'test-id-{index}', 'AutoScalingGroupName': 'test-group',
        'Cause': 'RunningTest', 'StartTime': datetime.now(), 'StatusCode': 'Testing'
    } for index in range(3)]
    metric = MagicMock(
        namespace='AWS/AutoScaling', dimensions=[{'Name': 'AutoScalingGroupName', 'Value': group_name}])
    metric.name = 'test-metric'

    inputs = [template_name, template_name, group_name, 1, 'y', '', '', 1, '', 1, 'n',
              '', 'y']
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))
    monkeypatch.setattr(scenario, 'wait', lambda x: None)

    with stub_runner(error_code, stop_on_method) as runner:
        runner.add(
            ec2_stubber.stub_describe_launch_templates, [template_name], [],
            'InvalidLaunchTemplateName.NotFoundException')
        runner.add(
            ec2_stubber.stub_create_launch_template, template_name, 't1.micro', ANY)
        runner.add(ec2_stubber.stub_describe_availability_zones, [zone])
        runner.add(
            as_stubber.stub_create_auto_scaling_group, group_name, [zone], template_name, 1, 1)
        runner.add(
            as_stubber.stub_describe_auto_scaling_groups, [group_name], [group])
        runner.add(
            as_stubber.stub_describe_auto_scaling_groups, [group_name], [group])
        runner.add(
            as_stubber.stub_describe_auto_scaling_instances, instance_ids, instances)
        runner.add(as_stubber.stub_enable_metrics_collection, group_name, [
            'GroupMinSize', 'GroupMaxSize', 'GroupDesiredCapacity',
            'GroupInServiceInstances', 'GroupTotalInstances'])
        runner.add(as_stubber.stub_update_auto_scaling_group, group_name, max_size=3)
        runner.add(
            as_stubber.stub_describe_auto_scaling_groups, [group_name], [group])
        runner.add(as_stubber.stub_set_desired_capacity, group_name, 2)
        runner.add(
            as_stubber.stub_describe_auto_scaling_groups, [group_name], [group])
        runner.add(
            as_stubber.stub_describe_auto_scaling_groups, [group_name], [group])
        runner.add(
            as_stubber.stub_describe_auto_scaling_instances, instance_ids, instances)
        runner.add(
            as_stubber.stub_terminate_instance_in_auto_scaling_group, instance_ids[0],
            False, activities[0])
        runner.add(
            as_stubber.stub_describe_auto_scaling_groups, [group_name], [group])
        runner.add(
            as_stubber.stub_describe_auto_scaling_groups, [group_name], [group])
        runner.add(
            as_stubber.stub_describe_auto_scaling_instances, instance_ids, instances)
        runner.add(
            as_stubber.stub_describe_scaling_activities, group_name, activities)
        runner.add(
            cw_stubber.stub_list_metrics, 'AWS/AutoScaling', dimensions=metric.dimensions,
            metrics=[metric])
        runner.add(
            cw_stubber.stub_get_metric_statistics, metric.namespace, metric.name,
            ANY, ANY, 60, 'Sum', [1], dimensions=metric.dimensions)
        runner.add(as_stubber.stub_disable_metrics_collection, group_name)
        runner.add(as_stubber.stub_update_auto_scaling_group, group_name, min_size=0)
        runner.add(
            as_stubber.stub_describe_auto_scaling_groups, [group_name], [group])
        for inst_id in instance_ids:
            runner.add(
                as_stubber.stub_terminate_instance_in_auto_scaling_group, inst_id, True,
                activities[0])
        runner.add(
            as_stubber.stub_describe_auto_scaling_groups, [group_name], [group])
        runner.add(
            as_stubber.stub_describe_auto_scaling_instances, instance_ids, instances)
        runner.add(as_stubber.stub_delete_auto_scaling_group, group_name)
        runner.add(ec2_stubber.stub_delete_launch_template, template_name)

    if error_code is None:
        scenario.run_scenario(as_wrapper, svc_helper)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario.run_scenario(as_wrapper, svc_helper)
        assert exc_info.value.response['Error']['Code'] == error_code


@pytest.mark.integ
def test_get_launch_template_integ(monkeypatch):
    as_client = boto3.client('autoscaling')
    ec2_client = boto3.client('ec2')
    cw_resource = boto3.resource('cloudwatch')
    as_wrapper = scenario.AutoScalingWrapper(as_client)
    svc_helper = scenario.ServiceHelper(ec2_client, cw_resource)
    template_name = 'doc-test-autoscale-scenario-template'
    group_name = 'doc-test-autoscale-scenario-group'

    inputs = [template_name, template_name, group_name, 1, 'y', '', '', 1, '', 1, 'n',
              '', 'y']
    monkeypatch.setattr('builtins.input', lambda x: inputs.pop(0))

    with patch('builtins.print') as mock_print:
        scenario.run_scenario(as_wrapper, svc_helper)
        mock_print.assert_any_call("\nThanks for watching!")
