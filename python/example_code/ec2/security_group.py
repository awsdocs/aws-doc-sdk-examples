import logging
from pprint import pp
import boto3
from botocore.exceptions import ClientError, WaiterError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ec2.SecurityGroupWrapper.class]
# snippet-start:[python.example_code.ec2.SecurityGroupWrapper.decl]
class SecurityGroupWrapper:
    """Encapsulates Amazon Elastic Compute Cloud (Amazon EC2) security group actions."""

    def __init__(self, ec2_client, security_group=None):
        """
        :param ec2_client: A Boto3 Amazon EC2 client. This client provides low-level
                           access to AWS EC2 services.
        :param security_group: A Boto3 SecurityGroup object. This is a high-level object
                               that wraps security group actions.
        """
        self.ec2_client = ec2_client
        self.security_group = security_group

    @classmethod
    def from_client(cls):
        ec2_client = boto3.client("ec2")
        return cls(ec2_client)

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
            response = self.ec2_client.create_security_group(
                GroupName=group_name, Description=group_description
            )
            self.security_group = response["GroupId"]
        except ClientError as err:
            # Improved error handling to catch specific errors like DryRunOperation and
            # ResourceAlreadyExists, providing more informative error messages for these cases.
            if err.response["Error"]["Code"] == "DryRunOperation":
                logger.info("You have permission to perform the create_security_group operation.")
            elif err.response["Error"]["Code"] == "ResourceAlreadyExists":
                logger.error(
                    "Security group %s already exists. Please choose a different name.",
                    group_name,
                )
            else:
                logger.error(
                    "Couldn't create security group %s. Here's why: %s: %s",
                    group_name,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise
        else:
            return self.security_group

    # snippet-end:[python.example_code.ec2.CreateSecurityGroup]

    # snippet-start:[python.example_code.ec2.AuthorizeSecurityGroupIngress]
    def authorize_ingress(self, ssh_ingress_ip):
        """
        Adds a rule to the security group to allow access to SSH.

        :param ssh_ingress_ip: The IP address that is granted inbound access to connect
                               to port 22 over TCP, used for SSH.
        :return: The response to the authorization request. The 'Return' field of the
                 response indicates whether the request succeeded or failed.
        """
        if self.security_group is None:
            logger.info("No security group to update.")
            return

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
            # Improved error handling to catch the specific error InvalidPermission.Duplicate,
            # providing a more informative error message for this case.
            if err.response["Error"]["Code"] == "InvalidPermission.Duplicate":
                logger.error(
                    "The ingress rule for SSH access from %s is already authorized for security group %s.",
                    ssh_ingress_ip,
                    self.security_group,
                )
            else:
                logger.error(
                    "Couldn't authorize inbound rules for %s. Here's why: %s: %s",
                    self.security_group,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
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
            response = self.ec2_client.describe_security_groups(GroupIds=[self.security_group])
            security_group = response["SecurityGroups"][0]
            print(f"Security group: {security_group['GroupName']}")
            print(f"\tID: {security_group['GroupId']}")
            print(f"\tVPC: {security_group['VpcId']}")
            if security_group["IpPermissions"]:
                print(f"Inbound permissions:")
                pp(security_group["IpPermissions"])
        except ClientError as err:
            logger.error(
                "Couldn't get data for security group %s. Here's why: %s: %s",
                self.security_group,
                err.response["Error"]["Code"],
                err.response["Error"]["Message"],
            )
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

        try:
            # Use a waiter to ensure the security group is in a deletable state before attempting deletion.
            waiter = self.ec2_client.get_waiter("security_group_exists")
            waiter.wait(GroupIds=[self.security_group])
            self.ec2_client.delete_security_group(GroupId=self.security_group)
        except WaiterError as err:
            logger.error(
                "Couldn't delete security group %s. The security group may still be in use.",
                self.security_group,
            )
            raise
        except ClientError as err:
            # Improved error handling to catch the specific error InvalidGroup.NotFound,
            # providing a more informative error message for this case.
            if err.response["Error"]["Code"] == "InvalidGroup.NotFound":
                logger.error(
                    "Couldn't delete security group %s. The security group does not exist.",
                    self.security_group,
                )
            else:
                logger.error(
                    "Couldn't delete security group %s. Here's why: %s: %s",
                    self.security_group,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.ec2.DeleteSecurityGroup]


# snippet-end:[python.example_code.ec2.SecurityGroupWrapper.class]