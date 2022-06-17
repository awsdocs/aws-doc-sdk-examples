# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon EC2 Auto Scaling unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class AutoScalingStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon EC2 Auto Scaling unit tests.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 EC2 Auto Scaling client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_auto_scaling_group(
            self, group_name, group_zones, launch_template_name, min_size, max_size, error_code=None):
        expected_params = {
            'AutoScalingGroupName': group_name,
            'AvailabilityZones': group_zones,
            'LaunchTemplate':
                {'LaunchTemplateName': launch_template_name, 'Version': '$Default'},
            'MinSize': min_size, 'MaxSize': max_size}
        response = {}
        self._stub_bifurcator(
            'create_auto_scaling_group', expected_params, response, error_code=error_code)

    def stub_update_auto_scaling_group(self, group_name, min_size=None, max_size=None, error_code=None):
        expected_params = {'AutoScalingGroupName': group_name}
        if min_size is not None:
            expected_params['MinSize'] = min_size
        if max_size is not None:
            expected_params['MaxSize'] = max_size
        response = {}
        self._stub_bifurcator(
            'update_auto_scaling_group', expected_params, response, error_code=error_code)

    def stub_delete_auto_scaling_group(self, group_name, error_code=None):
        expected_params = {'AutoScalingGroupName': group_name}
        response = {}
        self._stub_bifurcator(
            'delete_auto_scaling_group', expected_params, response, error_code=error_code)

    def stub_describe_auto_scaling_groups(self, group_names, groups, error_code=None):
        expected_params = {'AutoScalingGroupNames': group_names}
        response = {'AutoScalingGroups': groups}
        self._stub_bifurcator(
            'describe_auto_scaling_groups', expected_params, response, error_code=error_code)

    def stub_terminate_instance_in_auto_scaling_group(self, instance_id, decrement, activity, error_code=None):
        expected_params = {'InstanceId': instance_id, 'ShouldDecrementDesiredCapacity': decrement}
        response = {'Activity': activity}
        self._stub_bifurcator(
            'terminate_instance_in_auto_scaling_group', expected_params, response, error_code=error_code)

    def stub_set_desired_capacity(self, group_name, capacity, error_code=None):
        expected_params = {
            'AutoScalingGroupName': group_name, 'DesiredCapacity': capacity,
            'HonorCooldown': False}
        response = {}
        self._stub_bifurcator(
            'set_desired_capacity', expected_params, response, error_code=error_code)

    def stub_describe_auto_scaling_instances(self, instance_ids, instances, error_code=None):
        expected_params = {'InstanceIds': instance_ids}
        response = {'AutoScalingInstances': instances}
        self._stub_bifurcator(
            'describe_auto_scaling_instances', expected_params, response, error_code=error_code)

    def stub_describe_scaling_activities(self, group_name, activities, error_code=None):
        expected_params = {'AutoScalingGroupName': group_name}
        response = {'Activities': activities}
        self._stub_bifurcator(
            'describe_scaling_activities', expected_params, response, error_code=error_code)

    def stub_enable_metrics_collection(self, group_name, metrics, error_code=None):
        expected_params = {
            'AutoScalingGroupName': group_name, 'Metrics': metrics, 'Granularity': '1Minute'}
        response = {}
        self._stub_bifurcator(
            'enable_metrics_collection', expected_params, response, error_code=error_code)

    def stub_disable_metrics_collection(self, group_name, error_code=None):
        expected_params = {'AutoScalingGroupName': group_name}
        response = {}
        self._stub_bifurcator(
            'disable_metrics_collection', expected_params, response, error_code=error_code)
