# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration test for the Blue/Green Encryption Migration scenario.

This test runs the full scenario against real AWS resources and requires:
  - Valid AWS credentials with Amazon RDS, AWS KMS, and AWS CloudFormation
    permissions.
  - The environment variable RUN_RDS_BLUE_GREEN_INTEG_TEST set to 1.

You don't need to create KMS keys. The scenario deploys a CloudFormation stack
that creates them, and deletes the stack during cleanup.

WARNING: Running this test creates real AWS resources and may incur charges.
"""

import os
import uuid

import pytest

from cloudformation_helper import CloudFormationHelper
from rds_blue_green_encryption_migration_wrapper import (
    BlueGreenEncryptionMigrationWrapper,
)
from rds_blue_green_encryption_migration_scenario import (
    BlueGreenEncryptionMigrationScenario,
)


@pytest.mark.integ
def test_blue_green_encryption_migration_scenario(capsys, monkeypatch):
    """
    Runs the full Blue/Green encryption migration scenario against real AWS.
    The scenario cleans up its resources in the finally block of run().
    """
    if os.environ.get("RUN_RDS_BLUE_GREEN_INTEG_TEST") != "1":
        pytest.skip(
            "Set the RUN_RDS_BLUE_GREEN_INTEG_TEST environment variable to 1 "
            "to run this integration test."
        )

    # Answer y to the cleanup question so all resources are deleted.
    monkeypatch.setattr("builtins.input", lambda _: "y")

    unique_suffix = uuid.uuid4().hex[:8]
    scenario = BlueGreenEncryptionMigrationScenario(
        BlueGreenEncryptionMigrationWrapper.from_client(),
        CloudFormationHelper.from_client(),
    )

    scenario.run(
        db_instance_identifier=f"integ-bg-enc-{unique_suffix}",
        deployment_name=f"integ-kms-mig-{unique_suffix}",
        master_user_password=f"IntegTest-{uuid.uuid4().hex[:12]}",
    )

    capt = capsys.readouterr()
    assert "SUCCESS: KMS encryption key migration completed!" in capt.out
