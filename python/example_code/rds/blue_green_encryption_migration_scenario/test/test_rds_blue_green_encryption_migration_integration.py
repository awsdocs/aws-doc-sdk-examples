# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration test for the Blue/Green Encryption Migration scenario.

This test runs the full scenario against real AWS resources.
It is marked @pytest.mark.integ and is NOT run in the pre-merge offline check.
It requires:
  - Valid AWS credentials.
  - Two KMS key ARNs set via environment variables:
      ORIGINAL_KMS_KEY_ARN and TARGET_KMS_KEY_ARN.

WARNING: Running this test will create real AWS resources and may incur charges.
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import uuid
import pytest
import boto3

from rds_blue_green_encryption_migration_wrapper import (
    BlueGreenEncryptionMigrationWrapper,
)
from rds_blue_green_encryption_migration_scenario import (
    BlueGreenEncryptionMigrationScenario,
)


@pytest.mark.integ
def test_blue_green_encryption_migration_scenario(monkeypatch):
    """
    Runs the full Blue/Green encryption migration scenario against real AWS.
    User input is mocked via monkeypatch.
    Cleanup happens in the scenario's finally block.
    """
    original_kms_key = os.environ.get("ORIGINAL_KMS_KEY_ARN")
    target_kms_key = os.environ.get("TARGET_KMS_KEY_ARN")

    if not original_kms_key or not target_kms_key:
        pytest.skip(
            "Set ORIGINAL_KMS_KEY_ARN and TARGET_KMS_KEY_ARN environment "
            "variables to run this integration test."
        )

    unique_suffix = uuid.uuid4().hex[:8]
    db_instance_identifier = f"integ-bg-enc-{unique_suffix}"
    deployment_name = f"integ-kms-mig-{unique_suffix}"

    rds_client = boto3.client("rds")
    wrapper = BlueGreenEncryptionMigrationWrapper(rds_client)
    scenario = BlueGreenEncryptionMigrationScenario(wrapper)

    try:
        scenario.run(
            original_kms_key_id=original_kms_key,
            target_kms_key_id=target_kms_key,
            db_instance_identifier=db_instance_identifier,
            deployment_name=deployment_name,
            master_user_password=f"IntegTest-{uuid.uuid4().hex[:12]}",
        )
    finally:
        # Scenario._cleanup() is called in scenario.run()'s finally block,
        # so resources are cleaned up even if the test fails.
        # This outer finally is just for safety.
        pass
