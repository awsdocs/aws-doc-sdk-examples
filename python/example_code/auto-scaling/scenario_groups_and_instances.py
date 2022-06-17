# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon EC2 Auto Scaling to
do the following:

1. Create an Amazon Elastic Compute Cloud (Amazon EC2) launch template.
2. Create an EC2 Auto Scaling group configured with a launch template and Availability
   Zones.
3. Get information about the group and running instances.
4. Enable Amazon CloudWatch metrics collection on the group.
5. Update the desired capacity of the group and wait for an instance to start.
6. Terminate an instance in the group.
7. List scaling activities that have occurred in response to user requests and capacity
   changes.
8. Get statistics for CloudWatch metrics that have been collected during the example.
9. Stop collecting metrics, terminate all instances, and delete the group.
"""

from datetime import datetime, timedelta, timezone
import logging
from pprint import pp
import sys
import boto3
from botocore.exceptions import ClientError

from action_wrapper import AutoScalingWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append('../..')
import demo_tools.question as q
from demo_tools.retries import wait

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.auto-scaling.helper.ServiceHelper]
class ServiceHelper:
    """Encapsulates Amazon EC2 and CloudWatch actions for the example."""
    def __init__(self, ec2_client, cloudwatch_resource):
        """
        :param ec2_client: A Boto3 EC2 client.
        :param cloudwatch_resource: A Boto3 CloudWatch resource.
        """
        self.ec2_client = ec2_client
        self.cloudwatch_resource = cloudwatch_resource

    def get_template(self, template_name):
        """
        Gets a launch template. Launch templates specify configuration for instances
        that are launched by EC2 Auto Scaling.

        :param template_name: The name of the template to look up.
        :return: The template, if it exists.
        """
        try:
            response = self.ec2_client.describe_launch_templates(LaunchTemplateNames=[template_name])
            template = response['LaunchTemplates'][0]
        except ClientError as err:
            if err.response['Error']['Code'] == 'InvalidLaunchTemplateName.NotFoundException':
                logger.warning("Launch template %s does not exist.", template_name)
            else:
                logger.error(
                    "Couldn't verify launch template %s. Here's why: %s: %s", template_name,
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise
        else:
            return template

    def create_template(self, template_name, inst_type, ami_id):
        """
        Creates an EC2 launch template to use with EC2 Auto Scaling.

        :param template_name: The name to give the template.
        :param inst_type: The type of the instance, such a t1.micro.
        :param ami_id: The ID of the Amazon Machine Image (AMI) to use when creating
                       an instance.
        :return: Information about the newly created template.
        """
        try:
            response = self.ec2_client.create_launch_template(
                LaunchTemplateName=template_name,
                LaunchTemplateData={
                    'InstanceType': inst_type,
                    'ImageId': ami_id})
            template = response['LaunchTemplate']
        except ClientError as err:
            logger.error(
                "Couldn't create launch template %s. Here's why: %s: %s", template_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return template

    def delete_template(self, template_name):
        """
        Deletes a launch template.

        :param template_name: The name of the template to delete.
        """
        try:
            self.ec2_client.delete_launch_template(LaunchTemplateName=template_name)
        except ClientError as err:
            logger.error(
                "Couldn't delete launch template %s. Here's why: %s: %s", template_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise

    def get_availability_zones(self):
        """
        Gets a list of Availability Zones in the AWS Region of the EC2 client.

        :return: The list of Availability Zones for the client Region.
        """
        try:
            response = self.ec2_client.describe_availability_zones()
            zones = [zone['ZoneName'] for zone in response['AvailabilityZones']]
        except ClientError as err:
            logger.error(
                "Couldn't get availability zones. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return zones

    def get_metrics(self, namespace, dimensions):
        """
        Gets a list of CloudWatch metrics filtered by namespace and dimensions.

        :param namespace: The namespace of the metrics to look up.
        :param dimensions: The dimensions of the metrics to look up.
        :return: The list of metrics.
        """
        try:
            metrics = list(self.cloudwatch_resource.metrics.filter(
                Namespace=namespace, Dimensions=dimensions))
        except ClientError as err:
            logger.error(
                "Couldn't get metrics for %s, %s. Here's why: %s: %s", namespace, dimensions,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return metrics

    @staticmethod
    def get_metric_statistics(dimensions, metric, start, end):
        """
        Gets statistics for a CloudWatch metric within a specified time span.

        :param dimensions: The dimensions of the metric.
        :param metric: The metric to look up.
        :param start: The start of the time span for retrieved metrics.
        :param end: The end of the time span for retrieved metrics.
        :return: The list of data points found for the specified metric.
        """
        try:
            response = metric.get_statistics(
                Dimensions=dimensions, StartTime=start, EndTime=end, Period=60,
                Statistics=['Sum'])
            data = response['Datapoints']
        except ClientError as err:
            logger.error(
                "Couldn't get statistics for metric %s. Here's why: %s: %s", metric.name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return data


def print_simplified_group(group):
    """
    Prints a subset of data for an EC2 Auto Scaling group.
    """
    print(group['AutoScalingGroupName'])
    print(f"\tLaunch template: {group['LaunchTemplate']['LaunchTemplateName']}")
    print(f"\tMin: {group['MinSize']}, Max: {group['MaxSize']}, Desired: {group['DesiredCapacity']}")
    if group['Instances']:
        print(f"\tInstances:")
        for inst in group['Instances']:
            print(f"\t\t{inst['InstanceId']}: {inst['LifecycleState']}")


def wait_for_instances(group_name, as_wrapper):
    """
    Waits for instances to start or stop in an EC2 Auto Scaling group.
    Prints the data for each instance after scaling activities have completed.
    """
    ready = False
    instance_ids = []
    instances = []
    while not ready:
        group = as_wrapper.describe_group(group_name)
        instance_ids = [i['InstanceId'] for i in group['Instances']]
        instances = as_wrapper.describe_instances(instance_ids) if instance_ids else []
        if all([x['LifecycleState'] in ['Terminated', 'InService'] for x in instances]):
            ready = True
        else:
            wait(10)
    if instances:
        print(f"Here are the details of the instance{'s' if len(instances) > 1 else ''}:")
        for instance in instances:
            pp(instance)
    return instance_ids
# snippet-end:[python.example_code.auto-scaling.helper.ServiceHelper]


# snippet-start:[python.example_code.auto-scaling.Scenario_GroupsAndInstances]
def run_scenario(as_wrapper, svc_helper):
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    print('-'*88)
    print("Welcome to the Amazon EC2 Auto Scaling groups and instances demo.")
    print('-'*88)

    print("This example requires a launch template that specifies how to create\n"
          "EC2 instances. You can use an existing one or create a new one.")
    template_name = q.ask(
        "Enter the name of an existing launch template or press Enter to continue: ")
    template = None
    if template_name:
        template = svc_helper.get_template(template_name)
    if template is None:
        inst_type = 't1.micro'
        ami_id = 'ami-0ca285d4c2cda3300'
        print("Let's create a launch template with the following specifications:")
        print(f"\tInstanceType: {inst_type}")
        print(f"\tAMI ID: {ami_id}")
        template_name = q.ask("Enter a name for the template: ", q.non_empty)
        template = svc_helper.create_template(template_name, inst_type, ami_id)
    print('-'*88)

    print("Let's create an Auto Scaling group.")
    group_name = q.ask("Enter a name for the group: ", q.non_empty)
    zones = svc_helper.get_availability_zones()
    print("Amazon EC2 instances can be created in the following Availability Zones:")
    for index, zone in enumerate(zones):
        print(f"\t{index+1}. {zone}")
    print(f"\t{len(zones)+1}. All zones")
    zone_sel = q.ask("Which zone do you want to use? ", q.is_int, q.in_range(1, len(zones)+1))
    group_zones = [zones[zone_sel-1]] if zone_sel <= len(zones) else zones
    print(f"Creating group {group_name}...")
    as_wrapper.create_group(group_name, group_zones, template_name, 1, 1)
    wait(10)
    group = as_wrapper.describe_group(group_name)
    print("Created group:")
    pp(group)
    print("Waiting for instance to start...")
    wait_for_instances(group_name, as_wrapper)
    print('-'*88)

    use_metrics = q.ask(
        "Do you want to collect metrics about Auto Scaling during this demo (y/n)? ", q.is_yesno)
    if use_metrics:
        as_wrapper.enable_metrics(
            group_name, [
                'GroupMinSize', 'GroupMaxSize', 'GroupDesiredCapacity',
                'GroupInServiceInstances', 'GroupTotalInstances'])
        print(f"Metrics enabled for {group_name}.")
    print('-'*88)

    print(f"Let's update the maximum number of instances in {group_name} from 1 to 3.")
    q.ask("Press Enter when you're ready.")
    as_wrapper.update_group(group_name, MaxSize=3)
    group = as_wrapper.describe_group(group_name)
    print("The group still has one running instance, but can have up to three:")
    print_simplified_group(group)
    print('-'*88)

    print(f"Let's update the desired capacity of {group_name} from 1 to 2.")
    q.ask("Press Enter when you're ready.")
    as_wrapper.set_desired_capacity(group_name, 2)
    wait(10)
    group = as_wrapper.describe_group(group_name)
    print("Here's the current state of the group:")
    print_simplified_group(group)
    print('-'*88)
    print("Waiting for the new instance to start...")
    instance_ids = wait_for_instances(group_name, as_wrapper)
    print('-'*88)

    print(f"Let's terminate one of the instances in {group_name}.")
    print("Because the desired capacity is 2, another instance will start.")
    print("The currently running instances are:")
    for index, inst_id in enumerate(instance_ids):
        print(f"\t{index+1}. {inst_id}")
    inst_sel = q.ask(
        "Which instance do you want to stop? ", q.is_int, q.in_range(1, len(instance_ids)+1))
    print(f"Stopping {instance_ids[inst_sel-1]}...")
    as_wrapper.terminate_instance(instance_ids[inst_sel-1], False)
    wait(10)
    group = as_wrapper.describe_group(group_name)
    print(f"Here's the state of {group_name}:")
    print_simplified_group(group)
    print("Waiting for the scaling activities to complete...")
    wait_for_instances(group_name, as_wrapper)
    print('-'*88)

    print(f"Let's get a report of scaling activities for {group_name}.")
    q.ask("Press Enter when you're ready.")
    activities = as_wrapper.describe_scaling_activities(group_name)
    print(f"Found {len(activities)} activities.\n"
          f"Activities are ordered with the most recent one first:")
    for act in activities:
        pp(act)
    print('-'*88)

    if use_metrics:
        print("Let's look at CloudWatch metrics.")
        metric_namespace = 'AWS/AutoScaling'
        metric_dimensions = [{'Name': 'AutoScalingGroupName', 'Value': group_name}]
        print(f"The following metrics are enabled for {group_name}:")
        done = False
        while not done:
            metrics = svc_helper.get_metrics(metric_namespace, metric_dimensions)
            for index, metric in enumerate(metrics):
                print(f"\t{index+1}. {metric.name}")
            print(f"\t{len(metrics)+1}. None")
            metric_sel = q.ask(
                "Which metric do you want to see? ", q.is_int, q.in_range(1, len(metrics)+1))
            if metric_sel < len(metrics)+1:
                span = 5
                metric = metrics[metric_sel - 1]
                print(f"Over the last {span} minutes, {metric.name} recorded:")
                # CloudWatch metric times are in UTC.
                now = datetime.now(timezone.utc)
                metric_data = svc_helper.get_metric_statistics(
                    metric_dimensions, metric, now-timedelta(minutes=span), now)
                pp(metric_data)
                if not q.ask("Do you want to see another one (y/n)? ", q.is_yesno):
                    done = True
            else:
                done = True

    print(f"Let's clean up.")
    q.ask("Press Enter when you're ready.")
    if use_metrics:
        print(f"Stopping metrics collection for {group_name}.")
        as_wrapper.disable_metrics(group_name)

    print("You must terminate all instances in the group before you can delete the group.")
    print("Set minimum size to 0.")
    as_wrapper.update_group(group_name, MinSize=0)
    group = as_wrapper.describe_group(group_name)
    instance_ids = [inst['InstanceId'] for inst in group['Instances']]
    for inst_id in instance_ids:
        print(f"Stopping {inst_id}.")
        as_wrapper.terminate_instance(inst_id, True)
    print("Waiting for instances to stop...")
    wait_for_instances(group_name, as_wrapper)
    print(f"Deleting {group_name}.")
    as_wrapper.delete_group(group_name)
    print('-'*88)

    if template is not None:
        if q.ask(f"Do you want to delete launch template {template_name} used in this demo (y/n)? "):
            svc_helper.delete_template(template_name)
            print("Template deleted.")

    print("\nThanks for watching!")
    print('-'*88)


if __name__ == '__main__':
    try:
        wrapper = AutoScalingWrapper(boto3.client('autoscaling'))
        helper = ServiceHelper(boto3.client('ec2'), boto3.resource('cloudwatch'))
        run_scenario(wrapper, helper)
    except Exception:
        logging.exception("Something went wrong with the demo!")
# snippet-end:[python.example_code.auto-scaling.Scenario_GroupsAndInstances]
