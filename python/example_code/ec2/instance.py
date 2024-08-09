import logging
import boto3
from botocore.exceptions import ClientError
from typing import List, Optional

logger = logging.getLogger(__name__)

# snippet-start:[python.example_code.ec2.InstanceWrapper.class]
# snippet-start:[python.example_code.ec2.InstanceWrapper.decl]
class InstanceWrapper:
    """Encapsulates Amazon Elastic Compute Cloud (Amazon EC2) instance actions using the client interface."""

    def __init__(self, ec2_client, instance=None):
        """
        :param ec2_client: A Boto3 Amazon EC2 client. This client provides low-level
                           access to AWS EC2 services.
        :param instance: A Boto3 Instance object. This is a high-level object that
                           wraps instance actions.
        """
        self.ec2_client = boto3.client("ec2")
        self.instance = instance
        # snippet-end:[python.example_code.ec2.InstanceWrapper.decl]

    # snippet-start:[python.example_code.ec2.RunInstances]
    def create(self, image, instance_type, key_pair, security_groups=None):
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
                "ImageId": image.id,
                "InstanceType": instance_type,
                "KeyName": key_pair.name,
            }
            if security_groups is not None:
                instance_params["SecurityGroupIds"] = [sg.id for sg in security_groups]
            response = self.ec2_client.run_instances(**instance_params, MinCount=1, MaxCount=1)
            self.instance = response["Instances"][0]
            waiter = self.ec2_client.get_waiter("instance_running")
            waiter.wait(InstanceIds=[self.instance["InstanceId"]])
        except ClientError as err:
            logger.error(
                "Couldn't create instance with image %s, instance type %s, and key %s. Here's why: %s: %s",
                image.id,
                instance_type,
                key_pair.name,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise
        else:
            return self.instance

    def display(self, indent=1):
        """
        Displays information about an instance.

        :param indent: The visual indent to apply to the output.
        """
        if self.instance is None:
            logger.info("No instance to display.")
            return

        try:
            response = self.ec2_client.describe_instances(InstanceIds=[self.instance["InstanceId"]])
            instance = response["Reservations"][0]["Instances"][0]
            ind = "\t" * indent
            print(f"{ind}ID: {instance['InstanceId']}")
            print(f"{ind}Image ID: {instance['ImageId']}")
            print(f"{ind}Instance type: {instance['InstanceType']}")
            print(f"{ind}Key name: {instance['KeyName']}")
            print(f"{ind}VPC ID: {instance['VpcId']}")
            print(f"{ind}Public IP: {instance['PublicIpAddress']}")
            print(f"{ind}State: {instance['State']['Name']}")
        except ClientError as err:
            logger.error(
                "Couldn't display instance %s. Here's why: %s: %s",
                self.instance["InstanceId"],
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    def terminate(self):
        """
        Terminates an instance and waits for it to be in a terminated state.
        """
        if self.instance is None:
            logger.info("No instance to terminate.")
            return

        instance_id = self.instance["InstanceId"]
        try:
            self.ec2_client.terminate_instances(InstanceIds=[instance_id])
            waiter = self.ec2_client.get_waiter("instance_terminated")
            waiter.wait(InstanceIds=[instance_id])
            self.instance = None
        except ClientError as err:
            logger.error(
                "Couldn't terminate instance %s. Here's why: %s: %s",
                instance_id,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    def start(self):
        """
        Starts an instance and waits for it to be in a running state.

        :return: The response to the start request.
        """
        if self.instance is None:
            logger.info("No instance to start.")
            return

        try:
            response = self.ec2_client.start_instances(InstanceIds=[self.instance["InstanceId"]])
            waiter = self.ec2_client.get_waiter("instance_running")
            waiter.wait(InstanceIds=[self.instance["InstanceId"]])
        except ClientError as err:
            logger.error(
                "Couldn't start instance %s. Here's why: %s: %s",
                self.instance["InstanceId"],
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise
        else:
            return response

    def stop(self):
        """
        Stops an instance and waits for it to be in a stopped state.

        :return: The response to the stop request.
        """
        if self.instance is None:
            logger.info("No instance to stop.")
            return

        try:
            response = self.ec2_client.stop_instances(InstanceIds=[self.instance["InstanceId"]])
            waiter = self.ec2_client.get_waiter("instance_stopped")
            waiter.wait(InstanceIds=[self.instance["InstanceId"]])
        except ClientError as err:
            logger.error(
                "Couldn't stop instance %s. Here's why: %s: %s",
                self.instance["InstanceId"],
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise
        else:
            return response

    def get_images(self, image_ids: List[str]) -> List[dict]:
        """
        Gets information about Amazon Machine Images (AMIs) from a list of AMI IDs.

        :param image_ids: The list of AMIs to look up.
        :return: A list of dictionaries representing the requested AMIs.
        """
        try:
            response = self.ec2_client.describe_images(ImageIds=image_ids)
            return response["Images"]
        except ClientError as err:
            logger.error(
                "Couldn't get information about the requested AMIs. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise

    def get_instance_types(self, architecture: str) -> List[dict]:
        """
        Gets instance types that support the specified architecture and are designated
        as either 'micro' or 'small'.

        :param architecture: The architecture the instance types must support.
        :return: A list of instance types that support the specified architecture and are either 'micro' or 'small'.
        """
        try:
            inst_types = []
            paginator = self.ec2_client.get_paginator("describe_instance_types")
            for page in paginator.paginate(
                Filters=[
                    {"Name": "processor-info.supported-architecture", "Values": [architecture]},
                    {"Name": "instance-type", "Values": ["*.micro", "*.small"]},
                ]
            ):
                inst_types += page["InstanceTypes"]
            return inst_types
        except ClientError as err:
            logger.error(
                "Couldn't get instance types. Here's why: %s: %s",
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
            raise
