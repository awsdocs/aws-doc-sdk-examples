# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Elastic Compute Cloud
(Amazon EC2) API to create security resources and instances.
"""

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)
ec2 = boto3.resource('ec2')


# snippet-start:[python.example_code.ec2.CreateKeyPair]
def create_key_pair(key_name, private_key_file_name=None):
    """
    Creates a key pair that can be used to securely connect to an Amazon EC2 instance.

    :param key_name: The name of the key pair to create.
    :param private_key_file_name: The file name where the private key portion of
                                  the newly created key is stored.
    :return: The newly created key pair.
    """
    try:
        key_pair = ec2.create_key_pair(KeyName=key_name)
        logger.info("Created key %s.", key_pair.name)
        if private_key_file_name is not None:
            with open(private_key_file_name, 'w') as pk_file:
                pk_file.write(key_pair.key_material)
            logger.info("Wrote private key to %s.", private_key_file_name)
    except ClientError:
        logger.exception("Couldn't create key %s.", key_name)
        raise
    else:
        return key_pair
# snippet-end:[python.example_code.ec2.CreateKeyPair]


# snippet-start:[python.example_code.ec2.CreateSecurityGroup_AuthorizeIngress]
def setup_security_group(group_name, group_description, ssh_ingress_ip=None):
    """
    Creates a security group in the default virtual private cloud (VPC) of the
    current account, then adds rules to the security group to allow access to
    HTTP, HTTPS and, optionally, SSH.

    :param group_name: The name of the security group to create.
    :param group_description: The description of the security group to create.
    :param ssh_ingress_ip: The IP address that is granted inbound access to connect
                           to port 22 over TCP, used for SSH.
    :return: The newly created security group.
    """
    try:
        default_vpc = list(ec2.vpcs.filter(
            Filters=[{'Name': 'isDefault', 'Values': ['true']}]))[0]
        logger.info("Got default VPC %s.", default_vpc.id)
    except ClientError:
        logger.exception("Couldn't get VPCs.")
        raise
    except IndexError:
        logger.exception("No default VPC in the list.")
        raise

    try:
        security_group = default_vpc.create_security_group(
            GroupName=group_name, Description=group_description)
        logger.info(
            "Created security group %s in VPC %s.", group_name, default_vpc.id)
    except ClientError:
        logger.exception("Couldn't create security group %s.", group_name)
        raise

    try:
        ip_permissions = [{
            # HTTP ingress open to anyone
            'IpProtocol': 'tcp', 'FromPort': 80, 'ToPort': 80,
            'IpRanges': [{'CidrIp': '0.0.0.0/0'}]
        }, {
            # HTTPS ingress open to anyone
            'IpProtocol': 'tcp', 'FromPort': 443, 'ToPort': 443,
            'IpRanges': [{'CidrIp': '0.0.0.0/0'}]
        }]
        if ssh_ingress_ip is not None:
            ip_permissions.append({
                # SSH ingress open to only the specified IP address
                'IpProtocol': 'tcp', 'FromPort': 22, 'ToPort': 22,
                'IpRanges': [{'CidrIp': f'{ssh_ingress_ip}/32'}]})
        security_group.authorize_ingress(IpPermissions=ip_permissions)
        logger.info("Set inbound rules for %s to allow all inbound HTTP and HTTPS "
                    "but only %s for SSH.", security_group.id, ssh_ingress_ip)
    except ClientError:
        logger.exception("Couldnt authorize inbound rules for %s.", group_name)
        raise
    else:
        return security_group
# snippet-end:[python.example_code.ec2.CreateSecurityGroup_AuthorizeIngress]


# snippet-start:[python.example_code.ec2.RunInstances]
def create_instance(
        image_id, instance_type, key_name, security_group_names=None):
    """
    Creates a new Amazon EC2 instance. The instance automatically starts immediately after
    it is created.

    The instance is created in the default VPC of the current account.

    :param image_id: The Amazon Machine Image (AMI) that defines the kind of
                     instance to create. The AMI defines things like the kind of
                     operating system, such as Amazon Linux, and how the instance is
                     stored, such as Elastic Block Storage (EBS).
    :param instance_type: The type of instance to create, such as 't2.micro'.
                          The instance type defines things like the number of CPUs and
                          the amount of memory.
    :param key_name: The name of the key pair that is used to secure connections to
                     the instance.
    :param security_group_names: A list of security groups that are used to grant
                                 access to the instance. When no security groups are
                                 specified, the default security group of the VPC
                                 is used.
    :return: The newly created instance.
    """
    try:
        instance_params = {
            'ImageId': image_id, 'InstanceType': instance_type, 'KeyName': key_name
        }
        if security_group_names is not None:
            instance_params['SecurityGroups'] = security_group_names
        instance = ec2.create_instances(**instance_params, MinCount=1, MaxCount=1)[0]
        logger.info("Created instance %s.", instance.id)
    except ClientError:
        logging.exception(
            "Couldn't create instance with image %s, instance type %s, and key %s.",
            image_id, instance_type, key_name)
        raise
    else:
        return instance
# snippet-end:[python.example_code.ec2.RunInstances]
