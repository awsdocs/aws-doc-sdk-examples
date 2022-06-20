# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon EC2 Auto Scaling to
manage groups and instances.
"""

import logging

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.auto-scaling.AutoScalingWrapper.decl]
class AutoScalingWrapper:
    """Encapsulates Amazon EC2 Auto Scaling actions."""
    def __init__(self, autoscaling_client):
        """
        :param autoscaling_client: A Boto3 Amazon EC2 Auto Scaling client.
        """
        self.autoscaling_client = autoscaling_client
# snippet-end:[python.example_code.auto-scaling.AutoScalingWrapper.decl]

    # snippet-start:[python.example_code.auto-scaling.CreateAutoScalingGroup]
    def create_group(
            self, group_name, group_zones, launch_template_name, min_size, max_size):
        """
        Creates an Auto Scaling group.

        :param group_name: The name to give to the group.
        :param group_zones: The Availability Zones in which instances can be created.
        :param launch_template_name: The name of an existing Amazon EC2 launch template.
                                     The launch template specifies the configuration of
                                     instances that are created by auto scaling activities.
        :param min_size: The minimum number of active instances in the group.
        :param max_size: The maximum number of active instances in the group.
        """
        try:
            self.autoscaling_client.create_auto_scaling_group(
                AutoScalingGroupName=group_name,
                AvailabilityZones=group_zones,
                LaunchTemplate={
                    'LaunchTemplateName': launch_template_name, 'Version': '$Default'},
                MinSize=min_size, MaxSize=max_size
            )
        except ClientError as err:
            logger.error(
                "Couldn't create group %s. Here's why: %s: %s", group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.auto-scaling.CreateAutoScalingGroup]

    # snippet-start:[python.example_code.auto-scaling.UpdateAutoScalingGroup]
    def update_group(self, group_name, **kwargs):
        """
        Updates an Auto Scaling group.

        :param group_name: The name of the group to update.
        :param kwargs: Keyword arguments to pass through to the service.
        """
        try:
            self.autoscaling_client.update_auto_scaling_group(
                AutoScalingGroupName=group_name, **kwargs)
        except ClientError as err:
            logger.error(
                "Couldn't update group %s. Here's why: %s: %s", group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.auto-scaling.UpdateAutoScalingGroup]

    # snippet-start:[python.example_code.auto-scaling.DeleteAutoScalingGroup]
    def delete_group(self, group_name):
        """
        Deletes an Auto Scaling group. All instances must be stopped before the
        group can be deleted.

        :param group_name: The name of the group to delete.
        """
        try:
            self.autoscaling_client.delete_auto_scaling_group(
                AutoScalingGroupName=group_name)
        except ClientError as err:
            logger.error(
                "Couldn't delete group %s. Here's why: %s: %s", group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.auto-scaling.DeleteAutoScalingGroup]

    # snippet-start:[python.example_code.auto-scaling.DescribeAutoScalingGroups]
    def describe_group(self, group_name):
        """
        Gets information about an Auto Scaling group.

        :param group_name: The name of the group to look up.
        :return: Information about the group, if found.
        """
        try:
            response = self.autoscaling_client.describe_auto_scaling_groups(
                AutoScalingGroupNames=[group_name])
        except ClientError as err:
            logger.error(
                "Couldn't describe group %s. Here's why: %s: %s", group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            groups = response.get('AutoScalingGroups', [])
            return groups[0] if len(groups) > 0 else None
    # snippet-end:[python.example_code.auto-scaling.DescribeAutoScalingGroups]

    # snippet-start:[python.example_code.auto-scaling.TerminateInstanceInAutoScalingGroup]
    def terminate_instance(self, instance_id, decrease_capacity):
        """
        Stops an instance.

        :param instance_id: The ID of the instance to stop.
        :param decrease_capacity: Specifies whether to decrease the desired capacity
                                  of the group. When passing True for this parameter,
                                  you can stop an instance without having a replacement
                                  instance start when the desired capacity threshold is
                                  crossed.
        :return: The scaling activity that occurs in response to this action.
        """
        try:
            response = self.autoscaling_client.terminate_instance_in_auto_scaling_group(
                InstanceId=instance_id, ShouldDecrementDesiredCapacity=decrease_capacity)
        except ClientError as err:
            logger.error(
                "Couldn't terminate instance %s. Here's why: %s: %s", instance_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response['Activity']
    # snippet-end:[python.example_code.auto-scaling.TerminateInstanceInAutoScalingGroup]

    # snippet-start:[python.example_code.auto-scaling.SetDesiredCapacity]
    def set_desired_capacity(self, group_name, capacity):
        """
        Sets the desired capacity of the group. Amazon EC2 Auto Scaling tries to keep the
        number of running instances equal to the desired capacity.

        :param group_name: The name of the group to update.
        :param capacity: The desired number of running instances.
        """
        try:
            self.autoscaling_client.set_desired_capacity(
                AutoScalingGroupName=group_name, DesiredCapacity=capacity, HonorCooldown=False)
        except ClientError as err:
            logger.error(
                "Couldn't set desired capacity %s. Here's why: %s: %s", group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.auto-scaling.SetDesiredCapacity]

    # snippet-start:[python.example_code.auto-scaling.DescribeAutoScalingInstances]
    def describe_instances(self, instance_ids):
        """
        Gets information about instances.

        :param instance_ids: A list of instance IDs to look up.
        :return: Information about instances, or an empty list if none are found.
        """
        try:
            response = self.autoscaling_client.describe_auto_scaling_instances(
                InstanceIds=instance_ids)
        except ClientError as err:
            logger.error(
                "Couldn't describe instances %s. Here's why: %s: %s", instance_ids,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response['AutoScalingInstances']
    # snippet-end:[python.example_code.auto-scaling.DescribeAutoScalingInstances]

    # snippet-start:[python.example_code.auto-scaling.DescribeScalingActivities]
    def describe_scaling_activities(self, group_name):
        """
        Gets information about scaling activities for the group. Scaling activities
        are things like instances stopping or starting in response to user requests
        or capacity changes.

        :param group_name: The name of the group to look up.
        :return: The list of scaling activities for the group, ordered with the most
                 recent activity first.
        """
        try:
            response = self.autoscaling_client.describe_scaling_activities(
                AutoScalingGroupName=group_name)
        except ClientError as err:
            logger.error(
                "Couldn't describe scaling activities %s. Here's why: %s: %s", group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response['Activities']
    # snippet-end:[python.example_code.auto-scaling.DescribeScalingActivities]

    # snippet-start:[python.example_code.auto-scaling.EnableMetricsCollection]
    def enable_metrics(self, group_name, metrics):
        """
        Enables CloudWatch metric collection for Amazon EC2 Auto Scaling activities.

        :param group_name: The name of the group to enable.
        :param metrics: A list of metrics to collect.
        """
        try:
            self.autoscaling_client.enable_metrics_collection(
                AutoScalingGroupName=group_name, Metrics=metrics, Granularity='1Minute')
        except ClientError as err:
            logger.error(
                "Couldn't enable metrics on %s. Here's why: %s: %s", group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.auto-scaling.EnableMetricsCollection]

    # snippet-start:[python.example_code.auto-scaling.DisableMetricsCollection]
    def disable_metrics(self, group_name):
        """
        Stops CloudWatch metric collection for the Auto Scaling group.

        :param group_name: The name of the group.
        """
        try:
            self.autoscaling_client.disable_metrics_collection(
                AutoScalingGroupName=group_name)
        except ClientError as err:
            logger.error(
                "Couldn't disable metrics %s. Here's why: %s: %s", group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.auto-scaling.DisableMetricsCollection]
