# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the AWS KMS Basics scenario.

These tests exercise the KmsWrapper and KmsScenario against the live
AWS KMS service. They do NOT mock the KMS client. All resources created
during the tests are cleaned up in a ``finally`` block.

Usage:
    pytest test_kms_basics.py -v
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import json
import random
import time

import boto3
import pytest
from botocore.exceptions import ClientError

from kms_wrapper import KmsWrapper


@pytest.fixture(scope="module")
def kms_wrapper():
    """Provides a KmsWrapper instance backed by real Boto3 clients."""
    return KmsWrapper.from_client()


@pytest.fixture(scope="module")
def alias_name():
    """Generates a unique alias name for the test run."""
    suffix = random.randint(100000, 999999)
    return f"alias/kms-integ-test-{suffix}"


@pytest.mark.integ
class TestKmsWrapper:
    """Integration tests that exercise each KmsWrapper method."""

    key_id_1 = None
    key_id_2 = None
    data_key_ciphertext = None

    @pytest.mark.integ
    def test_01_list_keys(self, kms_wrapper):
        """ListKeys should return a list (possibly empty)."""
        keys = kms_wrapper.list_keys()
        assert isinstance(keys, list)

    @pytest.mark.integ
    def test_02_create_key(self, kms_wrapper):
        """CreateKey should return key metadata with a KeyId."""
        metadata = kms_wrapper.create_key(description="Integration test key 1")
        assert "KeyId" in metadata
        assert "Arn" in metadata
        assert metadata["KeyState"] == "Enabled"
        TestKmsWrapper.key_id_1 = metadata["KeyId"]

    @pytest.mark.integ
    def test_03_create_alias(self, kms_wrapper, alias_name):
        """CreateAlias should succeed without raising an error."""
        assert TestKmsWrapper.key_id_1 is not None, "Key not created in test_02"
        kms_wrapper.create_alias(
            alias_name=alias_name,
            target_key_id=TestKmsWrapper.key_id_1,
        )

    @pytest.mark.integ
    def test_04_put_key_policy(self, kms_wrapper):
        """PutKeyPolicy should apply a policy without error."""
        assert TestKmsWrapper.key_id_1 is not None
        kms_wrapper.put_key_policy(key_id=TestKmsWrapper.key_id_1)

    @pytest.mark.integ
    def test_05_enable_key_rotation(self, kms_wrapper):
        """EnableKeyRotation should enable rotation without error."""
        assert TestKmsWrapper.key_id_1 is not None
        kms_wrapper.enable_key_rotation(
            key_id=TestKmsWrapper.key_id_1,
            rotation_period_in_days=180,
        )

    @pytest.mark.integ
    def test_06_generate_data_key(self, kms_wrapper):
        """GenerateDataKey should return plaintext and ciphertext blobs."""
        assert TestKmsWrapper.key_id_1 is not None
        response = kms_wrapper.generate_data_key(key_id=TestKmsWrapper.key_id_1)
        assert "Plaintext" in response
        assert "CiphertextBlob" in response
        assert len(response["Plaintext"]) == 32  # AES_256 = 32 bytes
        TestKmsWrapper.data_key_ciphertext = response["CiphertextBlob"]

    @pytest.mark.integ
    def test_07_re_encrypt(self, kms_wrapper):
        """ReEncrypt should produce new ciphertext under a second key."""
        assert TestKmsWrapper.data_key_ciphertext is not None
        # Create second key for re-encryption.
        metadata2 = kms_wrapper.create_key(description="Integration test key 2")
        TestKmsWrapper.key_id_2 = metadata2["KeyId"]

        response = kms_wrapper.re_encrypt(
            ciphertext_blob=TestKmsWrapper.data_key_ciphertext,
            destination_key_id=TestKmsWrapper.key_id_2,
            source_key_id=TestKmsWrapper.key_id_1,
        )
        assert "CiphertextBlob" in response
        assert "KeyId" in response

    @pytest.mark.integ
    def test_08_update_alias(self, kms_wrapper, alias_name):
        """UpdateAlias should redirect the alias to the second key."""
        assert TestKmsWrapper.key_id_2 is not None
        kms_wrapper.update_alias(
            alias_name=alias_name,
            target_key_id=TestKmsWrapper.key_id_2,
        )

    @pytest.mark.integ
    def test_09_list_key_policies(self, kms_wrapper):
        """ListKeyPolicies should return at least the 'default' policy."""
        assert TestKmsWrapper.key_id_1 is not None
        policies = kms_wrapper.list_key_policies(key_id=TestKmsWrapper.key_id_1)
        assert isinstance(policies, list)
        assert "default" in policies

    @pytest.mark.integ
    def test_10_disable_key_rotation(self, kms_wrapper):
        """DisableKeyRotation should succeed without error."""
        assert TestKmsWrapper.key_id_1 is not None
        kms_wrapper.disable_key_rotation(key_id=TestKmsWrapper.key_id_1)

    @pytest.mark.integ
    def test_11_cleanup(self, kms_wrapper, alias_name):
        """Cleanup: delete alias and schedule key deletion for both keys."""
        errors = list()
        try:
            try:
                kms_wrapper.delete_alias(alias_name=alias_name)
            except ClientError as e:
                errors.append(f"delete_alias: {e}")

            if TestKmsWrapper.key_id_1 is not None:
                try:
                    kms_wrapper.schedule_key_deletion(
                        key_id=TestKmsWrapper.key_id_1,
                        pending_window_in_days=7,
                    )
                except ClientError as e:
                    errors.append(f"schedule_key_deletion(key1): {e}")

            if TestKmsWrapper.key_id_2 is not None:
                try:
                    kms_wrapper.schedule_key_deletion(
                        key_id=TestKmsWrapper.key_id_2,
                        pending_window_in_days=7,
                    )
                except ClientError as e:
                    errors.append(f"schedule_key_deletion(key2): {e}")
        finally:
            # Always report but don't fail the suite just for cleanup.
            if errors:
                print(f"Cleanup warnings: {errors}")


@pytest.mark.integ
def test_full_scenario():
    """
    Runs the full KMS Basics scenario end-to-end.
    Resources are created and cleaned up within the scenario's own
    try/finally block.
    """
    from scenario_kms_basics import KmsScenario

    kms_wrapper = KmsWrapper.from_client()
    scenario = KmsScenario(kms_wrapper)
    try:
        scenario.run_scenario()
    finally:
        # Scenario cleans up in its own finally block; this is a safety net.
        pass
