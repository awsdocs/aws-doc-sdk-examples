# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Demonstrates an AWS KMS Basics scenario that walks through the full
    lifecycle of KMS key management:

    1.  List existing KMS keys.
    2.  Create a symmetric encryption KMS key.
    3.  Create an alias for the key.
    4.  Set a key policy granting the account root full access.
    5.  Enable automatic key rotation (180-day period).
    6.  Generate a data key for envelope encryption.
    7.  Create a second key and re-encrypt the data key under it.
    8.  Update the alias to point to the second key.
    9.  List key policies for the first key.
    10. Disable automatic key rotation on the first key.
    11. Delete the alias.
    12. Schedule deletion of both KMS keys (7-day window).
"""

import logging
import random
import time

import boto3
from botocore.exceptions import ClientError

from kms_wrapper import KmsWrapper

logger = logging.getLogger(__name__)

ALIAS_PREFIX = "alias/kms-basics-"


# snippet-start:[python.example_code.kms.KmsScenario]
class KmsScenario:
    """Runs the AWS KMS Basics scenario."""

    def __init__(self, kms_wrapper: KmsWrapper):
        """
        :param kms_wrapper: An instance of KmsWrapper that wraps KMS operations.
        """
        self.kms_wrapper = kms_wrapper
        self.key_id_1 = None
        self.key_id_2 = None
        self.alias_name = None

    def run_scenario(self) -> None:
        """Runs all steps of the KMS Basics scenario."""
        print("=" * 68)
        print("Welcome to the AWS KMS Basics Scenario")
        print("=" * 68)

        # Generate a unique alias name to avoid collisions.
        suffix = random.randint(1000, 9999)
        self.alias_name = f"{ALIAS_PREFIX}{suffix}"

        try:
            self._step_1_list_keys()
            self._step_2_create_key()
            self._step_3_create_alias()
            self._step_4_put_key_policy()
            self._step_5_enable_key_rotation()
            self._step_6_generate_data_key()
            self._step_7_re_encrypt()
            self._step_8_update_alias()
            self._step_9_list_key_policies()
            self._step_10_disable_key_rotation()
        finally:
            self._cleanup()

        print("\n" + "=" * 68)
        print("KMS Basics Scenario completed successfully!")
        print("=" * 68)

    def _step_1_list_keys(self) -> None:
        """Step 1: List existing KMS keys."""
        print("\n--- Step 1: List existing KMS keys ---")
        keys = self.kms_wrapper.list_keys()
        print(f"Found {len(keys)} KMS key(s) in the current account and Region.")
        for key in keys[:5]:
            print(f"  Key ID: {key['KeyId']}")
        if len(keys) > 5:
            print(f"  ... and {len(keys) - 5} more.")

    def _step_2_create_key(self) -> None:
        """Step 2: Create a symmetric encryption KMS key."""
        print("\n--- Step 2: Create a symmetric encryption KMS key ---")
        key_metadata = self.kms_wrapper.create_key(
            description="KMS Basics scenario key (primary)"
        )
        self.key_id_1 = key_metadata["KeyId"]
        print(f"  Key ID:  {key_metadata['KeyId']}")
        print(f"  Key ARN: {key_metadata['Arn']}")
        print(f"  Status:  {key_metadata['KeyState']}")

    def _step_3_create_alias(self) -> None:
        """Step 3: Create an alias for the key."""
        print("\n--- Step 3: Create an alias for the key ---")
        self.kms_wrapper.create_alias(
            alias_name=self.alias_name,
            target_key_id=self.key_id_1,
        )
        print(f"  Alias '{self.alias_name}' -> Key '{self.key_id_1}'")

    def _step_4_put_key_policy(self) -> None:
        """Step 4: Set a key policy on the KMS key."""
        print("\n--- Step 4: Set a key policy ---")
        self.kms_wrapper.put_key_policy(key_id=self.key_id_1)
        print(f"  Key policy applied to key '{self.key_id_1}'.")
        print("  The policy grants the account root full KMS access.")

    def _step_5_enable_key_rotation(self) -> None:
        """Step 5: Enable automatic key rotation."""
        print("\n--- Step 5: Enable automatic key rotation ---")
        rotation_period = 180
        self.kms_wrapper.enable_key_rotation(
            key_id=self.key_id_1,
            rotation_period_in_days=rotation_period,
        )
        print(
            f"  Automatic rotation enabled for key '{self.key_id_1}' "
            f"with a {rotation_period}-day period."
        )

    def _step_6_generate_data_key(self) -> None:
        """Step 6: Generate a data key for envelope encryption."""
        print("\n--- Step 6: Generate a data key ---")
        data_key = self.kms_wrapper.generate_data_key(key_id=self.key_id_1)
        self._data_key_ciphertext = data_key["CiphertextBlob"]
        print(f"  KMS key used:       {data_key['KeyId']}")
        print(f"  Plaintext size:     {len(data_key['Plaintext'])} bytes")
        print(f"  Ciphertext size:    {len(data_key['CiphertextBlob'])} bytes")
        print(
            "  Envelope encryption pattern: use the plaintext key to encrypt "
            "your data locally, store the encrypted data key alongside the "
            "ciphertext, then discard the plaintext key from memory."
        )

    def _step_7_re_encrypt(self) -> None:
        """Step 7: Create a second key and re-encrypt the data key."""
        print("\n--- Step 7: Re-encrypt the data key under a second KMS key ---")
        # Create a second KMS key.
        key_metadata_2 = self.kms_wrapper.create_key(
            description="KMS Basics scenario key (secondary)"
        )
        self.key_id_2 = key_metadata_2["KeyId"]
        print(f"  Created second key: {self.key_id_2}")

        # Re-encrypt the data key ciphertext under the second key.
        result = self.kms_wrapper.re_encrypt(
            ciphertext_blob=self._data_key_ciphertext,
            destination_key_id=self.key_id_2,
            source_key_id=self.key_id_1,
        )
        print(f"  Source key:      {result.get('SourceKeyId', 'N/A')}")
        print(f"  Destination key: {result.get('KeyId', 'N/A')}")
        print(
            "  The data key was re-encrypted without exposing plaintext. "
            "The new ciphertext can only be decrypted with the second key."
        )

    def _step_8_update_alias(self) -> None:
        """Step 8: Update the alias to point to the second key."""
        print("\n--- Step 8: Update the alias to the second key ---")
        self.kms_wrapper.update_alias(
            alias_name=self.alias_name,
            target_key_id=self.key_id_2,
        )
        print(
            f"  Alias '{self.alias_name}' now points to key '{self.key_id_2}'."
        )
        print(
            "  Applications using the alias will seamlessly transition to "
            "the new key."
        )

    def _step_9_list_key_policies(self) -> None:
        """Step 9: List key policies for the first key."""
        print("\n--- Step 9: List key policies ---")
        policy_names = self.kms_wrapper.list_key_policies(key_id=self.key_id_1)
        print(f"  Key '{self.key_id_1}' has {len(policy_names)} policy(ies):")
        for name in policy_names:
            print(f"    - {name}")
        print(
            "  KMS keys have a default policy. You can modify it with "
            "PutKeyPolicy (as done in Step 4)."
        )

    def _step_10_disable_key_rotation(self) -> None:
        """Step 10: Disable automatic key rotation."""
        print("\n--- Step 10: Disable automatic key rotation ---")
        self.kms_wrapper.disable_key_rotation(key_id=self.key_id_1)
        print(f"  Automatic rotation disabled for key '{self.key_id_1}'.")
        print(
            "  Disabling rotation does not affect existing key material versions."
        )

    def _cleanup(self) -> None:
        """
        Cleanup: delete alias and schedule both keys for deletion.
        Wrapped in try/except so cleanup proceeds even if individual
        steps fail.
        """
        print("\n--- Cleanup ---")

        # Step 11: Delete the alias.
        if self.alias_name is not None:
            try:
                self.kms_wrapper.delete_alias(alias_name=self.alias_name)
                print(f"  Deleted alias '{self.alias_name}'.")
            except ClientError:
                logger.info("Could not delete alias '%s'. Continuing.", self.alias_name)

        # Step 12: Schedule deletion of the first key.
        if self.key_id_1 is not None:
            try:
                result = self.kms_wrapper.schedule_key_deletion(
                    key_id=self.key_id_1, pending_window_in_days=7
                )
                print(
                    f"  Key '{self.key_id_1}' scheduled for deletion on "
                    f"{result.get('DeletionDate', 'N/A')}."
                )
            except ClientError:
                logger.info(
                    "Could not schedule deletion of key '%s'. Continuing.",
                    self.key_id_1,
                )

        # Step 13: Schedule deletion of the second key.
        if self.key_id_2 is not None:
            try:
                result = self.kms_wrapper.schedule_key_deletion(
                    key_id=self.key_id_2, pending_window_in_days=7
                )
                print(
                    f"  Key '{self.key_id_2}' scheduled for deletion on "
                    f"{result.get('DeletionDate', 'N/A')}."
                )
            except ClientError:
                logger.info(
                    "Could not schedule deletion of key '%s'. Continuing.",
                    self.key_id_2,
                )

        print(
            "\n  WARNING: Key deletion is irreversible after the 7-day "
            "waiting period. All data encrypted under these keys will "
            "become unrecoverable."
        )
# snippet-end:[python.example_code.kms.KmsScenario]


def main():
    """Entry point for the KMS Basics scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    kms_wrapper = KmsWrapper.from_client()
    scenario = KmsScenario(kms_wrapper)
    scenario.run_scenario()


if __name__ == "__main__":
    main()
