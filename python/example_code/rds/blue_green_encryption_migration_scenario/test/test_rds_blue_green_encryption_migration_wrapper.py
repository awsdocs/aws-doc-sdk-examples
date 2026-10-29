# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for rds_blue_green_encryption_migration_wrapper.py

Uses botocore.stub.Stubber to stub every AWS call. Each wrapper method has
a success test and an error test that verifies the specific exception from the
specification is raised.
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import pytest
import boto3
from botocore.stub import Stubber, ANY
from botocore.exceptions import ClientError

from rds_blue_green_encryption_migration_wrapper import (
    BlueGreenEncryptionMigrationWrapper,
)


# ---------------------------------------------------------------------------
# Fixtures
# ---------------------------------------------------------------------------

@pytest.fixture
def rds_client():
    """Create a boto3 RDS client."""
    return boto3.client("rds", region_name="us-east-1")


@pytest.fixture
def rds_stubber(rds_client):
    """Create and activate a Stubber for the RDS client."""
    with Stubber(rds_client) as stubber:
        yield stubber
        stubber.assert_no_pending_responses()


@pytest.fixture
def wrapper(rds_client):
    """Create a wrapper instance with the stubbed client."""
    return BlueGreenEncryptionMigrationWrapper(rds_client)


# ---------------------------------------------------------------------------
# create_db_instance
# ---------------------------------------------------------------------------

class TestCreateDbInstance:
    """Tests for create_db_instance wrapper method."""

    def test_create_db_instance_success(self, wrapper, rds_stubber):
        rds_stubber.add_response(
            "create_db_instance",
            {
                "DBInstance": {
                    "DBInstanceIdentifier": "test-db",
                    "DBInstanceStatus": "creating",
                    "Engine": "mysql",
                    "StorageEncrypted": True,
                    "KmsKeyId": "arn:aws:kms:us-east-1:123456789012:key/original-key",
                    "DBInstanceArn": "arn:aws:rds:us-east-1:123456789012:db:test-db",
                }
            },
            expected_params={
                "DBInstanceIdentifier": "test-db",
                "DBInstanceClass": "db.t3.micro",
                "Engine": "mysql",
                "MasterUsername": "admin",
                "MasterUserPassword": "testpass123",
                "AllocatedStorage": 20,
                "StorageEncrypted": True,
                "KmsKeyId": "arn:aws:kms:us-east-1:123456789012:key/original-key",
                "BackupRetentionPeriod": 1,
            },
        )

        result = wrapper.create_db_instance(
            db_instance_identifier="test-db",
            db_instance_class="db.t3.micro",
            engine="mysql",
            master_username="admin",
            master_user_password="testpass123",
            allocated_storage=20,
            kms_key_id="arn:aws:kms:us-east-1:123456789012:key/original-key",
        )
        assert result["DBInstanceIdentifier"] == "test-db"
        assert result["DBInstanceStatus"] == "creating"
        assert result["StorageEncrypted"] is True

    def test_create_db_instance_already_exists(self, wrapper, rds_stubber):
        rds_stubber.add_client_error(
            "create_db_instance",
            service_error_code="DBInstanceAlreadyExistsFault",
            service_message="DB instance already exists.",
            expected_params={
                "DBInstanceIdentifier": "test-db",
                "DBInstanceClass": "db.t3.micro",
                "Engine": "mysql",
                "MasterUsername": "admin",
                "MasterUserPassword": "testpass123",
                "AllocatedStorage": 20,
                "StorageEncrypted": True,
                "KmsKeyId": "arn:aws:kms:us-east-1:123456789012:key/original-key",
                "BackupRetentionPeriod": 1,
            },
        )

        with pytest.raises(ClientError) as exc_info:
            wrapper.create_db_instance(
                db_instance_identifier="test-db",
                db_instance_class="db.t3.micro",
                engine="mysql",
                master_username="admin",
                master_user_password="testpass123",
                allocated_storage=20,
                kms_key_id="arn:aws:kms:us-east-1:123456789012:key/original-key",
            )
        assert exc_info.value.response["Error"]["Code"] == "DBInstanceAlreadyExistsFault"


# ---------------------------------------------------------------------------
# describe_db_instance
# ---------------------------------------------------------------------------

class TestDescribeDbInstance:
    """Tests for describe_db_instance wrapper method."""

    def test_describe_db_instance_success(self, wrapper, rds_stubber):
        rds_stubber.add_response(
            "describe_db_instances",
            {
                "DBInstances": [
                    {
                        "DBInstanceIdentifier": "test-db",
                        "DBInstanceStatus": "available",
                        "StorageEncrypted": True,
                        "KmsKeyId": "arn:aws:kms:us-east-1:123456789012:key/original-key",
                        "DBInstanceArn": "arn:aws:rds:us-east-1:123456789012:db:test-db",
                        "Engine": "mysql",
                    }
                ]
            },
            expected_params={"DBInstanceIdentifier": "test-db"},
        )

        result = wrapper.describe_db_instance("test-db")
        assert result["DBInstanceIdentifier"] == "test-db"
        assert result["DBInstanceStatus"] == "available"
        assert result["StorageEncrypted"] is True

    def test_describe_db_instance_not_found(self, wrapper, rds_stubber):
        rds_stubber.add_client_error(
            "describe_db_instances",
            service_error_code="DBInstanceNotFoundFault",
            service_message="DB instance not found.",
            expected_params={"DBInstanceIdentifier": "nonexistent-db"},
        )

        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_db_instance("nonexistent-db")
        assert exc_info.value.response["Error"]["Code"] == "DBInstanceNotFoundFault"


