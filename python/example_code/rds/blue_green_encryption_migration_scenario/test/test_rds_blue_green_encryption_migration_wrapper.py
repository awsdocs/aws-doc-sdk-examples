# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for rds_blue_green_encryption_migration_wrapper.py.
"""

import pytest
from botocore.exceptions import ClientError

ACCOUNT = "123456789012"
DB_ID = "test-db"
DB_ARN = f"arn:aws:rds:us-east-1:{ACCOUNT}:db:{DB_ID}"
ORIGINAL_KMS = f"arn:aws:kms:us-east-1:{ACCOUNT}:key/original-key"
TARGET_KMS = f"arn:aws:kms:us-east-1:{ACCOUNT}:key/target-key"
BGD_ID = "bgd-abc123"
BGD_NAME = "test-migration"


@pytest.mark.parametrize("error_code", [None, "DBInstanceAlreadyExistsFault"])
def test_create_db_instance(scenario_data, error_code):
    scenario_data.stubber.stub_create_db_instance(
        DB_ID,
        "mysql",
        "db.t3.micro",
        allocated_storage=20,
        admin_name="admin",
        admin_password="test-password",
        storage_encrypted=True,
        kms_key_id=ORIGINAL_KMS,
        backup_retention_period=1,
        error_code=error_code,
    )
    kwargs = {
        "db_instance_identifier": DB_ID,
        "db_instance_class": "db.t3.micro",
        "engine": "mysql",
        "master_username": "admin",
        "master_user_password": "test-password",
        "allocated_storage": 20,
        "kms_key_id": ORIGINAL_KMS,
    }

    if error_code is None:
        got = scenario_data.wrapper.create_db_instance(**kwargs)
        assert got["DBInstanceIdentifier"] == DB_ID
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.create_db_instance(**kwargs)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "DBInstanceNotFoundFault"])
def test_describe_db_instance(scenario_data, error_code):
    details = {"StorageEncrypted": True, "KmsKeyId": ORIGINAL_KMS}
    scenario_data.stubber.stub_describe_db_instances(
        DB_ID, instance_details=details, error_code=error_code
    )

    if error_code is None:
        got = scenario_data.wrapper.describe_db_instance(DB_ID)
        assert got["DBInstanceStatus"] == "available"
        assert got["KmsKeyId"] == ORIGINAL_KMS
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.describe_db_instance(DB_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code


def test_wait_for_db_instance_available(scenario_data):
    scenario_data.stubber.stub_describe_db_instances(DB_ID, status="creating")
    scenario_data.stubber.stub_describe_db_instances(DB_ID)

    got = scenario_data.wrapper.wait_for_db_instance_available(DB_ID, wait_seconds=1)

    assert got["DBInstanceStatus"] == "available"


def test_wait_for_db_instance_available_timeout(scenario_data):
    scenario_data.stubber.stub_describe_db_instances(DB_ID, status="creating")

    with pytest.raises(TimeoutError):
        scenario_data.wrapper.wait_for_db_instance_available(
            DB_ID, wait_seconds=1, max_wait=1
        )


@pytest.mark.parametrize("error_code", [None, "SourceDatabaseNotSupportedFault"])
def test_create_blue_green_deployment(scenario_data, error_code):
    scenario_data.stubber.stub_create_blue_green_deployment(
        BGD_NAME, DB_ARN, TARGET_KMS, BGD_ID, error_code=error_code
    )

    if error_code is None:
        got = scenario_data.wrapper.create_blue_green_deployment(
            BGD_NAME, DB_ARN, TARGET_KMS
        )
        assert got["BlueGreenDeploymentIdentifier"] == BGD_ID
        assert got["Status"] == "PROVISIONING"
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.create_blue_green_deployment(
                BGD_NAME, DB_ARN, TARGET_KMS
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "BlueGreenDeploymentNotFoundFault"])
def test_describe_blue_green_deployment(scenario_data, error_code):
    scenario_data.stubber.stub_describe_blue_green_deployments(
        BGD_ID, BGD_NAME, "AVAILABLE", source_arn=DB_ARN, error_code=error_code
    )

    if error_code is None:
        got = scenario_data.wrapper.describe_blue_green_deployment(BGD_ID)
        assert got["BlueGreenDeploymentIdentifier"] == BGD_ID
        assert got["Status"] == "AVAILABLE"
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.describe_blue_green_deployment(BGD_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "statuses, expected",
    [
        (["PROVISIONING", "AVAILABLE"], "AVAILABLE"),
        (["INVALID_CONFIGURATION"], RuntimeError),
        (["PROVISIONING"], TimeoutError),
    ],
)
def test_wait_for_deployment_available(scenario_data, statuses, expected):
    for status in statuses:
        scenario_data.stubber.stub_describe_blue_green_deployments(
            BGD_ID, BGD_NAME, status
        )

    if isinstance(expected, str):
        got = scenario_data.wrapper.wait_for_deployment_available(
            BGD_ID, wait_seconds=1
        )
        assert got["Status"] == expected
    else:
        with pytest.raises(expected):
            scenario_data.wrapper.wait_for_deployment_available(
                BGD_ID, wait_seconds=1, max_wait=1
            )


def test_wait_for_switchover_completed(scenario_data):
    scenario_data.stubber.stub_describe_blue_green_deployments(
        BGD_ID, BGD_NAME, "SWITCHOVER_IN_PROGRESS"
    )
    scenario_data.stubber.stub_describe_blue_green_deployments(
        BGD_ID, BGD_NAME, "SWITCHOVER_COMPLETED"
    )

    got = scenario_data.wrapper.wait_for_switchover_completed(BGD_ID, wait_seconds=1)

    assert got["Status"] == "SWITCHOVER_COMPLETED"


@pytest.mark.parametrize("error_code", [None, "InvalidBlueGreenDeploymentStateFault"])
def test_switchover_blue_green_deployment(scenario_data, error_code):
    scenario_data.stubber.stub_switchover_blue_green_deployment(
        BGD_ID, BGD_NAME, 300, error_code=error_code
    )

    if error_code is None:
        got = scenario_data.wrapper.switchover_blue_green_deployment(BGD_ID)
        assert got["Status"] == "SWITCHOVER_IN_PROGRESS"
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.switchover_blue_green_deployment(BGD_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "InvalidBlueGreenDeploymentStateFault"])
def test_delete_blue_green_deployment(scenario_data, error_code):
    scenario_data.stubber.stub_delete_blue_green_deployment(
        BGD_ID, BGD_NAME, False, error_code=error_code
    )

    if error_code is None:
        got = scenario_data.wrapper.delete_blue_green_deployment(BGD_ID)
        assert got["Status"] == "DELETING"
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.delete_blue_green_deployment(BGD_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "DBInstanceNotFoundFault"])
def test_delete_db_instance(scenario_data, error_code):
    scenario_data.stubber.stub_delete_db_instance(DB_ID, error_code=error_code)

    if error_code is None:
        got = scenario_data.wrapper.delete_db_instance(DB_ID)
        assert got["DBInstanceStatus"] == "deleting"
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.delete_db_instance(DB_ID)
        assert exc_info.value.response["Error"]["Code"] == error_code


def test_wait_for_db_instance_deleted(scenario_data):
    scenario_data.stubber.stub_describe_db_instances(DB_ID, status="deleting")
    scenario_data.stubber.stub_describe_db_instances(
        DB_ID, error_code="DBInstanceNotFoundFault"
    )

    scenario_data.wrapper.wait_for_db_instance_deleted(DB_ID, wait_seconds=1)
