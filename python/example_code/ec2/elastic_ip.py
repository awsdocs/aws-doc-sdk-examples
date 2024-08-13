# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import logging

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ec2.ElasticIpWrapper.class]
# snippet-start:[python.example_code.ec2.ElasticIpWrapper.decl]
class ElasticIpWrapper:
    """Encapsulates Amazon Elastic Compute Cloud (Amazon EC2) Elastic IP address actions using the client interface."""

    class ElasticIp:
        """Represents an Elastic IP and its associated instance."""

        def __init__(self, allocation_id, public_ip, instance_id=None):
            self.allocation_id = allocation_id
            self.public_ip = public_ip
            self.instance_id = instance_id

    def __init__(self, ec2_client):
        """
        :param ec2_client: A Boto3 Amazon EC2 client. This client provides low-level
                           access to AWS EC2 services.
        """
        self.ec2_client = ec2_client
        self.elastic_ips = []

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

        :return: The ElasticIp object for the newly created Elastic IP address.
        """
        try:
            response = self.ec2_client.allocate_address(Domain="vpc")
            elastic_ip = self.ElasticIp(
                allocation_id=response["AllocationId"], public_ip=response["PublicIp"]
            )
            self.elastic_ips.append(elastic_ip)
        except ClientError as err:
            if err.response["Error"]["Code"] in "AddressLimitExceeded":
                logger.error(
                    "Max IP's reached. Release unused addresses or contact AWS Support for an increase."
                )
            raise err
        return elastic_ip

    # snippet-end:[python.example_code.ec2.AllocateAddress]

    # snippet-start:[python.example_code.ec2.AssociateAddress]
    def associate(self, allocation_id, instance_id):
        """
        Associates an Elastic IP address with an instance. When this association is
        created, the Elastic IP's public IP address is immediately used as the public
        IP address of the associated instance.

        :param allocation_id: The allocation ID of the Elastic IP.
        :param instance_id: The ID of the Amazon EC2 instance.
        :return: A response that contains the ID of the association.
        """
        elastic_ip = self.get_elastic_ip_by_allocation(allocation_id)
        if elastic_ip is None:
            logger.info(f"No Elastic IP found with allocation ID {allocation_id}.")
            return

        try:
            response = self.ec2_client.associate_address(
                AllocationId=allocation_id, InstanceId=instance_id
            )
            elastic_ip.instance_id = (
                instance_id  # Track the instance associated with this Elastic IP
            )
        except ClientError as err:
            if err.response["Error"]["Code"] in "InvalidInstanceID.NotFound":
                logger.error(
                    f"Failed to associate Elastic IP {allocation_id} with {instance_id} "
                    "because the specified instance ID does not exist or has not propagated fully. "
                    "Verify the instance ID and try again, or wait a few moments before attempting to "
                    "associate the Elastic IP address."
                )
                raise
        return response

    # snippet-end:[python.example_code.ec2.AssociateAddress]

    # snippet-start:[python.example_code.ec2.DisassociateAddress]
    def disassociate(self, allocation_id):
        """
        Removes an association between an Elastic IP address and an instance. When the
        association is removed, the instance is assigned a new public IP address.

        :param allocation_id: The allocation ID of the Elastic IP to disassociate.
        """
        elastic_ip = self.get_elastic_ip_by_allocation(allocation_id)
        if elastic_ip is None or elastic_ip.instance_id is None:
            logger.info(
                f"No association found for Elastic IP with allocation ID {allocation_id}."
            )
            return

        try:
            # Retrieve the association ID before disassociating
            response = self.ec2_client.describe_addresses(AllocationIds=[allocation_id])
            association_id = response["Addresses"][0].get("AssociationId")

            if association_id:
                self.ec2_client.disassociate_address(AssociationId=association_id)
                elastic_ip.instance_id = None  # Remove the instance association
            else:
                logger.info(
                    f"No Association ID found for Elastic IP with allocation ID {allocation_id}."
                )

        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidAssociationID.NotFound":
                logger.error(
                    f"Failed to disassociate Elastic IP {allocation_id} "
                    "because the specified association ID for the Elastic IP address was not found. "
                    "Verify the association ID and ensure the Elastic IP is currently associated with a "
                    "resource before attempting to dissociate it."
                )
            raise

    # snippet-end:[python.example_code.ec2.DisassociateAddress]

    # snippet-start:[python.example_code.ec2.ReleaseAddress]
    def release(self, allocation_id):
        """
        Releases an Elastic IP address. After the Elastic IP address is released,
        it can no longer be used.

        :param allocation_id: The allocation ID of the Elastic IP to release.
        """
        elastic_ip = self.get_elastic_ip_by_allocation(allocation_id)
        if elastic_ip is None:
            logger.info(f"No Elastic IP found with allocation ID {allocation_id}.")
            return

        try:
            self.ec2_client.release_address(AllocationId=allocation_id)
            self.elastic_ips.remove(elastic_ip)  # Remove the Elastic IP from the list
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidAddress.NotFound":
                logger.error(
                    f"Failed to release Elastic IP address {allocation_id} "
                    "because it could not be found. Verify the Elastic IP address "
                    "and ensure it is allocated to your account in the correct region "
                    "before attempting to release it."
                )
            raise

    # snippet-end:[python.example_code.ec2.ReleaseAddress]

    def get_elastic_ip_by_allocation(self, allocation_id):
        """
        Retrieves an Elastic IP object by its allocation ID.

        :param allocation_id: The allocation ID of the Elastic IP to retrieve.
        :return: The ElasticIp object associated with the allocation ID, or None if not found.
        """
        for elastic_ip in self.elastic_ips:
            if elastic_ip.allocation_id == allocation_id:
                return elastic_ip
        return None


# snippet-end:[python.example_code.ec2.ElasticIpWrapper.class]