# ---------------------------------------------------------------------------
# create_blue_green_deployment
# ---------------------------------------------------------------------------

class TestCreateBlueGreenDeployment:
    """Tests for create_blue_green_deployment wrapper method."""

    def test_create_blue_green_deployment_success(self, wrapper, rds_stubber):
        source_arn = "arn:aws:rds:us-east-1:123456789012:db:test-db"
        target_kms = "arn:aws:kms:us-east-1:123456789012:key/target-key"

        rds_stubber.add_response(
            "create_blue_green_deployment",
            {
                "BlueGreenDeployment": {
                    "BlueGreenDeploymentIdentifier": "bgd-abc123",
                    "BlueGreenDeploymentName": "test-migration",
                    "Status": "PROVISIONING",
                    "Source": source_arn,
                    "SwitchoverDetails": list(),
                    "Tasks": list(),
                }
            },
            expected_params={
                "BlueGreenDeploymentName": "test-migration",
                "Source": source_arn,
                "TargetResourceConfigurations": [
                    {
                        "SourceArn": source_arn,
                        "TargetKmsKeyId": target_kms,
                    }
                ],
            },
        )

        result = wrapper.create_blue_green_deployment(
            deployment_name="test-migration",
            source_arn=source_arn,
            target_kms_key_id=target_kms,
        )
        assert result["BlueGreenDeploymentIdentifier"] == "bgd-abc123"
        assert result["Status"] == "PROVISIONING"

    def test_create_blue_green_deployment_source_not_supported(
        self, wrapper, rds_stubber
    ):
        source_arn = "arn:aws:rds:us-east-1:123456789012:db:test-db"
        target_kms = "arn:aws:kms:us-east-1:123456789012:key/target-key"

        rds_stubber.add_client_error(
            "create_blue_green_deployment",
            service_error_code="SourceDatabaseNotSupportedFault",
            service_message="Source DB instance is not supported.",
            expected_params={
                "BlueGreenDeploymentName": "test-migration",
                "Source": source_arn,
                "TargetResourceConfigurations": [
                    {
                        "SourceArn": source_arn,
                        "TargetKmsKeyId": target_kms,
                    }
                ],
            },
        )

        with pytest.raises(ClientError) as exc_info:
            wrapper.create_blue_green_deployment(
                deployment_name="test-migration",
                source_arn=source_arn,
                target_kms_key_id=target_kms,
            )
        assert (
            exc_info.value.response["Error"]["Code"]
            == "SourceDatabaseNotSupportedFault"
        )


# ---------------------------------------------------------------------------
# describe_blue_green_deployment
# ---------------------------------------------------------------------------

class TestDescribeBlueGreenDeployment:
    """Tests for describe_blue_green_deployment wrapper method."""

    def test_describe_blue_green_deployment_success(self, wrapper, rds_stubber):
        rds_stubber.add_response(
            "describe_blue_green_deployments",
            {
                "BlueGreenDeployments": [
                    {
                        "BlueGreenDeploymentIdentifier": "bgd-abc123",
                        "BlueGreenDeploymentName": "test-migration",
                        "Status": "AVAILABLE",
                        "Source": "arn:aws:rds:us-east-1:123456789012:db:test-db",
                        "Target": "arn:aws:rds:us-east-1:123456789012:db:test-db-green-xyz",
                        "SwitchoverDetails": [
                            {
                                "SourceMember": "arn:aws:rds:us-east-1:123456789012:db:test-db",
                                "TargetMember": "arn:aws:rds:us-east-1:123456789012:db:test-db-green-xyz",
                                "Status": "AVAILABLE",
                            }
                        ],
                        "Tasks": [
                            {"Name": "CREATING_READ_REPLICA_OF_SOURCE", "Status": "COMPLETED"},
                            {"Name": "CONFIGURE_BACKUPS", "Status": "COMPLETED"},
                        ],
                    }
                ]
            },
            expected_params={"BlueGreenDeploymentIdentifier": "bgd-abc123"},
        )

        result = wrapper.describe_blue_green_deployment("bgd-abc123")
        assert result["BlueGreenDeploymentIdentifier"] == "bgd-abc123"
        assert result["Status"] == "AVAILABLE"

    def test_describe_blue_green_deployment_not_found(self, wrapper, rds_stubber):
        rds_stubber.add_client_error(
            "describe_blue_green_deployments",
            service_error_code="BlueGreenDeploymentNotFoundFault",
            service_message="Blue/Green Deployment not found.",
            expected_params={
                "BlueGreenDeploymentIdentifier": "bgd-nonexistent"
            },
        )

        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_blue_green_deployment("bgd-nonexistent")
        assert (
            exc_info.value.response["Error"]["Code"]
            == "BlueGreenDeploymentNotFoundFault"
        )


