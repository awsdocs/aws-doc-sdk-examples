# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the run method in rds_blue_green_encryption_migration_scenario.py.
"""

import pytest
from botocore.exceptions import ClientError


class MockManager:
    def __init__(self, stub_runner, scenario_data):
        self.scenario_data = scenario_data
        self.account = "123456789012"
        self.db_id = "test-db"
        self.db_arn = f"arn:aws:rds:us-east-1:{self.account}:db:{self.db_id}"
        self.green_id = "test-db-green-abc123"
        self.green_arn = f"arn:aws:rds:us-east-1:{self.account}:db:{self.green_id}"
        self.old_blue_id = "test-db-old1"
        self.old_blue_arn = (
            f"arn:aws:rds:us-east-1:{self.account}:db:{self.old_blue_id}"
        )
        self.original_kms = f"arn:aws:kms:us-east-1:{self.account}:key/original-key"
        self.target_kms = f"arn:aws:kms:us-east-1:{self.account}:key/target-key"
        self.bgd_id = "bgd-abc123"
        self.bgd_name = "test-migration"
        self.password = "test-password"
        self.scenario_args = {
            "original_kms_key_id": self.original_kms,
            "target_kms_key_id": self.target_kms,
            "db_instance_identifier": self.db_id,
            "deployment_name": self.bgd_name,
            "master_user_password": self.password,
        }
        self.stub_runner = stub_runner

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            # Steps 1-2: Create and verify the encrypted source DB instance.
            runner.add(
                stubber.stub_create_db_instance,
                self.db_id,
                "mysql",
                "db.t3.micro",
                allocated_storage=20,
                admin_name="admin",
                admin_password=self.password,
                storage_encrypted=True,
                kms_key_id=self.original_kms,
                backup_retention_period=1,
            )
            runner.add(stubber.stub_describe_db_instances, self.db_id)
            runner.add(
                stubber.stub_describe_db_instances,
                self.db_id,
                instance_details={
                    "DBInstanceArn": self.db_arn,
                    "StorageEncrypted": True,
                    "KmsKeyId": self.original_kms,
                },
            )
            # Steps 3-5: Create the Blue/Green Deployment and verify green.
            runner.add(
                stubber.stub_create_blue_green_deployment,
                self.bgd_name,
                self.db_arn,
                self.target_kms,
                self.bgd_id,
            )
            runner.add(
                stubber.stub_describe_blue_green_deployments,
                self.bgd_id,
                self.bgd_name,
                "AVAILABLE",
                source_arn=self.db_arn,
                target_arn=self.green_arn,
            )
            runner.add(
                stubber.stub_describe_db_instances,
                self.green_id,
                instance_details={
                    "StorageEncrypted": True,
                    "KmsKeyId": self.target_kms,
                },
            )
            # Steps 6-7: Switch over and verify the new production instance.
            runner.add(
                stubber.stub_switchover_blue_green_deployment,
                self.bgd_id,
                self.bgd_name,
                600,
            )
            runner.add(
                stubber.stub_describe_blue_green_deployments,
                self.bgd_id,
                self.bgd_name,
                "SWITCHOVER_COMPLETED",
                source_arn=self.old_blue_arn,
                target_arn=self.db_arn,
                switchover_details=[
                    {
                        "SourceMember": self.old_blue_arn,
                        "TargetMember": self.db_arn,
                        "Status": "SWITCHOVER_COMPLETED",
                    }
                ],
            )
            runner.add(
                stubber.stub_describe_db_instances,
                self.db_id,
                instance_details={
                    "StorageEncrypted": True,
                    "KmsKeyId": self.target_kms,
                },
            )
            # Cleanup.
            runner.add(
                stubber.stub_delete_blue_green_deployment,
                self.bgd_id,
                self.bgd_name,
                False,
            )
            runner.add(stubber.stub_delete_db_instance, self.old_blue_id)
            runner.add(
                stubber.stub_describe_db_instances,
                self.old_blue_id,
                error_code="DBInstanceNotFoundFault",
            )
            runner.add(stubber.stub_delete_db_instance, self.db_id)
            runner.add(
                stubber.stub_describe_db_instances,
                self.db_id,
                error_code="DBInstanceNotFoundFault",
            )


@pytest.fixture
def mock_mgr(stub_runner, scenario_data):
    return MockManager(stub_runner, scenario_data)


def test_run_scenario(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, None, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.run(**mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert "SUCCESS: KMS encryption key migration completed!" in capt.out
    assert mock_mgr.target_kms in capt.out
    assert "Cleanup complete." in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_create_db_instance", 0),
        ("TESTERROR-stub_describe_db_instances", 1),
        ("TESTERROR-stub_describe_db_instances", 2),
        ("TESTERROR-stub_create_blue_green_deployment", 3),
        ("TESTERROR-stub_describe_blue_green_deployments", 4),
        ("TESTERROR-stub_describe_db_instances", 5),
        ("TESTERROR-stub_switchover_blue_green_deployment", 6),
        ("TESTERROR-stub_describe_blue_green_deployments", 7),
        ("TESTERROR-stub_describe_db_instances", 8),
    ],
)
def test_run_scenario_error(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.run(**mock_mgr.scenario_args)

    assert exc_info.value.response["Error"]["Code"] == error
    assert error in caplog.text


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_delete_blue_green_deployment", 9),
        ("TESTERROR-stub_delete_db_instance", 10),
        ("TESTERROR-stub_delete_db_instance", 12),
    ],
)
def test_run_scenario_cleanup_error(mock_mgr, caplog, error, stop_on_index):
    """Cleanup errors are logged and do not stop the scenario from finishing."""
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    mock_mgr.scenario_data.scenario.run(**mock_mgr.scenario_args)

    assert error in caplog.text
