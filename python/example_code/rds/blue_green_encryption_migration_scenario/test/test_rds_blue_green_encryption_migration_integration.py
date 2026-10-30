# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration test for the Blue/Green Encryption Migration scenario.

This test runs the full scenario against real AWS resources and requires:
  - Valid AWS credentials.
  - Two KMS key ARNs set via environment variables:
      ORIGINAL_KMS_KEY_ARN and TARGET_KMS_KEY_ARN.

WARNING: Running this test creates real AWS resources and may incur charges.
"""

import os
import uuid

import pytest

from rds_blue_green_encryption_migration_wrapper import (
    BlueGreenEncryptionMigrationWrapper,
)
from rds_blue_green_encryption_migration_scenario import (
    BlueGreenEncryptionMigrationScenario,
)


@pytest.mark.integ
def test_blue_green_encryption_migration_scenario(capsys):
    """
    Runs the full Blue/Green encryption migration scenario against real AWS.
    The scenario cleans up its resources in the finally block of run().
    """
    original_kms_key = os.environ.get("ORIGINAL_KMS_KEY_ARN")
    target_kms_key = os.environ.get("TARGET_KMS_KEY_ARN")
    if not original_kms_key or not target_kms_key:
        pytest.skip(
            "Set ORIGINAL_KMS_KEY_ARN and TARGET_KMS_KEY_ARN environment "
            "variables to run this integration test."
        )

    unique_suffix = uuid.uuid4().hex[:8]
    scenario = BlueGreenEncryptionMigrationScenario(
        BlueGreenEncryptionMigrationWrapper.from_client()
    )

    scenario.run(
        original_kms_key_id=original_kms_key,
        target_kms_key_id=target_kms_key,
        db_instance_identifier=f"integ-bg-enc-{unique_suffix}",
        deployment_name=f"integ-kms-mig-{unique_suffix}",
        master_user_password=f"IntegTest-{uuid.uuid4().hex[:12]}",
    )

    capt = capsys.readouterr()
    assert "SUCCESS: KMS encryption key migration completed!" in capt.out
