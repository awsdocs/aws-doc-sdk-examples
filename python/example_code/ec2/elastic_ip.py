import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ec2.ElasticIpWrapper.class]
# snippet-start:[python.example_code.ec2.ElasticIpWrapper.decl]
class ElasticIpWrapper:
    """Encapsulates Amazon Elastic Compute Cloud (Amazon EC2) Elastic IP address actions using the client interface."""

    def __init__(self, ec2_client, allocation_id=None):
        """
        :param ec2_client: A Boto3 Amazon EC2 client. This client provides low-level
                           access to AWS EC2 services.
        :param allocation_id: The allocation ID of an Elastic IP address.
        """
        self.ec2_client = ec2_client
        self.allocation_id = allocation_id

    @classmethod
    def from_client(cls):
        ec2_client = boto3.client("ec2")
        return cls(ec2_client)

    # snippet-end:[python.example_code.ec2.ElasticIpWrapper.decl]

    # snippet-start:[python.example_code.ec2.AllocateAddress]
    def allocate(self):
        """
        Allocates an Elastic IP address that can be associated with an Amazon EC2
        instance. By using an Elastic IP address, you can keep the public IP address
        constant even when you restart the associated instance.

        :return: The allocation ID of the newly created Elastic IP address.
        """
        try:
            response = self.ec2_client.allocate_address(Domain="vpc")
            self.allocation_id = response["AllocationId"]
        except ClientError as err:
            if err.response["Error"]["Code"] in "AddressLimitExceeded":
                logger.error(
                    "Max IP's reached. Release unused addresses or contact AWS Support for an increase."
                )
            raise err
        else:
            return self.allocation_id

    # snippet-end:[python.example_code.ec2.AllocateAddress]

    # snippet-start:[python.example_code.ec2.AssociateAddress]
    def associate(self, instance_id):
        """
        Associates an Elastic IP address with an instance. When this association is
        created, the Elastic IP's public IP address is immediately used as the public
        IP address of the associated instance.

        :param instance_id: The ID of the Amazon EC2 instance.
        :return: A response that contains the ID of the association.
        """
        if self.allocation_id is None:
            logger.info("No Elastic IP to associate.")
            return

        try:
            response = self.ec2_client.associate_address(
                AllocationId=self.allocation_id, InstanceId=instance_id
            )
        except ClientError as err:
            if err.response["Error"]["Code"] in "InvalidInstanceID.NotFound":
                logger.error(
                    f"""
                    Failed to associate Elastic IP {self.allocation_id} with {instance_id}
                    because the specified instance ID does not exist or has not propagated fully.
                    Verify the instance ID and try again, or wait a few moments
                    before attempting to associate the Elastic IP address.
                """
                )
                raise
        return response

    # snippet-end:[python.example_code.ec2.AssociateAddress]

    # snippet-start:[python.example_code.ec2.DisassociateAddress]
    def disassociate(self, association_id):
        """
        Removes an association between an Elastic IP address and an instance. When the
        association is removed, the instance is assigned a new public IP address.

        :param association_id: The ID of the association to disassociate.
        """
        try:
            self.ec2_client.disassociate_address(AssociationId=association_id)
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidAssociationID.NotFound":
                logger.error(
                    """
                    Failed to disassociate Elastic IP {self.allocation_id} with {instance_id}
                    because the specified association ID for the Elastic IP address was not found.
                    Verify the association ID and ensure the Elastic IP is currently
                    associated with a resource before attempting to dissociate it.
                """
                )
            raise

    # snippet-end:[python.example_code.ec2.DisassociateAddress]

    # snippet-start:[python.example_code.ec2.ReleaseAddress]
    def release(self):
        """
        Releases an Elastic IP address. After the Elastic IP address is released,
        it can no longer be used.
        """
        if self.allocation_id is None:
            logger.info("No Elastic IP to release.")
            return

        try:
            self.ec2_client.release_address(AllocationId=self.allocation_id)
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidAddress.NotFound":
                logger.error(
                    f"""
                    Failed to release Elastic IP address {self.allocation_id}
                    because it could not be found. Verify the Elastic IP address
                    and ensure it is allocated to your account in the correct region
                    before attempting to release it."
                """
                )
            raise

    # snippet-end:[python.example_code.ec2.ReleaseAddress]


# snippet-end:[python.example_code.ec2.ElasticIpWrapper.class]
