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
            # Improved error handling to catch specific errors like InvalidAddress.Unavailable and InvalidInput,
            # providing more informative error messages for these cases.
            if err.response["Error"]["Code"] in ["InvalidAddress.Unavailable", "InvalidInput"]:
                logger.error(
                    "Couldn't allocate Elastic IP. The requested IP address is not available or the input is invalid."
                )
            else:
                logger.error(
                    "Couldn't allocate Elastic IP. Here's why: %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
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
            response = self.ec2_client.associate_address(AllocationId=self.allocation_id, InstanceId=instance_id)
        except ClientError as err:
            # Improved error handling to catch specific errors like InvalidAssociationID.NotFound
            # and InvalidInstanceID.NotFound, providing more informative error messages for these cases.
            if err.response["Error"]["Code"] in ["InvalidAssociationID.NotFound", "InvalidInstanceID.NotFound"]:
                logger.error(
                    "Couldn't associate Elastic IP %s with instance %s. The Elastic IP or instance does not exist.",
                    self.allocation_id,
                    instance_id,
                )
            else:
                logger.error(
                    "Couldn't associate Elastic IP %s with instance %s. Here's why: %s: %s",
                    self.allocation_id,
                    instance_id,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise err
        return response

    # snippet-end:[python.example_code.ec2.AssociateAddress]

    # snippet-start:[python.example_code.ec2.DisassociateAddress]
    def disassociate(self, association_id):
        """
        Removes an association between an Elastic IP address and an instance. When the
        association is removed, the instance is assigned a new public IP address.

        :param association_id: The ID of the association to disassociate.
        """
        if association_id is None:
            logger.info("No association ID provided for disassociation.")
            return

        try:
            self.ec2_client.disassociate_address(AssociationId=association_id)
        except ClientError as err:
            # Improved error handling to catch the specific error InvalidAssociationID.NotFound,
            # providing a more informative error message for this case.
            if err.response["Error"]["Code"] == "InvalidAssociationID.NotFound":
                logger.error(
                    "Couldn't disassociate Elastic IP %s from its instance. The association does not exist.",
                    association_id,
                )
            else:
                logger.error(
                    "Couldn't disassociate Elastic IP %s from its instance. Here's why: %s: %s",
                    association_id,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise err

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
            # Improved error handling to catch the specific error InvalidAllocationID.NotFound,
            # providing a more informative error message for this case.
            if err.response["Error"]["Code"] == "InvalidAllocationID.NotFound":
                logger.error(
                    "Couldn't release Elastic IP address %s. The Elastic IP does not exist.",
                    self.allocation_id,
                )
            else:
                logger.error(
                    "Couldn't release Elastic IP address %s. Here's why: %s: %s",
                    self.allocation_id,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise err

    # snippet-end:[python.example_code.ec2.ReleaseAddress]

# snippet-end:[python.example_code.ec2.ElasticIpWrapper.class]