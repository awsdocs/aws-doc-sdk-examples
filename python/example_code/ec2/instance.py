# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ec2.InstanceWrapper.class]
# snippet-start:[python.example_code.ec2.InstanceWrapper.decl]
class InstanceWrapper:
    """Encapsulates Amazon Elastic Compute Cloud (Amazon EC2) instance actions."""
    def __init__(self, ec2_resource, instance=None):
        """
        :param ec2_resource: A Boto3 Amazon EC2 resource. This high-level resource
                             is used to create additional high-level objects
                             that wrap low-level Amazon EC2 service actions.
        :param instance: A Boto3 Instance object. This is a high-level object that
                           wraps instance actions.
        """
        self.ec2_resource = ec2_resource
        self.instance = instance

    @classmethod
    def from_resource(cls):
        ec2_resource = boto3.resource('ec2')
        return cls(ec2_resource)
# snippet-end:[python.example_code.ec2.InstanceWrapper.decl]

    # snippet-start:[python.example_code.ec2.RunInstances]
    def create(
            self, image, instance_type, key_pair, security_groups=None):
        """
        Creates a new EC2 instance. The instance starts immediately after
        it is created.

        The instance is created in the default VPC of the current account.

        :param image: A Boto3 Image object that represents an Amazon Machine Image (AMI)
                      that defines attributes of the instance that is created. The AMI
                      defines things like the kind of operating system and the type of
                      storage used by the instance.
        :param instance_type: The type of instance to create, such as 't2.micro'.
                              The instance type defines things like the number of CPUs and
                              the amount of memory.
        :param key_pair: A Boto3 KeyPair or KeyPairInfo object that represents the key
                         pair that is used to secure connections to the instance.
        :param security_groups: A list of Boto3 SecurityGroup objects that represents the
                                security groups that are used to grant access to the
                                instance. When no security groups are specified, the
                                default security group of the VPC is used.
        :return: A Boto3 Instance object that represents the newly created instance.
        """
        try:
            instance_params = {
                'ImageId': image.id, 'InstanceType': instance_type, 'KeyName': key_pair.name
            }
            if security_groups is not None:
                instance_params['SecurityGroupIds'] = [sg.id for sg in security_groups]
            self.instance = self.ec2_resource.create_instances(**instance_params, MinCount=1, MaxCount=1)[0]
            self.instance.wait_until_running()
        except ClientError as err:
            logging.error(
                "Couldn't create instance with image %s, instance type %s, and key %s. "
                "Here's why: %s: %s", image.id, instance_type, key_pair.name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return self.instance
    # snippet-end:[python.example_code.ec2.RunInstances]

    # snippet-start:[python.example_code.ec2.DescribeInstances]
    def display(self, indent=1):
        """
        Displays information about an instance.

        :param indent: The visual indent to apply to the output.
        """
        if self.instance is None:
            logger.info("No instance to display.")
            return

        try:
            self.instance.load()
            ind = '\t'*indent
            print(f"{ind}ID: {self.instance.id}")
            print(f"{ind}Image ID: {self.instance.image_id}")
            print(f"{ind}Instance type: {self.instance.instance_type}")
            print(f"{ind}Key name: {self.instance.key_name}")
            print(f"{ind}VPC ID: {self.instance.vpc_id}")
            print(f"{ind}Public IP: {self.instance.public_ip_address}")
            print(f"{ind}State: {self.instance.state['Name']}")
        except ClientError as err:
            logger.error(
                "Couldn't display your instance. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.ec2.DescribeInstances]

    # snippet-start:[python.example_code.ec2.TerminateInstances]
    def terminate(self):
        """
        Terminates an instance and waits for it to be in a terminated state.
        """
        if self.instance is None:
            logger.info("No instance to terminate.")
            return

        instance_id = self.instance.id
        try:
            self.instance.terminate()
            self.instance.wait_until_terminated()
            self.instance = None
        except ClientError as err:
            logging.error(
                "Couldn't terminate instance %s. Here's why: %s: %s", instance_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.ec2.TerminateInstances]

    # snippet-start:[python.example_code.ec2.StartInstances]
    def start(self):
        """
        Starts an instance and waits for it to be in a running state.

        :return: The response to the start request.
        """
        if self.instance is None:
            logger.info("No instance to start.")
            return

        try:
            response = self.instance.start()
            self.instance.wait_until_running()
        except ClientError as err:
            logger.error(
                "Couldn't start instance %s. Here's why: %s: %s", self.instance.id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response
    # snippet-end:[python.example_code.ec2.StartInstances]

    # snippet-start:[python.example_code.ec2.StopInstances]
    def stop(self):
        """
        Stops an instance and waits for it to be in a stopped state.

        :return: The response to the stop request.
        """
        if self.instance is None:
            logger.info("No instance to stop.")
            return

        try:
            response = self.instance.stop()
            self.instance.wait_until_stopped()
        except ClientError as err:
            logger.error(
                "Couldn't stop instance %s. Here's why: %s: %s", self.instance.id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response
    # snippet-end:[python.example_code.ec2.StopInstances]

    # snippet-start:[python.example_code.ec2.DescribeImages]
    def get_images(self, image_ids):
        """
        Gets information about Amazon Machine Images (AMIs) from a list of AMI IDs.

        :param image_ids: The list of AMIs to look up.
        :return: A list of Boto3 Image objects that represent the requested AMIs.
        """
        try:
            images = list(self.ec2_resource.images.filter(ImageIds=image_ids))
        except ClientError as err:
            logger.error(
                "Couldn't get images. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return images
    # snippet-end:[python.example_code.ec2.DescribeImages]

    # snippet-start:[python.example_code.ec2.DescribeInstanceTypes]
    def get_instance_types(self, architecture):
        """
        Gets instance types that support the specified architecture and are designated
        as either 'micro' or 'small'. When an instance is created, the instance type
        you specify must support the architecture of the AMI you use.

        :param architecture: The kind of architecture the instance types must support,
                             such as 'x86_64'.
        :return: A list of instance types that support the specified architecture
                 and are either 'micro' or 'small'.
        """
        try:
            inst_types = []
            it_paginator = self.ec2_resource.meta.client.get_paginator('describe_instance_types')
            for page in it_paginator.paginate(
                    Filters=[{
                        'Name': 'processor-info.supported-architecture', 'Values': [architecture]},
                        {'Name': 'instance-type', 'Values': ['*.micro', '*.small']}]):
                inst_types += page['InstanceTypes']
        except ClientError as err:
            logger.error(
                "Couldn't get instance types. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return inst_types
    # snippet-end:[python.example_code.ec2.DescribeInstanceTypes]
# snippet-end:[python.example_code.ec2.InstanceWrapper.class]
