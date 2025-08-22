# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""Unit tests for S3 batch operations using service method stubbing patterns."""

from botocore.exceptions import ClientError
import pytest


class MockManager:
    def __init__(self, stub_runner, scenario_data, input_mocker):
        self.scenario_data = scenario_data
        self.stub_runner = stub_runner
        self.account_id = "123456789012"
        self.bucket_name = "test-batch-bucket"
        self.job_id = "test-job-123"
        self.role_arn = "arn:aws:iam::123456789012:role/S3BatchRole"
        self.manifest_location = f"arn:aws:s3:::{self.bucket_name}/job-manifest.csv"
        self.etag = "test-etag-123"
        self.file_names = ["job-manifest.csv", "object-key-1.txt", "object-key-2.txt"]
        
        # Mock user inputs
        answers = ["y", "n", "y"]  # yes to proceed, no to cancel, yes to cleanup
        input_mocker.mock_answers(answers)

    def setup_stubs(self, error, stop_on, stubber):
        with self.stub_runner(error, stop_on) as runner:
            runner.add(stubber.stub_get_caller_identity, self.account_id)
            runner.add(stubber.stub_create_bucket, self.bucket_name)
            runner.add(stubber.stub_put_object, self.bucket_name, "object-key-1.txt")
            runner.add(stubber.stub_put_object, self.bucket_name, "object-key-2.txt")
            runner.add(stubber.stub_put_object, self.bucket_name, "job-manifest.csv", etag=self.etag)
            runner.add(stubber.stub_head_object, self.bucket_name, "job-manifest.csv", etag=self.etag)
            runner.add(stubber.stub_create_job, self.account_id, self.job_id)
            runner.add(stubber.stub_describe_job, self.account_id, self.job_id, status="Suspended")
            runner.add(stubber.stub_describe_job, self.account_id, self.job_id, status="Suspended")
            runner.add(stubber.stub_update_job_priority, self.account_id, self.job_id)
            runner.add(stubber.stub_update_job_status, self.account_id, self.job_id, "Ready")
            runner.add(stubber.stub_describe_job, self.account_id, self.job_id, status="Ready")
            runner.add(stubber.stub_get_job_tagging, self.account_id, self.job_id, tags=[])
            runner.add(stubber.stub_put_job_tagging, self.account_id, self.job_id)
            runner.add(stubber.stub_list_jobs, self.account_id, [{"JobId": self.job_id, "Priority": 60}])
            runner.add(stubber.stub_delete_job_tagging, self.account_id, self.job_id)

    def setup_cleanup_stubs(self, stubber):
        with self.stub_runner(None, None) as runner:
            for file_name in self.file_names:
                runner.add(stubber.stub_delete_object, self.bucket_name, file_name)
            runner.add(stubber.stub_list_objects_v2, self.bucket_name, prefix="batch-op-reports/", contents=[])
            runner.add(stubber.stub_delete_bucket, self.bucket_name)


@pytest.fixture
def mock_mgr(stub_runner, scenario_data, input_mocker):
    return MockManager(stub_runner, scenario_data, input_mocker)


def test_get_account_id(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, 0, mock_mgr.scenario_data.stubber)
    
    account_id = mock_mgr.scenario_data.wrapper.get_account_id()
    
    assert account_id == mock_mgr.account_id


def test_create_bucket(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, 1, mock_mgr.scenario_data.stubber)
    
    mock_mgr.scenario_data.wrapper.create_bucket(mock_mgr.bucket_name)
    
    capt = capsys.readouterr()
    assert f"Created bucket: {mock_mgr.bucket_name}" in capt.out


def test_upload_files_to_bucket(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, 4, mock_mgr.scenario_data.stubber)
    
    etag = mock_mgr.scenario_data.wrapper.upload_files_to_bucket(
        mock_mgr.bucket_name, mock_mgr.file_names
    )
    
    assert etag == mock_mgr.etag
    capt = capsys.readouterr()
    assert "Uploaded manifest file" in capt.out


def test_create_s3_batch_job(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, 6, mock_mgr.scenario_data.stubber)
    
    job_id = mock_mgr.scenario_data.wrapper.create_s3_batch_job(
        mock_mgr.account_id,
        mock_mgr.role_arn,
        mock_mgr.manifest_location,
        f"arn:aws:s3:::{mock_mgr.bucket_name}"
    )
    
    assert job_id == mock_mgr.job_id
    capt = capsys.readouterr()
    assert f"The Job id is {mock_mgr.job_id}" in capt.out


