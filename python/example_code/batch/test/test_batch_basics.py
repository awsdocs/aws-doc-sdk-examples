# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the AWS Batch wrapper and Basics scenario.

Uses the shared ``make_stubber`` and ``stub_runner`` fixtures (backed by the
botocore Stubber) from ``test_tools``. No AWS credentials or resources are
required, and no AWS charges are incurred.
"""

import pytest
from botocore.exceptions import ClientError

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
JD_REVISION = 1
EXECUTION_ROLE_ARN = f"arn:aws:iam::{ACCOUNT_ID}:role/ecsTaskExecutionRole"
JOB_ID = "a1b2c3d4-5678-90ab-cdef-111222333444"
JOB_ARN = f"arn:aws:batch:{REGION}:{ACCOUNT_ID}:job/{JOB_ID}"
JOB_NAME = "batch-basics-hello-job"
SUBNET_IDS = ["subnet-0abc1234", "subnet-0def5678"]
SECURITY_GROUP_IDS = ["sg-0aabbccdd"]

COMPUTE_ENVIRONMENT_VALID = {
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

COMPUTE_ENVIRONMENT_DISABLED = {
    **COMPUTE_ENVIRONMENT_VALID,
    "state": "DISABLED",
    "status": "VALID",
}

JOB_DETAIL_SUCCEEDED = {
    "jobName": JOB_NAME,
    "jobId": JOB_ID,
    "jobArn": JOB_ARN,
    "jobQueue": JQ_ARN,
    "status": "SUCCEEDED",
    "jobDefinition": JD_ARN,
    "createdAt": 1700000000000,
    "startedAt": 1700000010000,
    "stoppedAt": 1700000020000,
    "container": {"exitCode": 0},
}

JOB_SUMMARY_SUCCEEDED = {
    "jobId": JOB_ID,
    "jobName": JOB_NAME,
    "status": "SUCCEEDED",
    "createdAt": 1700000000000,
    "startedAt": 1700000010000,
    "stoppedAt": 1700000020000,
}


# ---------------------------------------------------------------------------
# Unit tests for individual wrapper methods
#
# Each test is parametrized over ``error_code`` so that both the success path
# (``None``) and the ClientError path (``"TestException"``) are exercised,
# matching the convention used across the Python examples.
# ---------------------------------------------------------------------------
@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_compute_environments(scenario_data, error_code):
    scenario_data.batch_stubber.stub_describe_compute_environments(
        [COMPUTE_ENVIRONMENT_VALID], names=[CE_NAME], error_code=error_code
    )
    if error_code is None:
        result = scenario_data.wrapper.describe_compute_environments([CE_NAME])
        assert len(result) == 1
        assert result[0]["computeEnvironmentName"] == CE_NAME
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.describe_compute_environments([CE_NAME])
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_compute_environment(scenario_data, error_code):
    scenario_data.batch_stubber.stub_create_compute_environment(
        CE_NAME, SUBNET_IDS, SECURITY_GROUP_IDS, CE_ARN, error_code=error_code
    )
    if error_code is None:
        result = scenario_data.wrapper.create_compute_environment(
            CE_NAME, SUBNET_IDS, SECURITY_GROUP_IDS
        )
        assert result["computeEnvironmentArn"] == CE_ARN
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.create_compute_environment(
                CE_NAME, SUBNET_IDS, SECURITY_GROUP_IDS
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_job_queue(scenario_data, error_code):
    scenario_data.batch_stubber.stub_create_job_queue(
        JQ_NAME, CE_NAME, JQ_ARN, error_code=error_code
    )
    if error_code is None:
        result = scenario_data.wrapper.create_job_queue(JQ_NAME, CE_NAME)
        assert result["jobQueueArn"] == JQ_ARN
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.create_job_queue(JQ_NAME, CE_NAME)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_register_job_definition(scenario_data, error_code):
    scenario_data.batch_stubber.stub_register_job_definition(
        JD_NAME, JD_ARN, EXECUTION_ROLE_ARN, revision=JD_REVISION, error_code=error_code
    )
    if error_code is None:
        result = scenario_data.wrapper.register_job_definition(
            JD_NAME, EXECUTION_ROLE_ARN
        )
        assert result["revision"] == JD_REVISION
        assert result["jobDefinitionArn"] == JD_ARN
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.register_job_definition(JD_NAME, EXECUTION_ROLE_ARN)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_submit_job(scenario_data, error_code):
    scenario_data.batch_stubber.stub_submit_job(
        JOB_NAME, JQ_NAME, JD_ARN, JOB_ID, JOB_ARN, error_code=error_code
    )
    if error_code is None:
        result = scenario_data.wrapper.submit_job(JOB_NAME, JQ_NAME, JD_ARN)
        assert result["jobId"] == JOB_ID
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.submit_job(JOB_NAME, JQ_NAME, JD_ARN)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_jobs(scenario_data, error_code):
    scenario_data.batch_stubber.stub_describe_jobs(
        [JOB_ID], [JOB_DETAIL_SUCCEEDED], error_code=error_code
    )
    if error_code is None:
        result = scenario_data.wrapper.describe_jobs([JOB_ID])
        assert len(result) == 1
        assert result[0]["status"] == "SUCCEEDED"
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.describe_jobs([JOB_ID])
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_jobs(scenario_data, error_code):
    scenario_data.batch_stubber.stub_list_jobs(
        JQ_NAME, "SUCCEEDED", [JOB_SUMMARY_SUCCEEDED], error_code=error_code
    )
    if error_code is None:
        result = scenario_data.wrapper.list_jobs(JQ_NAME, "SUCCEEDED")
        assert len(result) == 1
        assert result[0]["jobId"] == JOB_ID
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.list_jobs(JQ_NAME, "SUCCEEDED")
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_deregister_job_definition(scenario_data, error_code):
    scenario_data.batch_stubber.stub_deregister_job_definition(
        JD_ARN, error_code=error_code
    )
    if error_code is None:
        scenario_data.wrapper.deregister_job_definition(JD_ARN)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.deregister_job_definition(JD_ARN)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_update_job_queue(scenario_data, error_code):
    scenario_data.batch_stubber.stub_update_job_queue(
        JQ_NAME, "DISABLED", JQ_NAME, JQ_ARN, error_code=error_code
    )
    if error_code is None:
        result = scenario_data.wrapper.update_job_queue(JQ_NAME, "DISABLED")
        assert result["jobQueueName"] == JQ_NAME
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.update_job_queue(JQ_NAME, "DISABLED")
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_update_compute_environment(scenario_data, error_code):
    scenario_data.batch_stubber.stub_update_compute_environment(
        CE_NAME, "DISABLED", error_code=error_code
    )
    if error_code is None:
        result = scenario_data.wrapper.update_compute_environment(CE_NAME, "DISABLED")
        assert result["computeEnvironmentName"] == CE_NAME
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.update_compute_environment(CE_NAME, "DISABLED")
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_job_queue(scenario_data, error_code):
    scenario_data.batch_stubber.stub_delete_job_queue(JQ_NAME, error_code=error_code)
    if error_code is None:
        scenario_data.wrapper.delete_job_queue(JQ_NAME)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.delete_job_queue(JQ_NAME)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_compute_environment(scenario_data, error_code):
    scenario_data.batch_stubber.stub_delete_compute_environment(
        CE_NAME, error_code=error_code
    )
    if error_code is None:
        scenario_data.wrapper.delete_compute_environment(CE_NAME)
    else:
        with pytest.raises(ClientError) as exc_info:
            scenario_data.wrapper.delete_compute_environment(CE_NAME)
        assert exc_info.value.response["Error"]["Code"] == error_code


# ---------------------------------------------------------------------------
# Full scenario test
#
# ``stub_runner`` chains the stubs in call order. The ``error`` / ``stop_on``
# parameters let the test inject a ClientError at a chosen step; here we run
# the happy path (``error=None``) end-to-end through run() + cleanup().
# ---------------------------------------------------------------------------
def test_run_scenario(scenario_data, stub_runner, mock_wait):
    scenario = scenario_data.scenario
    stubber = scenario_data.batch_stubber

    with stub_runner(None, None) as runner:
        # 1. Create compute environment (setup discovers a single subnet)
        runner.add(
            stubber.stub_create_compute_environment,
            CE_NAME,
            [SUBNET_IDS[0]],
            SECURITY_GROUP_IDS,
            CE_ARN,
        )
        # 2. Wait for VALID (describe_compute_environments by name)
        runner.add(
            stubber.stub_describe_compute_environments,
            [COMPUTE_ENVIRONMENT_VALID],
            [CE_NAME],
        )
        # 3. Create job queue
        runner.add(stubber.stub_create_job_queue, JQ_NAME, CE_NAME, JQ_ARN)
        # 3b. Wait for the job queue to become VALID/ENABLED before submitting
        runner.add(stubber.stub_describe_job_queues, JQ_NAME, "ENABLED")
        # 4. Register job definition (execution role is required for Fargate)
        runner.add(
            stubber.stub_register_job_definition,
            JD_NAME,
            JD_ARN,
            EXECUTION_ROLE_ARN,
            JD_REVISION,
        )
        # 5. Submit job (job_definition is "<name>:<revision>")
        runner.add(
            stubber.stub_submit_job,
            JOB_NAME,
            JQ_NAME,
            f"{JD_NAME}:{JD_REVISION}",
            JOB_ID,
            JOB_ARN,
        )
        # 6. Monitor job (describe_jobs — SUCCEEDED)
        runner.add(stubber.stub_describe_jobs, [JOB_ID], [JOB_DETAIL_SUCCEEDED])
        # 7. List jobs (paginator)
        runner.add(
            stubber.stub_list_jobs, JQ_NAME, "SUCCEEDED", [JOB_SUMMARY_SUCCEEDED]
        )
        # -- cleanup() --
        # 8. Deregister job definition
        runner.add(stubber.stub_deregister_job_definition, JD_ARN)
        # 9. Wait for the queue to be VALID/ENABLED before disabling
        runner.add(stubber.stub_describe_job_queues, JQ_NAME, "ENABLED")
        # 10. Disable job queue
        runner.add(stubber.stub_update_job_queue, JQ_NAME, "DISABLED", JQ_NAME, JQ_ARN)
        # 11. Wait for the queue to reach DISABLED/VALID before deletion
        runner.add(stubber.stub_describe_job_queues, JQ_NAME)
        # 12. Delete job queue
        runner.add(stubber.stub_delete_job_queue, JQ_NAME)
        # 13. Wait for the queue to finish deleting
        runner.add(stubber.stub_describe_job_queues, JQ_NAME)
        # 14. Wait for the compute environment to be VALID before disabling
        runner.add(
            stubber.stub_describe_compute_environments,
            [COMPUTE_ENVIRONMENT_VALID],
            [CE_NAME],
        )
        # 15. Disable compute environment
        runner.add(stubber.stub_update_compute_environment, CE_NAME, "DISABLED")
        # 16. Wait for the compute environment to reach DISABLED/VALID
        runner.add(
            stubber.stub_describe_compute_environments,
            [COMPUTE_ENVIRONMENT_DISABLED],
            [CE_NAME],
        )
        # 17. Delete compute environment
        runner.add(stubber.stub_delete_compute_environment, CE_NAME)

    # Configure the mocked IAM/EC2 clients the scenario uses to self-provision.
    scenario_data.ec2_client.describe_vpcs.return_value = {
        "Vpcs": [{"VpcId": "vpc-012345"}]
    }
    scenario_data.ec2_client.describe_subnets.return_value = {
        "Subnets": [{"SubnetId": SUBNET_IDS[0]}]
    }
    scenario_data.ec2_client.describe_security_groups.return_value = {
        "SecurityGroups": [{"GroupId": SECURITY_GROUP_IDS[0]}]
    }
    scenario_data.iam_client.create_role.return_value = {
        "Role": {"Arn": EXECUTION_ROLE_ARN}
    }

    scenario.setup(TIMESTAMP)
    # setup() discovers one subnet; align the Batch stubs that were queued
    # with a single subnet so create_compute_environment matches.
    assert scenario.subnet_ids == [SUBNET_IDS[0]]
    assert scenario.execution_role_arn == EXECUTION_ROLE_ARN
    scenario.run()
    scenario.cleanup()
