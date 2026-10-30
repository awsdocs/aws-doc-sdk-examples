# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the run method in rds_blue_green_encryption_migration_scenario.py.

The CloudFormation stack that creates the KMS keys is stubbed with the
CloudFormation stubber, so no KMS key ARNs are entered by the user. The only
input is the y/n answer to the cleanup question.
"""

import pytest
from botocore.exceptions import ClientError, WaiterError


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
        self.stack_name = "RdsBlueGreenKmsKeysStack"
        self.stack_id = (
            f"arn:aws:cloudformation:us-east-1:{self.account}:stack/"
            f"{self.stack_name}/11111111-2222-3333-4444-555555555555"
        )
        with open(
            scenario_data.cfn_helper.template_path, encoding="utf-8"
        ) as template_file:
            self.template_body = template_file.read()
        self.outputs = [
            {"OutputKey": "OriginalKmsKeyArn", "OutputValue": self.original_kms},
            {"OutputKey": "TargetKmsKeyArn", "OutputValue": self.target_kms},
        ]
        self.scenario_args = {
            "db_instance_identifier": self.db_id,
            "deployment_name": self.bgd_name,
            "master_user_password": self.password,
        }
        self.stub_runner = stub_runner

    def add_setup_stubs(self, runner, cfn_stubber, stack_exists=False, outputs=None):
        """Adds the Setup stubs: deploy the stack and read its outputs."""
        if stack_exists:
            runner.add(cfn_stubber.stub_describe_stacks, self.stack_name, "CREATE_COMPLETE")
        else:
            # The stack doesn't exist yet.
            runner.add(
                cfn_stubber.stub_describe_stacks,
                self.stack_name,
                "CREATE_COMPLETE",
                error_code="ValidationError",
            )
            runner.add(
                cfn_stubber.stub_create_stack,
                self.stack_name,
                self.template_body,
                ["CAPABILITY_IAM"],
                self.stack_id,
            )
            # The stack_create_complete waiter.
            runner.add(
                cfn_stubber.stub_describe_stacks, self.stack_name, "CREATE_COMPLETE"
            )
        runner.add(
            cfn_stubber.stub_describe_stacks,
            self.stack_name,
            "CREATE_COMPLETE",
            outputs=self.outputs if outputs is None else outputs,
        )

    def add_destroy_stubs(self, runner, cfn_stubber):
        """Adds the stubs that delete the stack and wait for the deletion."""
        runner.add(cfn_stubber.stub_delete_stack, self.stack_name)
        runner.add(cfn_stubber.stub_describe_stacks, self.stack_name, "DELETE_COMPLETE")

    def setup_stubs(
        self,
        error,
        stop_on,
        stubber,
        cfn_stubber,
        include_cleanup=True,
        stack_exists=False,
    ):
        with self.stub_runner(error, stop_on) as runner:
            # Setup (indexes 0-3 when the stack is new): deploy the stack and
            # read the KMS key ARNs. Indexes below assume a new stack.
            self.add_setup_stubs(runner, cfn_stubber, stack_exists=stack_exists)
            # Steps 1-2 (indexes 4-6): create and verify the source DB instance.
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
            # Steps 3-5 (indexes 7-9): create the deployment and verify green.
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
            # Steps 6-7 (indexes 10-12): switch over and verify production.
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
            if include_cleanup:
                # Cleanup RDS resources (indexes 13-17).
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
                # Cleanup the CloudFormation stack (indexes 18-19).
                self.add_destroy_stubs(runner, cfn_stubber)

    def setup_failure_stubs(self, include_destroy):
        """Adds stubs for a run that fails when the DB instance is created."""
        stubber = self.scenario_data.stubber
        cfn_stubber = self.scenario_data.cf_stubber
        with self.stub_runner(None, None) as runner:
            self.add_setup_stubs(runner, cfn_stubber)
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
                error_code="TESTERROR-create",
            )
            if include_destroy:
                self.add_destroy_stubs(runner, cfn_stubber)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data):
    return MockManager(stub_runner, scenario_data)


def test_run_scenario(mock_mgr, input_mocker, capsys):
    mock_mgr.setup_stubs(
        None, None, mock_mgr.scenario_data.stubber, mock_mgr.scenario_data.cf_stubber
    )
    input_mocker.mock_answers(["y"])

    mock_mgr.scenario_data.scenario.run(**mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert "SUCCESS: KMS encryption key migration completed!" in capt.out
    assert mock_mgr.original_kms in capt.out
    assert mock_mgr.target_kms in capt.out
    assert "Setup complete." in capt.out
    assert "7-day pending window" in capt.out
    assert "Cleanup complete." in capt.out


def test_run_scenario_keep_resources(mock_mgr, input_mocker, capsys):
    """Answering n deletes nothing and lists the resources that were kept."""
    mock_mgr.setup_stubs(
        None,
        None,
        mock_mgr.scenario_data.stubber,
        mock_mgr.scenario_data.cf_stubber,
        include_cleanup=False,
    )
    input_mocker.mock_answers(["n"])

    mock_mgr.scenario_data.scenario.run(**mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert "SUCCESS: KMS encryption key migration completed!" in capt.out
    assert "Resources were not deleted." in capt.out
    assert mock_mgr.stack_name in capt.out
    assert mock_mgr.db_id in capt.out
    assert mock_mgr.old_blue_id in capt.out
    assert mock_mgr.bgd_id in capt.out
    assert "aws cloudformation delete-stack" in capt.out


def test_run_scenario_existing_stack(mock_mgr, input_mocker, capsys):
    """An existing stack is reused instead of created again."""
    mock_mgr.setup_stubs(
        None,
        None,
        mock_mgr.scenario_data.stubber,
        mock_mgr.scenario_data.cf_stubber,
        stack_exists=True,
    )
    input_mocker.mock_answers(["y"])

    mock_mgr.scenario_data.scenario.run(**mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert "already exists" in capt.out
    assert "SUCCESS: KMS encryption key migration completed!" in capt.out
    assert "Cleanup complete." in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index, expected_exc",
    [
        ("TESTERROR-stub_describe_stacks", 0, ClientError),
        ("TESTERROR-stub_create_stack", 1, ClientError),
        ("TESTERROR-stub_describe_stacks", 2, WaiterError),
        ("TESTERROR-stub_describe_stacks", 3, ClientError),
        ("TESTERROR-stub_create_db_instance", 4, ClientError),
        ("TESTERROR-stub_describe_db_instances", 5, ClientError),
        ("TESTERROR-stub_describe_db_instances", 6, ClientError),
        ("TESTERROR-stub_create_blue_green_deployment", 7, ClientError),
        ("TESTERROR-stub_describe_blue_green_deployments", 8, ClientError),
        ("TESTERROR-stub_describe_db_instances", 9, ClientError),
        ("TESTERROR-stub_switchover_blue_green_deployment", 10, ClientError),
        ("TESTERROR-stub_describe_blue_green_deployments", 11, ClientError),
        ("TESTERROR-stub_describe_db_instances", 12, ClientError),
    ],
)
def test_run_scenario_error(
    mock_mgr, input_mocker, caplog, error, stop_on_index, expected_exc
):
    mock_mgr.setup_stubs(
        error,
        stop_on_index,
        mock_mgr.scenario_data.stubber,
        mock_mgr.scenario_data.cf_stubber,
    )
    input_mocker.mock_answers(["y"])

    with pytest.raises(expected_exc) as exc_info:
        mock_mgr.scenario_data.scenario.run(**mock_mgr.scenario_args)

    if expected_exc is ClientError:
        assert exc_info.value.response["Error"]["Code"] == error
    assert error in caplog.text


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_delete_blue_green_deployment", 13),
        ("TESTERROR-stub_delete_db_instance", 14),
        ("TESTERROR-stub_delete_db_instance", 16),
    ],
)
def test_run_scenario_cleanup_error(
    mock_mgr, input_mocker, caplog, capsys, error, stop_on_index
):
    """
    RDS cleanup errors are logged and don't stop the scenario from finishing.
    The stack is kept because RDS resources might still use its KMS keys.
    """
    mock_mgr.setup_stubs(
        error,
        stop_on_index,
        mock_mgr.scenario_data.stubber,
        mock_mgr.scenario_data.cf_stubber,
    )
    input_mocker.mock_answers(["y"])

    mock_mgr.scenario_data.scenario.run(**mock_mgr.scenario_args)

    capt = capsys.readouterr()
    assert error in caplog.text
    assert f"CloudFormation stack '{mock_mgr.stack_name}' was kept" in capt.out
    assert "Cleanup complete." in capt.out


def test_run_scenario_failure_cleanup_yes(mock_mgr, input_mocker, capsys):
    """After a failure, answering y still deletes the stack."""
    mock_mgr.setup_failure_stubs(include_destroy=True)
    input_mocker.mock_answers(["y"])

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.run(**mock_mgr.scenario_args)

    assert exc_info.value.response["Error"]["Code"] == "TESTERROR-create"
    capt = capsys.readouterr()
    assert "CloudFormation stack deleted" in capt.out
    assert "scheduled for deletion" in capt.out
    assert "Cleanup complete." in capt.out


def test_run_scenario_failure_cleanup_no(mock_mgr, input_mocker, capsys):
    """After a failure, answering n deletes nothing and lists the stack."""
    mock_mgr.setup_failure_stubs(include_destroy=False)
    input_mocker.mock_answers(["n"])

    with pytest.raises(ClientError) as exc_info:
        mock_mgr.scenario_data.scenario.run(**mock_mgr.scenario_args)

    assert exc_info.value.response["Error"]["Code"] == "TESTERROR-create"
    capt = capsys.readouterr()
    assert mock_mgr.stack_name in capt.out
    assert mock_mgr.original_kms in capt.out
    assert "Resources were not deleted." in capt.out


def test_run_scenario_missing_outputs(mock_mgr, input_mocker, capsys):
    """A stack without the expected outputs fails Setup and is cleaned up."""
    cfn_stubber = mock_mgr.scenario_data.cf_stubber
    with mock_mgr.stub_runner(None, None) as runner:
        mock_mgr.add_setup_stubs(runner, cfn_stubber, outputs=[])
        mock_mgr.add_destroy_stubs(runner, cfn_stubber)
    input_mocker.mock_answers(["y"])

    with pytest.raises(RuntimeError) as exc_info:
        mock_mgr.scenario_data.scenario.run(**mock_mgr.scenario_args)

    assert "OriginalKmsKeyArn" in str(exc_info.value)
    capt = capsys.readouterr()
    assert "CloudFormation stack deleted" in capt.out