# ---------------------------------------------------------------------------
# switchover_blue_green_deployment
# ---------------------------------------------------------------------------

class TestSwitchoverBlueGreenDeployment:
    """Tests for switchover_blue_green_deployment wrapper method."""

    def test_switchover_success(self, wrapper, rds_stubber):
        rds_stubber.add_response(
            "switchover_blue_green_deployment",
            {
                "BlueGreenDeployment": {
                    "BlueGreenDeploymentIdentifier": "bgd-abc123",
                    "BlueGreenDeploymentName": "test-migration",
                    "Status": "SWITCHOVER_IN_PROGRESS",
                    "Source": "arn:aws:rds:us-east-1:123456789012:db:test-db",
                    "Target": "arn:aws:rds:us-east-1:123456789012:db:test-db-green-xyz",
                    "SwitchoverDetails": list(),
                    "Tasks": list(),
                }
            },
            expected_params={
                "BlueGreenDeploymentIdentifier": "bgd-abc123",
                "SwitchoverTimeout": 300,
            },
        )

        result = wrapper.switchover_blue_green_deployment("bgd-abc123")
        assert result["Status"] == "SWITCHOVER_IN_PROGRESS"

    def test_switchover_invalid_state(self, wrapper, rds_stubber):
        rds_stubber.add_client_error(
            "switchover_blue_green_deployment",
            service_error_code="InvalidBlueGreenDeploymentStateFault",
            service_message="Deployment cannot be switched over.",
            expected_params={
                "BlueGreenDeploymentIdentifier": "bgd-abc123",
                "SwitchoverTimeout": 300,
            },
        )

        with pytest.raises(ClientError) as exc_info:
            wrapper.switchover_blue_green_deployment("bgd-abc123")
        assert (
            exc_info.value.response["Error"]["Code"]
            == "InvalidBlueGreenDeploymentStateFault"
        )


# ---------------------------------------------------------------------------
# delete_blue_green_deployment
# ---------------------------------------------------------------------------

class TestDeleteBlueGreenDeployment:
    """Tests for delete_blue_green_deployment wrapper method."""

    def test_delete_success(self, wrapper, rds_stubber):
        rds_stubber.add_response(
            "delete_blue_green_deployment",
            {
                "BlueGreenDeployment": {
                    "BlueGreenDeploymentIdentifier": "bgd-abc123",
                    "BlueGreenDeploymentName": "test-migration",
                    "Status": "DELETING",
                    "Source": "arn:aws:rds:us-east-1:123456789012:db:test-db-old1",
                    "Target": "arn:aws:rds:us-east-1:123456789012:db:test-db",
                    "SwitchoverDetails": list(),
                    "Tasks": list(),
                }
            },
            expected_params={
                "BlueGreenDeploymentIdentifier": "bgd-abc123",
                "DeleteTarget": False,
            },
        )

        result = wrapper.delete_blue_green_deployment("bgd-abc123")
        assert result["Status"] == "DELETING"

    def test_delete_invalid_state(self, wrapper, rds_stubber):
        rds_stubber.add_client_error(
            "delete_blue_green_deployment",
            service_error_code="InvalidBlueGreenDeploymentStateFault",
            service_message="Deployment cannot be deleted.",
            expected_params={
                "BlueGreenDeploymentIdentifier": "bgd-abc123",
                "DeleteTarget": False,
            },
        )

        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_blue_green_deployment("bgd-abc123")
        assert (
            exc_info.value.response["Error"]["Code"]
            == "InvalidBlueGreenDeploymentStateFault"
        )


# ---------------------------------------------------------------------------
# delete_db_instance
# ---------------------------------------------------------------------------

class TestDeleteDbInstance:
    """Tests for delete_db_instance wrapper method."""

    def test_delete_db_instance_success(self, wrapper, rds_stubber):
        rds_stubber.add_response(
            "delete_db_instance",
            {
                "DBInstance": {
                    "DBInstanceIdentifier": "test-db-old1",
                    "DBInstanceStatus": "deleting",
                    "Engine": "mysql",
                }
            },
            expected_params={
                "DBInstanceIdentifier": "test-db-old1",
                "SkipFinalSnapshot": True,
                "DeleteAutomatedBackups": True,
            },
        )

        result = wrapper.delete_db_instance("test-db-old1")
        assert result["DBInstanceStatus"] == "deleting"

    def test_delete_db_instance_not_found(self, wrapper, rds_stubber):
        rds_stubber.add_client_error(
            "delete_db_instance",
            service_error_code="DBInstanceNotFoundFault",
            service_message="DB instance not found.",
            expected_params={
                "DBInstanceIdentifier": "nonexistent-db",
                "SkipFinalSnapshot": True,
                "DeleteAutomatedBackups": True,
            },
        )

        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_db_instance("nonexistent-db")
        assert exc_info.value.response["Error"]["Code"] == "DBInstanceNotFoundFault"
