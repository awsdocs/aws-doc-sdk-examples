import logging
from pprint import pp
from typing import Any, Dict, Optional

import boto3
from botocore.exceptions import ClientError, WaiterError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ec2.SecurityGroupWrapper.class]
# snippet-start:[python.example_code.ec2.SecurityGroupWrapper.decl]
class SecurityGroupWrapper:
    """Encapsulates Amazon Elastic Compute Cloud (Amazon EC2) security group actions."""

    def __init__(self, ec2_client: boto3.client, security_group: Optional[str] = None):
        """
        Initializes the SecurityGroupWrapper with an EC2 client and an optional security group ID.

        :param ec2_client: A Boto3 Amazon EC2 client. This client provides low-level
                           access to AWS EC2 services.
        :param security_group: The ID of a security group to manage. This is a high-level identifier
                               that represents the security group.
        """
        self.ec2_client = ec2_client
        self.security_group = security_group

    @classmethod
    def from_client(cls) -> "SecurityGroupWrapper":
        """
        Creates a SecurityGroupWrapper instance with a default EC2 client.

        :return: An instance of SecurityGroupWrapper initialized with the default EC2 client.
        """
        ec2_client = boto3.client("ec2")
        return cls(ec2_client)

    # snippet-end:[python.example_code.ec2.SecurityGroupWrapper.decl]

    # snippet-start:[python.example_code.ec2.CreateSecurityGroup]
    def create(self, group_name: str, group_description: str) -> str:
        """
        Creates a security group in the default virtual private cloud (VPC) of the current account.

        :param group_name: The name of the security group to create.
        :param group_description: The description of the security group to create.
        :return: The ID of the newly created security group.
        """
        try:
            response = self.ec2_client.create_security_group(
                GroupName=group_name, Description=group_description
            )
            self.security_group = response["GroupId"]
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceAlreadyExists":
                logger.error(
                    f"Security group '{group_name}' already exists. Please choose a different name."
                )
            raise
        else:
            return self.security_group
    # snippet-end:[python.example_code.ec2.CreateSecurityGroup]

    # snippet-start:[python.example_code.ec2.AuthorizeSecurityGroupIngress]
    def authorize_ingress(self, ssh_ingress_ip: str) -> Optional[Dict[str, Any]]:
        """
        Adds a rule to the security group to allow access to SSH.

        :param ssh_ingress_ip: The IP address that is granted inbound access to connect
                               to port 22 over TCP, used for SSH.
        :return: The response to the authorization request. The 'Return' field of the
                 response indicates whether the request succeeded or failed, or None if no security group is set.
        """
        if self.security_group is None:
            logger.info("No security group to update.")
            return None

        try:
            ip_permissions = [
                {
                    # SSH ingress open to only the specified IP address.
                    "IpProtocol": "tcp",
                    "FromPort": 22,
                    "ToPort": 22,
                    "IpRanges": [{"CidrIp": f"{ssh_ingress_ip}/32"}],
                }
            ]
            response = self.ec2_client.authorize_security_group_ingress(
                GroupId=self.security_group, IpPermissions=ip_permissions
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidPermission.Duplicate":
                logger.error(f"""
                    The SSH ingress rule for IP {ssh_ingress_ip} already exists in security group '{self.security_group}'."
                """)
            raise
        else:
            return response
    # snippet-end:[python.example_code.ec2.AuthorizeSecurityGroupIngress]

    # snippet-start:[python.example_code.ec2.DescribeSecurityGroups]
    def describe(self) -> None:
        """
        Displays information about the security group.
        """
        if self.security_group is None:
            logger.info("No security group to describe.")
            return

        try:
            paginator = self.ec2_client.get_paginator('describe_security_groups')
            page_iterator = paginator.paginate(GroupIds=[self.security_group])

            for page in page_iterator:
                for security_group in page['SecurityGroups']:
                    print(f"Security group: {security_group['GroupName']}")
                    print(f"\tID: {security_group['GroupId']}")
                    print(f"\tVPC: {security_group['VpcId']}")
                    if security_group["IpPermissions"]:
                        print(f"Inbound permissions:")
                        pp(security_group["IpPermissions"])
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidGroup.NotFound":
                logger.error(f"""
                    Security group {self.security_group} does not exist because specified security group ID was not found.
                """)
            raise
    # snippet-end:[python.example_code.ec2.DescribeSecurityGroups]

    # snippet-start:[python.example_code.ec2.DeleteSecurityGroup]
    def delete(self) -> None:
        """
        Deletes the security group.
        """
        if self.security_group is None:
            logger.info("No security group to delete.")
            return
        try:
            self.ec2_client.delete_security_group(GroupId=self.security_group)
            waiter = self.ec2_client.get_waiter("security_group_not_exists")
            waiter.wait(GroupIds=[self.security_group])
            logger.info(f"Successfully deleted security group '{self.security_group}'")
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidGroup.NotFound":
                logger.error(
                    f"Security group cannot be deleted because it does not exist."
                )
            if err.response["Error"]["Code"] == "DependencyViolation":
                logger.error(
                    "Security group cannot be deleted because it is still in use."
                    "Verify it is:"
                    "\t- Detached from resources"
                    "\t- Removed from references in other groups"
                    "\t- Removed from VPC's as a default group"
                )
            logger.error(f"Deletion failed for security group '{self.security_group}'")
            raise
    # snippet-end:[python.example_code.ec2.DeleteSecurityGroup]
# snippet-end:[python.example_code.ec2.SecurityGroupWrapper.class]
