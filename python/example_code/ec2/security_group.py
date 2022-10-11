# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
from pprint import pp
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ec2.SecurityGroupWrapper.class]
# snippet-start:[python.example_code.ec2.SecurityGroupWrapper.decl]
class SecurityGroupWrapper:
    """Encapsulates Amazon Elastic Compute Cloud (Amazon EC2) security group actions."""
    def __init__(self, ec2_resource, security_group=None):
        """
        :param ec2_resource: A Boto3 Amazon EC2 resource. This high-level resource
                             is used to create additional high-level objects
                             that wrap low-level Amazon EC2 service actions.
        :param security_group: A Boto3 SecurityGroup object. This is a high-level object
                               that wraps security group actions.
        """
        self.ec2_resource = ec2_resource
        self.security_group = security_group

    @classmethod
    def from_resource(cls):
        ec2_resource = boto3.resource('ec2')
        return cls(ec2_resource)
# snippet-end:[python.example_code.ec2.SecurityGroupWrapper.decl]

    # snippet-start:[python.example_code.ec2.CreateSecurityGroup]
    def create(self, group_name, group_description):
        """
        Creates a security group in the default virtual private cloud (VPC) of the
        current account.

        :param group_name: The name of the security group to create.
        :param group_description: The description of the security group to create.
        :return: A Boto3 SecurityGroup object that represents the newly created security group.
        """
        try:
            self.security_group = self.ec2_resource.create_security_group(
                GroupName=group_name, Description=group_description)
        except ClientError as err:
            logger.error(
                "Couldn't create security group %s. Here's why: %s: %s", group_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return self.security_group
    # snippet-end:[python.example_code.ec2.CreateSecurityGroup]

    # snippet-start:[python.example_code.ec2.AuthorizeSecurityGroupIngress]
    def authorize_ingress(self, ssh_ingress_ip):
        """
        Adds rules to the security group to allow access to HTTP, HTTPS and, SSH.

        :param ssh_ingress_ip: The IP address that is granted inbound access to connect
                               to port 22 over TCP, used for SSH.
        :return: The response to the authorization request. The 'Return' field of the
                 response indicates whether the request succeeded or failed.
        """
        if self.security_group is None:
            logger.info("No security group to update.")
            return

        try:
            ip_permissions = [{
                # HTTP ingress open to anyone
                'IpProtocol': 'tcp', 'FromPort': 80, 'ToPort': 80,
                'IpRanges': [{'CidrIp': '0.0.0.0/0'}]
            }, {

                # HTTPS ingress open to anyone
                'IpProtocol': 'tcp', 'FromPort': 443, 'ToPort': 443,
                'IpRanges': [{'CidrIp': '0.0.0.0/0'}]
            }, {
                # SSH ingress open to only the specified IP address
                'IpProtocol': 'tcp', 'FromPort': 22, 'ToPort': 22,
                'IpRanges': [{'CidrIp': f'{ssh_ingress_ip}/32'}]}]
            response = self.security_group.authorize_ingress(IpPermissions=ip_permissions)
        except ClientError as err:
            logger.error(
                "Couldn't authorize inbound rules for %s. Here's why: %s: %s",
                self.security_group.id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response
    # snippet-end:[python.example_code.ec2.AuthorizeSecurityGroupIngress]

    # snippet-start:[python.example_code.ec2.DescribeSecurityGroups]
    def describe(self):
        """
        Displays information about the security group.
        """
        if self.security_group is None:
            logger.info("No security group to describe.")
            return

        try:
            print(f"Security group: {self.security_group.group_name}")
            print(f"\tID: {self.security_group.id}")
            print(f"\tVPC: {self.security_group.vpc_id}")
            if self.security_group.ip_permissions:
                print(f"Inbound permissions:")
                pp(self.security_group.ip_permissions)
        except ClientError as err:
            logger.error(
                "Couldn't get data for security group %s. Here's why: %s: %s", self.security_group.id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.ec2.DescribeSecurityGroups]

    # snippet-start:[python.example_code.ec2.DeleteSecurityGroup]
    def delete(self):
        """
        Deletes the security group.
        """
        if self.security_group is None:
            logger.info("No security group to delete.")
            return

        group_id = self.security_group.id
        try:
            self.security_group.delete()
        except ClientError as err:
            logger.error(
                "Couldn't delete security group %s. Here's why: %s: %s", group_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.ec2.DeleteSecurityGroup]
# snippet-end:[python.example_code.ec2.SecurityGroupWrapper.class]
