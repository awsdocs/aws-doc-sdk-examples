# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Key Management Service (AWS KMS)
to create, list, and manage keys.
"""

# snippet-start:[python.example_code.kms.Scenario_KeyManagement]
import logging
from pprint import pprint

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kms.KeyManager.decl]
class KeyManager:
    def __init__(self, kms_client):
        self.kms_client = kms_client
        self.created_keys = []

    @classmethod
    def from_client(cls) -> "KeyManager":
        """
        Creates a KeyManager instance with a default KMS client.

        :return: An instance of KeyManager initialized with the default KMS client.
        """
        kms_client = boto3.client("kms")
        return cls(kms_client)

    # snippet-end:[python.example_code.kms.KeyManager.decl]

    # snippet-start:[python.example_code.kms.CreateKey]
    def create_key(self, key_description: str) -> dict[str, any]:
        """
        Creates a key with a user-provided description.

        :param key_description: A description for the key.
        :return: The key ID.
        """
        try:
            key = self.kms_client.create_key(Description=key_description)["KeyMetadata"]
            self.created_keys.append(key)
            return key
        except ClientError as err:
            logging.error(
                "Couldn't create your key. Here's why: %s",
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.kms.CreateKey]

    # snippet-start:[python.example_code.kms.CreateAsymmetricKey]
    def create_asymmetric_key(self) -> str:
        """
        Creates an asymmetric key in AWS KMS for signing messages.

        :return: The ID of the created key.
        """
        try:
            key = self.kms_client.create_key(
                KeySpec="RSA_2048", KeyUsage="SIGN_VERIFY", Origin="AWS_KMS"
            )["KeyMetadata"]
            self.created_keys.append(key)
            return key["KeyId"]
        except ClientError as err:
            logger.error(
                "Couldn't create your key. Here's why: %s",
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.kms.CreateAsymmetricKey]

    # snippet-start:[python.example_code.kms.ListKeys]
    def list_keys(self):
        """
        Lists the keys for the current account by using a paginator.
        """
        try:
            page_size = 10
            print("\nLet's list your keys.")
            key_paginator = self.kms_client.get_paginator("list_keys")
            for key_page in key_paginator.paginate(PaginationConfig={"PageSize": 10}):
                print(f"Here are {len(key_page['Keys'])} keys:")
                pprint(key_page["Keys"])
                if key_page["Truncated"]:
                    answer = input(
                        f"Do you want to see the next {page_size} keys (y/n)? "
                    )
                    if answer.lower() != "y":
                        break
                else:
                    print("That's all your keys!")
        except ClientError as err:
            logging.error(
                "Couldn't list your keys. Here's why: %s",
                err.response["Error"]["Message"],
            )

    # snippet-end:[python.example_code.kms.ListKeys]

    # snippet-start:[python.example_code.kms.DescribeKey]
    def describe_key(self, key_id: str) -> dict[str, any]:
        """
        Describes a key.

        :param key_id: The ARN or ID of the key to describe.
        :return: Information about the key.
        """

        try:
            key = self.kms_client.describe_key(KeyId=key_id)["KeyMetadata"]
            return key
        except ClientError as err:
            logging.error(
                "Couldn't get key '%s'. Here's why: %s",
                key_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.kms.DescribeKey]

    # snippet-start:[python.example_code.kms.GenerateDataKey]
    def generate_data_key(self, key_id):
        """
        Generates a symmetric data key that can be used for client-side encryption.
        """
        answer = input(
            f"Do you want to generate a symmetric data key from key {key_id} (y/n)? "
        )
        if answer.lower() == "y":
            try:
                data_key = self.kms_client.generate_data_key(
                    KeyId=key_id, KeySpec="AES_256"
                )
            except ClientError as err:
                logger.error(
                    "Couldn't generate a data key for key %s. Here's why: %s",
                    key_id,
                    err.response["Error"]["Message"],
                )
            else:
                pprint(data_key)

    # snippet-end:[python.example_code.kms.GenerateDataKey]

    # snippet-start:[python.example_code.kms.EnableKey]
    def enable_key(self, key_id: str) -> None:
        """
        Enables a key. Gets the key state after each state change.

        :param key_id: The ARN or ID of the key to enable.
        """
        try:
            self.kms_client.enable_key(KeyId=key_id)
        except ClientError as err:
            logging.error(
                "Couldn't enable key '%s'. Here's why: %s",
                key_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.kms.EnableKey]

    # snippet-start:[python.example_code.kms.DisableKey]
    def disable_key(self, key_id: str) -> None:
        try:
            self.kms_client.disable_key(KeyId=key_id)
        except ClientError as err:
            logging.error(
                "Couldn't disable key '%s'. Here's why: %s",
                key_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.kms.DisableKey]

    # snippet-start:[python.example_code.kms.ScheduleKeyDeletion]
    def delete_key(self, key_id: str, window: int) -> None:
        """
        Deletes a list of keys.

        Warning:
        Deleting a KMS key is a destructive and potentially dangerous operation. When a KMS key is deleted,
        all data that was encrypted under the KMS key is unrecoverable.

        :param key_id: The ARN or ID of the key to delete.
        :param window: The waiting period, in days, before the KMS key is deleted.
        """

        try:
            self.kms_client.schedule_key_deletion(
                KeyId=key_id, PendingWindowInDays=window
            )
        except ClientError as err:
            logging.error(
                "Couldn't delete key %s. Here's why: %s",
                key_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.kms.ScheduleKeyDeletion]

    # snippet-start:[python.example_code.kms.EnableKeyRotation]
    def enable_key_rotation(self, key_id: str) -> None:
        """
        Enables rotation for a key.

        :param key_id: The ARN or ID of the key to enable rotation for.
        """
        try:
            self.kms_client.enable_key_rotation(KeyId=key_id)
        except ClientError as err:
            logging.error(
                "Couldn't enable rotation for key '%s'. Here's why: %s",
                key_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.kms.EnableKeyRotation]

    # snippet-start:[python.example_code.kms.TagResource]
    def tag_resource(self, key_id: str, tag_key: str, tag_value: str) -> None:
        """
        Add or edit tags on a customer managed key.

        :param key_id: The ARN or ID of the key to enable rotation for.
        :param tag_key: Key for the tag.
        :param tag_value: Value for the tag.
        """
        try:
            self.kms_client.tag_resource(
                KeyId=key_id, Tags=[{"TagKey": tag_key, "TagValue": tag_value}]
            )
        except ClientError as err:
            logging.error(
                "Couldn't add a tag for the key '%s'. Here's why: %s",
                key_id,
                err.response["Error"]["Message"],
            )
            raise

    # snippet-end:[python.example_code.kms.TagResource]


def key_management(kms_client):
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    print("-" * 88)
    print("Welcome to the AWS Key Management Service (AWS KMS) key management demo.")
    print("-" * 88)

    key_manager = KeyManager(kms_client)

    answer = "y"
    while answer.lower() == "y":
        key_desc = input("\nLet's create a key. Describe it for me: ")
        if not key_desc:
            key_desc = "Key management demo key"
        try:
            key = key_manager.create_key(key_desc)
        except ClientError as err:
            logging.error(
                "Couldn't create your key. Here's why: %s",
                err.response["Error"]["Message"],
            )
            raise
        else:
            print("Key created:")
            pprint(key)
            answer = input("Create another (y/n)? ")

    print("-" * 88)
    key_manager.list_keys()
    print("-" * 88)
    key_id = input("Enter a key ID or ARN here to get information about the key: ")
    if key_id:
        key = key_manager.describe_key(key_id)
        print(f"Got key {key_id}:")
        pprint(key)
    if key_id:
        answer = input("Do you want to disable and then enable that key (y/n)? ")
        if answer.lower() == "y":
            key_manager.disable_key(key_id)
            key = key_manager.describe_key(key_id)
            print(f"AWS KMS says your key state is: {key['KeyState']}.")
            key_manager.enable_key(key_id)
            key = key_manager.describe_key(key_id)
            print(f"AWS KMS says your key state is: {key['KeyState']}.")

    print("-" * 88)
    key_manager.generate_data_key(key_id)
    print("-" * 88)
    print("For this demo, we created these keys:")
    for key in key_manager.created_keys:
        print(f"\tKeyId: {key['KeyId']}")
        print(f"\tDescription: {key['Description']}")
        print("-" * 66)
    print(
        """
      Warning:
          Deleting a KMS key is a destructive and potentially dangerous operation. When a KMS key is deleted,
          all data that was encrypted under the KMS key is unrecoverable.
          """
    )

    answer = input("Do you want to delete these keys (y/n)? ")
    if answer.lower() == "y":
        window = 7
        for key in key_manager.created_keys:
            key_manager.delete_key(key["KeyId"], window)
            print(f"Key {key['KeyId']} scheduled for deletion in {window} days.")

    print("\nThanks for watching!")
    print("-" * 88)


if __name__ == "__main__":
    try:
        key_management(boto3.client("kms"))
    except Exception:
        logging.exception("Something went wrong with the demo!")
# snippet-end:[python.example_code.kms.Scenario_KeyManagement]
