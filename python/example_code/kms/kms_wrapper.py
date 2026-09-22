# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Demonstrates how to use the AWS SDK for Python (Boto3) with AWS Key
    Management Service (AWS KMS) to manage encryption keys and perform
    cryptographic operations.
"""

import json
import logging
from typing import Any, Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kms.KmsWrapper.decl]
class KmsWrapper:
    """Encapsulates AWS KMS key management operations."""

    def __init__(self, kms_client: boto3.client, sts_client: boto3.client = None):
        """
        Initializes the KmsWrapper with a KMS client and an optional STS client.

        :param kms_client: A Boto3 KMS client.
        :param sts_client: A Boto3 STS client (used to get account ID for key policies).
        """
        self.kms_client = kms_client
        self.sts_client = sts_client or boto3.client("sts")

    @classmethod
    def from_client(cls) -> "KmsWrapper":
        """
        Creates a KmsWrapper instance with default Boto3 clients.

        :return: An instance of KmsWrapper.
        """
        kms_client = boto3.client("kms")
        sts_client = boto3.client("sts")
        return cls(kms_client, sts_client)
    # snippet-end:[python.example_code.kms.KmsWrapper.decl]

    def _get_account_id(self) -> str:
        """
        Gets the AWS account ID of the caller using STS.

        :return: The 12-digit AWS account ID.
        """
        identity = self.sts_client.get_caller_identity()
        return identity["Account"]

    # snippet-start:[python.example_code.kms.ListKeys]
    def list_keys(self) -> List[Dict[str, str]]:
        """
        Lists all KMS keys in the account using pagination.

        :return: A list of dictionaries containing KeyId and KeyArn for each key.
        :raises ClientError: If the listing fails due to a KMS internal error.
        """
        keys = list()
        try:
            paginator = self.kms_client.get_paginator("list_keys")
            for page in paginator.paginate():
                keys.extend(page.get("Keys", list()))
            logger.info("Listed %d KMS keys.", len(keys))
        except ClientError as err:
            if err.response["Error"]["Code"] == "KMSInternalException":
                logger.error(
                    "KMS internal error while listing keys: %s",
                    err.response["Error"]["Message"],
                )
            raise
        return keys
    # snippet-end:[python.example_code.kms.ListKeys]

    # snippet-start:[python.example_code.kms.CreateKey]
    def create_key(
        self, description: str = "KMS Basics scenario key", key_usage: str = "ENCRYPT_DECRYPT"
    ) -> Dict[str, Any]:
        """
        Creates a symmetric encryption KMS key.

        :param description: A description for the key.
        :param key_usage: The cryptographic usage for the key.
        :return: The key metadata dictionary from the CreateKey response.
        :raises ClientError: If the key limit has been exceeded.
        """
        try:
            response = self.kms_client.create_key(
                Description=description,
                KeyUsage=key_usage,
            )
            key_metadata = response["KeyMetadata"]
            logger.info(
                "Created KMS key with ID: %s, ARN: %s",
                key_metadata["KeyId"],
                key_metadata["Arn"],
            )
            return key_metadata
        except ClientError as err:
            if err.response["Error"]["Code"] == "LimitExceededException":
                logger.error(
                    "Account has reached maximum number of KMS keys. "
                    "Delete unused keys or request a limit increase. %s",
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.kms.CreateKey]

    # snippet-start:[python.example_code.kms.CreateAlias]
    def create_alias(self, alias_name: str, target_key_id: str) -> None:
        """
        Creates an alias for a KMS key.

        :param alias_name: The alias name, which must start with 'alias/'.
        :param target_key_id: The key ID or ARN of the KMS key.
        :raises ClientError: If an alias with that name already exists.
        """
        try:
            self.kms_client.create_alias(
                AliasName=alias_name,
                TargetKeyId=target_key_id,
            )
            logger.info(
                "Created alias '%s' for key '%s'.", alias_name, target_key_id
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "AlreadyExistsException":
                logger.error(
                    "Alias '%s' already exists. Use a different name or "
                    "delete the existing alias first. %s",
                    alias_name,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.kms.CreateAlias]

    # snippet-start:[python.example_code.kms.PutKeyPolicy]
    def put_key_policy(self, key_id: str, policy: Optional[str] = None) -> None:
        """
        Sets a key policy on a KMS key. If no policy is provided, a default
        policy granting the account root full access is used.

        :param key_id: The key ID or ARN of the KMS key.
        :param policy: A JSON key policy document string. If None, a default is used.
        :raises ClientError: If the policy document is malformed.
        """
        if policy is None:
            account_id = self._get_account_id()
            policy = json.dumps(
                {
                    "Version": "2012-10-17",
                    "Id": "key-policy-1",
                    "Statement": [
                        {
                            "Sid": "Enable IAM User Permissions",
                            "Effect": "Allow",
                            "Principal": {
                                "AWS": f"arn:aws:iam::{account_id}:root"
                            },
                            "Action": "kms:*",
                            "Resource": "*",
                        }
                    ],
                }
            )
        try:
            self.kms_client.put_key_policy(
                KeyId=key_id,
                PolicyName="default",
                Policy=policy,
            )
            logger.info("Set key policy for key '%s'.", key_id)
        except ClientError as err:
            if err.response["Error"]["Code"] == "MalformedPolicyDocumentException":
                logger.error(
                    "The key policy document is malformed. Check the JSON "
                    "syntax and required elements. %s",
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.kms.PutKeyPolicy]

    # snippet-start:[python.example_code.kms.EnableKeyRotation]
    def enable_key_rotation(self, key_id: str, rotation_period_in_days: int = 180) -> None:
        """
        Enables automatic key rotation for a symmetric encryption KMS key.

        :param key_id: The key ID or ARN of the KMS key.
        :param rotation_period_in_days: The rotation period in days (90–2560).
        :raises ClientError: If rotation is not supported for this key type.
        """
        try:
            self.kms_client.enable_key_rotation(
                KeyId=key_id,
                RotationPeriodInDays=rotation_period_in_days,
            )
            logger.info(
                "Enabled automatic rotation for key '%s' with a %d-day period.",
                key_id,
                rotation_period_in_days,
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "UnsupportedOperationException":
                logger.error(
                    "Key rotation is not supported for this type of KMS key. "
                    "Only symmetric encryption keys support automatic rotation. %s",
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.kms.EnableKeyRotation]

    # snippet-start:[python.example_code.kms.GenerateDataKey]
    def generate_data_key(self, key_id: str, key_spec: str = "AES_256") -> Dict[str, Any]:
        """
        Generates a unique symmetric data encryption key for envelope encryption.

        :param key_id: The key ID, ARN, or alias of the KMS key.
        :param key_spec: The key spec (e.g., AES_256).
        :return: A dictionary with 'CiphertextBlob', 'Plaintext', and 'KeyId'.
        :raises ClientError: If the KMS key is disabled.
        """
        try:
            response = self.kms_client.generate_data_key(
                KeyId=key_id,
                KeySpec=key_spec,
            )
            logger.info(
                "Generated data key under KMS key '%s'. "
                "Plaintext length: %d bytes, ciphertext length: %d bytes.",
                response["KeyId"],
                len(response["Plaintext"]),
                len(response["CiphertextBlob"]),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "DisabledException":
                logger.error(
                    "The KMS key '%s' is disabled. Enable the key before "
                    "performing cryptographic operations. %s",
                    key_id,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.kms.GenerateDataKey]

    # snippet-start:[python.example_code.kms.ReEncrypt]
    def re_encrypt(
        self,
        ciphertext_blob: bytes,
        destination_key_id: str,
        source_key_id: Optional[str] = None,
    ) -> Dict[str, Any]:
        """
        Re-encrypts ciphertext from one KMS key to another without exposing
        the plaintext.

        :param ciphertext_blob: The encrypted data to re-encrypt.
        :param destination_key_id: The key ID or ARN of the destination KMS key.
        :param source_key_id: The key ID or ARN of the source KMS key (recommended).
        :return: The response dictionary with the re-encrypted ciphertext and key info.
        :raises ClientError: If the source or destination key is not found.
        """
        params = dict()
        params["CiphertextBlob"] = ciphertext_blob
        params["DestinationKeyId"] = destination_key_id
        if source_key_id is not None:
            params["SourceKeyId"] = source_key_id
        try:
            response = self.kms_client.re_encrypt(**params)
            logger.info(
                "Re-encrypted data from key '%s' to key '%s'.",
                response.get("SourceKeyId", "unknown"),
                response.get("KeyId", "unknown"),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.error(
                    "The source or destination KMS key was not found. "
                    "Verify the key IDs are correct. %s",
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.kms.ReEncrypt]

    # snippet-start:[python.example_code.kms.UpdateAlias]
    def update_alias(self, alias_name: str, target_key_id: str) -> None:
        """
        Updates an alias to point to a different KMS key.

        :param alias_name: The alias to update (must start with 'alias/').
        :param target_key_id: The key ID or ARN of the new target KMS key.
        :raises ClientError: If the alias or target key is not found.
        """
        try:
            self.kms_client.update_alias(
                AliasName=alias_name,
                TargetKeyId=target_key_id,
            )
            logger.info(
                "Updated alias '%s' to point to key '%s'.",
                alias_name,
                target_key_id,
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.error(
                    "The alias or target key was not found. Verify both the "
                    "alias name and the new target key ID. %s",
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.kms.UpdateAlias]

    # snippet-start:[python.example_code.kms.ListKeyPolicies]
    def list_key_policies(self, key_id: str) -> List[str]:
        """
        Lists the names of key policies attached to a KMS key.

        :param key_id: The key ID or ARN of the KMS key.
        :return: A list of policy name strings (typically ['default']).
        :raises ClientError: If the specified key is not found.
        """
        try:
            response = self.kms_client.list_key_policies(KeyId=key_id)
            policy_names = response.get("PolicyNames", list())
            logger.info(
                "Key '%s' has %d key policies: %s",
                key_id,
                len(policy_names),
                policy_names,
            )
            return policy_names
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.error(
                    "The KMS key '%s' was not found. Verify the key ID. %s",
                    key_id,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.kms.ListKeyPolicies]

    # snippet-start:[python.example_code.kms.DisableKeyRotation]
    def disable_key_rotation(self, key_id: str) -> None:
        """
        Disables automatic key rotation for a KMS key.

        :param key_id: The key ID or ARN of the KMS key.
        :raises ClientError: If the key is in an invalid state.
        """
        try:
            self.kms_client.disable_key_rotation(KeyId=key_id)
            logger.info("Disabled automatic rotation for key '%s'.", key_id)
        except ClientError as err:
            if err.response["Error"]["Code"] == "KMSInvalidStateException":
                logger.error(
                    "Key '%s' is not in a valid state for this operation "
                    "(e.g., pending deletion or disabled). %s",
                    key_id,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.kms.DisableKeyRotation]

    # snippet-start:[python.example_code.kms.DeleteAlias]
    def delete_alias(self, alias_name: str) -> None:
        """
        Deletes a KMS alias.

        :param alias_name: The alias to delete (must start with 'alias/').
        :raises ClientError: If the alias is not found.
        """
        try:
            self.kms_client.delete_alias(AliasName=alias_name)
            logger.info("Deleted alias '%s'.", alias_name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "NotFoundException":
                logger.error(
                    "Alias '%s' not found. It may have already been deleted. %s",
                    alias_name,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.kms.DeleteAlias]

    # snippet-start:[python.example_code.kms.ScheduleKeyDeletion]
    def schedule_key_deletion(self, key_id: str, pending_window_in_days: int = 7) -> Dict[str, Any]:
        """
        Schedules a KMS key for deletion after a waiting period.

        WARNING: Deleting a KMS key is destructive and potentially dangerous.
        When a KMS key is deleted, all data encrypted under it is unrecoverable.

        :param key_id: The key ID or ARN of the KMS key.
        :param pending_window_in_days: The waiting period (7–30 days).
        :return: The response dictionary with KeyId, DeletionDate, KeyState, etc.
        :raises ClientError: If the key is in an invalid state.
        """
        try:
            response = self.kms_client.schedule_key_deletion(
                KeyId=key_id,
                PendingWindowInDays=pending_window_in_days,
            )
            logger.info(
                "Scheduled deletion of key '%s'. Deletion date: %s. "
                "Key state: %s.",
                response.get("KeyId", key_id),
                response.get("DeletionDate", "N/A"),
                response.get("KeyState", "N/A"),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "KMSInvalidStateException":
                logger.error(
                    "Key '%s' is not in a valid state to be scheduled for "
                    "deletion (it may already be pending deletion). %s",
                    key_id,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.kms.ScheduleKeyDeletion]
