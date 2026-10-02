# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for AWS Batch wrapper and scenario.

Uses botocore.stub.Stubber for offline testing — no AWS credentials required.
No CloudFormation calls. No @pytest.mark.integ.
"""

import time
from unittest.mock import patch

import boto3
import pytest
from botocore.stub import Stubber

from batch_wrapper import BatchWrapper
from scenario_batch_basics import BatchScenario


# ---------------------------------------------------------------------------
# Shared test data
# ---------------------------------------------------------------------------
ACCOUNT_ID = "123456789012"
REGION = "us-east-1"
TIMESTAMP = "20240115120000"
CE_NAME = f"batch-basics-fargate-ce-{TIMESTAMP}"
CE_ARN = f"arn:aws:batch:{REGION}:{ACCOUNT_ID}:compute-environment/{CE_NAME}"
JQ_NAME = f"batch-basics-job-queue-{TIMESTAMP}"
JQ_ARN = f"arn:aws:batch:{REGION}:{ACCOUNT_ID}:job-queue/{JQ_NAME}"
JD_NAME = f"batch-basics-job-def-{TIMESTAMP}"
JD_ARN = f"arn:aws:batch:{REGION}:{ACCOUNT_ID}:job-definition/{JD_NAME}:1"
JOB_ID = "a1b2c3d4-5678-90ab-cdef-111222333444"
JOB_ARN = f"arn:aws:batch:{REGION}:{ACCOUNT_ID}:job/{JOB_ID}"
JOB_NAME = "batch-basics-hello-job"
SUBNET_IDS = ["subnet-0abc1234", "subnet-0def5678"]
SECURITY_GROUP_IDS = ["sg-0aabbccdd"]

# Required fields for DescribeJobs JobDetail: jobName, jobId, jobQueue,
# status, jobDefinition. startedAt is required in attempts[].
DESCRIBE_JOBS_SUCCEEDED = {
    "jobs": [
        {
            "jobName": JOB_NAME,
            "jobId": JOB_ID,
            "jobArn": JOB_ARN,
            "jobQueue": JQ_ARN,
            "status": "SUCCEEDED",
            "jobDefinition": JD_ARN,
            "createdAt": 1700000000000,
            "startedAt": 1700000010000,
            "stoppedAt": 1700000020000,
            "container": {
                "exitCode": 0,
                "image": "public.ecr.aws/amazonlinux/amazonlinux:2023",
                "resourceRequirements": [
                    {"type": "VCPU", "value": "0.25"},
                    {"type": "MEMORY", "value": "512"},
                ],
            },
        }
    ]
}

DESCRIBE_CE_VALID = {
    "computeEnvironments": [
        {
            "computeEnvironmentName": CE_NAME,
            "computeEnvironmentArn": CE_ARN,
            "type": "MANAGED",
            "state": "ENABLED",
            "status": "VALID",
            "statusReason": "ComputeEnvironment Healthy",
            "computeResources": {
                "type": "FARGATE",
                "maxvCpus": 4,
                "subnets": SUBNET_IDS,
                "securityGroupIds": SECURITY_GROUP_IDS,
            },
        }
    ]
}

LIST_JOBS_SUCCEEDED = {
    "jobSummaryList": [
        {
            "jobId": JOB_ID,
            "jobName": JOB_NAME,
            "status": "SUCCEEDED",
            "createdAt": 1700000000000,
            "startedAt": 1700000010000,
            "stoppedAt": 1700000020000,
        }
    ]
}


# ---------------------------------------------------------------------------
# Fixtures
# ---------------------------------------------------------------------------
@pytest.fixture
def batch_client():
    """Create a Batch client for testing."""
    return boto3.client("batch", region_name=REGION)


@pytest.fixture
def batch_wrapper(batch_client):
    """Create a BatchWrapper with a test client."""
    return BatchWrapper(batch_client)


# ---------------------------------------------------------------------------
# Unit tests for individual wrapper methods
# ---------------------------------------------------------------------------
class TestDescribeComputeEnvironments:
    """Tests for describe_compute_environments."""

    def test_describe_all(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "describe_compute_environments",
            DESCRIBE_CE_VALID,
            {},
        )
        with stubber:
            result = batch_wrapper.describe_compute_environments()
        assert len(result) == 1
        assert result[0]["computeEnvironmentName"] == CE_NAME

    def test_describe_by_name(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "describe_compute_environments",
            DESCRIBE_CE_VALID,
            {"computeEnvironments": [CE_NAME]},
        )
        with stubber:
            result = batch_wrapper.describe_compute_environments([CE_NAME])
        assert len(result) == 1


class TestCreateComputeEnvironment:
    """Tests for create_compute_environment."""

    def test_create(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "create_compute_environment",
            {
                "computeEnvironmentName": CE_NAME,
                "computeEnvironmentArn": CE_ARN,
            },
            {
                "computeEnvironmentName": CE_NAME,
                "type": "MANAGED",
                "state": "ENABLED",
                "computeResources": {
                    "type": "FARGATE",
                    "maxvCpus": 4,
                    "subnets": SUBNET_IDS,
                    "securityGroupIds": SECURITY_GROUP_IDS,
                },
            },
        )
        with stubber:
            result = batch_wrapper.create_compute_environment(
                CE_NAME, SUBNET_IDS, SECURITY_GROUP_IDS
            )
        assert result["computeEnvironmentArn"] == CE_ARN


class TestCreateJobQueue:
    """Tests for create_job_queue."""

    def test_create(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "create_job_queue",
            {"jobQueueName": JQ_NAME, "jobQueueArn": JQ_ARN},
            {
                "jobQueueName": JQ_NAME,
                "state": "ENABLED",
                "priority": 1,
                "computeEnvironmentOrder": [
                    {"order": 1, "computeEnvironment": CE_NAME}
                ],
            },
        )
        with stubber:
            result = batch_wrapper.create_job_queue(JQ_NAME, CE_NAME)
        assert result["jobQueueArn"] == JQ_ARN


class TestRegisterJobDefinition:
    """Tests for register_job_definition."""

    def test_register(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "register_job_definition",
            {
                "jobDefinitionName": JD_NAME,
                "jobDefinitionArn": JD_ARN,
                "revision": 1,
            },
        )
        with stubber:
            result = batch_wrapper.register_job_definition(JD_NAME)
        assert result["revision"] == 1
        assert result["jobDefinitionArn"] == JD_ARN


class TestSubmitJob:
    """Tests for submit_job."""

    def test_submit(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "submit_job",
            {
                "jobName": JOB_NAME,
                "jobId": JOB_ID,
                "jobArn": JOB_ARN,
            },
        )
        with stubber:
            result = batch_wrapper.submit_job(JOB_NAME, JQ_NAME, JD_ARN)
        assert result["jobId"] == JOB_ID


class TestDescribeJobs:
    """Tests for describe_jobs."""

    def test_describe(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "describe_jobs",
            DESCRIBE_JOBS_SUCCEEDED,
            {"jobs": [JOB_ID]},
        )
        with stubber:
            result = batch_wrapper.describe_jobs([JOB_ID])
        assert len(result) == 1
        assert result[0]["status"] == "SUCCEEDED"


class TestListJobs:
    """Tests for list_jobs (uses paginator)."""

    def test_list(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "list_jobs",
            LIST_JOBS_SUCCEEDED,
            {"jobQueue": JQ_NAME, "jobStatus": "SUCCEEDED"},
        )
        with stubber:
            result = batch_wrapper.list_jobs(JQ_NAME, "SUCCEEDED")
        assert len(result) == 1
        assert result[0]["jobId"] == JOB_ID


class TestDeregisterJobDefinition:
    """Tests for deregister_job_definition."""

    def test_deregister(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "deregister_job_definition",
            {},
            {"jobDefinition": JD_ARN},
        )
        with stubber:
            batch_wrapper.deregister_job_definition(JD_ARN)


class TestUpdateJobQueue:
    """Tests for update_job_queue."""

    def test_update(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "update_job_queue",
            {"jobQueueName": JQ_NAME, "jobQueueArn": JQ_ARN},
            {"jobQueue": JQ_NAME, "state": "DISABLED"},
        )
        with stubber:
            result = batch_wrapper.update_job_queue(JQ_NAME, "DISABLED")
        assert result["jobQueueName"] == JQ_NAME


class TestDeleteJobQueue:
    """Tests for delete_job_queue."""

    def test_delete(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "delete_job_queue",
            {},
            {"jobQueue": JQ_NAME},
        )
        with stubber:
            batch_wrapper.delete_job_queue(JQ_NAME)


class TestDeleteComputeEnvironment:
    """Tests for delete_compute_environment."""

    def test_delete(self, batch_wrapper):
        stubber = Stubber(batch_wrapper.batch_client)
        stubber.add_response(
            "delete_compute_environment",
            {},
            {"computeEnvironment": CE_NAME},
        )
        with stubber:
            batch_wrapper.delete_compute_environment(CE_NAME)


# ---------------------------------------------------------------------------
# Full scenario test
# ---------------------------------------------------------------------------
def test_full_scenario():
    """
    Tests the full BatchScenario end-to-end using Stubber.

    Call order in scenario.run() + scenario.cleanup():
      1. create_compute_environment       (CreateComputeEnvironment)
      2. describe_compute_environments    (wait poll — VALID)
      3. create_job_queue                 (CreateJobQueue)
      4. register_job_definition          (RegisterJobDefinition)
      5. submit_job                       (SubmitJob)
      6. describe_jobs                    (wait poll — SUCCEEDED)
      7. list_jobs                        (ListJobs via paginator)
      -- cleanup() --
      8. deregister_job_definition        (DeregisterJobDefinition)
      9. update_job_queue                 (UpdateJobQueue — DISABLED)
     10. delete_job_queue                 (DeleteJobQueue)
     11. delete_compute_environment       (DeleteComputeEnvironment)
    """
    client = boto3.client("batch", region_name=REGION)
    stubber = Stubber(client)

    # 1. CreateComputeEnvironment
    stubber.add_response(
        "create_compute_environment",
        {
            "computeEnvironmentName": CE_NAME,
            "computeEnvironmentArn": CE_ARN,
        },
    )

    # 2. DescribeComputeEnvironments (wait — returns VALID immediately)
    stubber.add_response(
        "describe_compute_environments",
        DESCRIBE_CE_VALID,
    )

    # 3. CreateJobQueue
    stubber.add_response(
        "create_job_queue",
        {"jobQueueName": JQ_NAME, "jobQueueArn": JQ_ARN},
    )

    # 4. RegisterJobDefinition
    stubber.add_response(
        "register_job_definition",
        {
            "jobDefinitionName": JD_NAME,
            "jobDefinitionArn": JD_ARN,
            "revision": 1,
        },
    )

    # 5. SubmitJob
    stubber.add_response(
        "submit_job",
        {"jobName": JOB_NAME, "jobId": JOB_ID, "jobArn": JOB_ARN},
    )

    # 6. DescribeJobs (wait — returns SUCCEEDED immediately)
    stubber.add_response(
        "describe_jobs",
        DESCRIBE_JOBS_SUCCEEDED,
    )

    # 7. ListJobs (via paginator)
    stubber.add_response(
        "list_jobs",
        LIST_JOBS_SUCCEEDED,
    )

    # -- cleanup() stubs --
    # 8. DeregisterJobDefinition
    stubber.add_response("deregister_job_definition", {})

    # 9. UpdateJobQueue (DISABLED)
    stubber.add_response(
        "update_job_queue",
        {"jobQueueName": JQ_NAME, "jobQueueArn": JQ_ARN},
    )

    # 10. DeleteJobQueue
    stubber.add_response("delete_job_queue", {})

    # 11. DeleteComputeEnvironment
    stubber.add_response("delete_compute_environment", {})

    wrapper = BatchWrapper(client)
    scenario = BatchScenario(wrapper)

    with stubber:
        # Patch time.sleep so we don't actually wait
        with patch("batch_wrapper.time.sleep"):
            scenario.setup(SUBNET_IDS, SECURITY_GROUP_IDS, TIMESTAMP)
            scenario.run()
            scenario.cleanup()

    # Verify all stubs were consumed
    stubber.assert_no_pending_responses()