def test_check_job_failure_reasons(mock_mgr):
    mock_mgr.setup_stubs(None, 7, mock_mgr.scenario_data.stubber)
    
    reasons = mock_mgr.scenario_data.wrapper.check_job_failure_reasons(
        mock_mgr.job_id, mock_mgr.account_id
    )
    
    assert reasons == []


def test_update_job_priority(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, 10, mock_mgr.scenario_data.stubber)
    
    mock_mgr.scenario_data.wrapper.update_job_priority(
        mock_mgr.job_id, mock_mgr.account_id
    )
    
    capt = capsys.readouterr()
    assert "The job priority was updated" in capt.out


def test_describe_job_details(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, 11, mock_mgr.scenario_data.stubber)
    
    mock_mgr.scenario_data.wrapper.describe_job_details(
        mock_mgr.job_id, mock_mgr.account_id
    )
    
    capt = capsys.readouterr()
    assert f"Job ID: {mock_mgr.job_id}" in capt.out


def test_get_job_tags(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, 12, mock_mgr.scenario_data.stubber)
    
    mock_mgr.scenario_data.wrapper.get_job_tags(
        mock_mgr.job_id, mock_mgr.account_id
    )
    
    capt = capsys.readouterr()
    assert f"No tags found for job ID: {mock_mgr.job_id}" in capt.out


def test_put_job_tags(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, 13, mock_mgr.scenario_data.stubber)
    
    mock_mgr.scenario_data.wrapper.put_job_tags(
        mock_mgr.job_id, mock_mgr.account_id
    )
    
    capt = capsys.readouterr()
    assert f"Additional tags were added to job {mock_mgr.job_id}" in capt.out


def test_list_jobs(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, 14, mock_mgr.scenario_data.stubber)
    
    mock_mgr.scenario_data.wrapper.list_jobs(mock_mgr.account_id)
    
    capt = capsys.readouterr()
    assert f"The job id is {mock_mgr.job_id}" in capt.out
    assert "The job priority is 60" in capt.out


def test_delete_job_tags(mock_mgr, capsys):
    mock_mgr.setup_stubs(None, 15, mock_mgr.scenario_data.stubber)
    
    mock_mgr.scenario_data.wrapper.delete_job_tags(
        mock_mgr.job_id, mock_mgr.account_id
    )
    
    capt = capsys.readouterr()
    assert f"You have successfully deleted {mock_mgr.job_id} tagging." in capt.out


def test_cleanup_resources(mock_mgr, capsys):
    mock_mgr.setup_cleanup_stubs(mock_mgr.scenario_data.stubber)
    
    mock_mgr.scenario_data.wrapper.cleanup_resources(
        mock_mgr.bucket_name, mock_mgr.file_names
    )
    
    capt = capsys.readouterr()
    assert f"Deleted bucket {mock_mgr.bucket_name}" in capt.out


@pytest.mark.parametrize(
    "error, stop_on_index",
    [
        ("TESTERROR-stub_get_caller_identity", 0),
        ("TESTERROR-stub_create_bucket", 1),
        ("TESTERROR-stub_create_job", 6),
        ("TESTERROR-stub_update_job_priority", 9),
    ],
)
def test_wrapper_errors(mock_mgr, caplog, error, stop_on_index):
    mock_mgr.setup_stubs(error, stop_on_index, mock_mgr.scenario_data.stubber)

    with pytest.raises(ClientError) as exc_info:
        if "get_caller_identity" in error:
            mock_mgr.scenario_data.wrapper.get_account_id()
        elif "create_bucket" in error:
            mock_mgr.scenario_data.wrapper.create_bucket(mock_mgr.bucket_name)
        elif "create_job" in error:
            mock_mgr.scenario_data.wrapper.create_s3_batch_job(
                mock_mgr.account_id,
                mock_mgr.role_arn,
                mock_mgr.manifest_location,
                f"arn:aws:s3:::{mock_mgr.bucket_name}"
            )
        elif "update_job_priority" in error:
            mock_mgr.scenario_data.wrapper.update_job_priority(
                mock_mgr.job_id, mock_mgr.account_id
            )
    
    assert exc_info.value.response["Error"]["Code"] == error
    assert error in caplog.text