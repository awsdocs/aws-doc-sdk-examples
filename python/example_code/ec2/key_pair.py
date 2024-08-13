# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import logging
import os
import tempfile

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.ec2.KeyPairWrapper.class]
# snippet-start:[python.example_code.ec2.KeyPairWrapper.decl]
class KeyPairWrapper:
    """Encapsulates Amazon Elastic Compute Cloud (Amazon EC2) key pair actions."""

    def __init__(self, ec2_client, key_file_dir, key_pair=None):
        """
        :param ec2_client: A Boto3 Amazon EC2 client. This client provides low-level
                           access to AWS EC2 services.
        :param key_file_dir: The folder where the private key information is stored.
                             This should be a secure folder.
        :param key_pair: A Boto3 KeyPair object. This is a high-level object that
                         wraps key pair actions. Optional.
        """
        self.ec2_client = ec2_client
        self.key_pair = key_pair
        self.key_file_path = None
        self.key_file_dir = key_file_dir

    @classmethod
    def from_client(cls):
        ec2_client = boto3.client("ec2")
        return cls(ec2_client, tempfile.TemporaryDirectory())

    # snippet-end:[python.example_code.ec2.KeyPairWrapper.decl]

    # snippet-start:[python.example_code.ec2.CreateKeyPair]
    def create(self, key_name):
        """
        Creates a key pair that can be used to securely connect to an EC2 instance.
        The returned key pair contains private key information that cannot be retrieved
        again. The private key data is stored as a .pem file.

        :param key_name: The name of the key pair to create.
        :return: A Boto3 KeyPair object that represents the newly created key pair.
        """
        try:
            response = self.ec2_client.create_key_pair(KeyName=key_name)
            self.key_pair = response
            self.key_file_path = os.path.join(
                self.key_file_dir.name, f"{self.key_pair['KeyName']}.pem"
            )
            with open(self.key_file_path, "w") as key_file:
                key_file.write(self.key_pair["KeyMaterial"])
        except ClientError as err:
            breakpoint()
            if err.response["Error"]["Code"] == "InvalidKeyPair.Duplicate":
                logger.error(
                    f"A key pair called {key_name} already exists. "
                    "Please choose a different name for your key pair "
                    "or delete the existing key pair before creating."
                )
            raise
        else:
            return self.key_pair

    # snippet-end:[python.example_code.ec2.CreateKeyPair]

    # snippet-start:[python.example_code.ec2.DescribeKeyPairs]
    def list(self, limit=None):
        """
        Displays a list of key pairs for the current account.

        WARNING: Results are not paginated.

        :param limit: The maximum number of key pairs to list. If not specified,
                      all key pairs will be listed.
        """
        try:
            response = self.ec2_client.describe_key_pairs()
            key_pairs = response.get("KeyPairs", [])

            if limit:
                key_pairs = key_pairs[:limit]

            for key_pair in key_pairs:
                logger.info(
                    f"Found {key_pair['KeyType']} key '{key_pair['KeyName']}' with fingerprint:"
                )
                logger.info(f"\t{key_pair['KeyFingerprint']}")

        except ClientError as err:
            logger.error(f"Failed to list key pairs: {str(err)}")
            raise

    # snippet-end:[python.example_code.ec2.DescribeKeyPairs]

    # snippet-start:[python.example_code.ec2.DeleteKeyPair]
    def delete(self, key_name):
        """
        Deletes a key pair.

        :param key_name: The name of the key pair to delete.
        :return: Boolean indicating whether the deletion was successful or not.
        """
        try:
            self.ec2_client.delete_key_pair(KeyName=key_name)
            logger.info(f"Successfully deleted key pair: {key_name}")
            self.key_pair = None
            return True
        except self.ec2_client.exceptions.ClientError as err:
            logger.error(f"Deletion failed: {key_name}")
            error_code = err.response["Error"]["Code"]
            if error_code == "InvalidKeyPair.NotFound":
                logger.error(
                    f"The key pair '{key_name}' does not exist and cannot be deleted. "
                    "Please verify the key pair name and try again."
                )
                return False
        raise

    # snippet-end:[python.example_code.ec2.DeleteKeyPair]


# snippet-end:[python.example_code.ec2.KeyPairWrapper.class]
