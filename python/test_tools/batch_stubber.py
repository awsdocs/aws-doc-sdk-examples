# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Batch unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from test_tools.example_stubber import ExampleStubber


class BatchStubber(ExampleStubber):
    """
    A class that implements stub functions used by AWS Batch unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, batch_client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param batch_client: A Boto 3 AWS Batch client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(batch_client, use_stubs)

    def stub_describe_compute_environments(
        self, compute_environments, names=None, error_code=None
    ):
        """Stub the describe_compute_environments function."""
        expected_params = {}
        if names is not None:
            expected_params["computeEnvironments"] = names
        response = {"computeEnvironments": compute_environments}
        self._stub_bifurcator(
            "describe_compute_environments",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_create_compute_environment(
        self,
        compute_environment_name,
        subnet_ids,
        security_group_ids,
        compute_environment_arn,
        max_vcpus=4,
        error_code=None,
    ):
        """Stub the create_compute_environment function."""
        expected_params = {
            "computeEnvironmentName": compute_environment_name,
            "type": "MANAGED",
            "state": "ENABLED",
            "computeResources": {
                "type": "FARGATE",
                "maxvCpus": max_vcpus,
                "subnets": subnet_ids,
                "securityGroupIds": security_group_ids,
            },
        }
        response = {
            "computeEnvironmentName": compute_environment_name,
            "computeEnvironmentArn": compute_environment_arn,
        }
        self._stub_bifurcator(
            "create_compute_environment",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_create_job_queue(
        self,
        job_queue_name,
        compute_environment_name,
        job_queue_arn,
        priority=1,
        error_code=None,
    ):
        """Stub the create_job_queue function."""
        expected_params = {
            "jobQueueName": job_queue_name,
            "state": "ENABLED",
            "priority": priority,
            "computeEnvironmentOrder": [
                {"order": 1, "computeEnvironment": compute_environment_name}
            ],
        }
        response = {"jobQueueName": job_queue_name, "jobQueueArn": job_queue_arn}
        self._stub_bifurcator(
            "create_job_queue", expected_params, response, error_code=error_code
        )

    def stub_register_job_definition(
        self,
        job_definition_name,
        job_definition_arn,
        revision=1,
        image="public.ecr.aws/amazonlinux/amazonlinux:2023",
        command=None,
        vcpus="0.25",
        memory="512",
        error_code=None,
    ):
        """Stub the register_job_definition function."""
        if command is None:
            command = ["echo", "Hello from AWS Batch!"]
        expected_params = {
            "jobDefinitionName": job_definition_name,
            "type": "container",
            "containerProperties": {
                "image": image,
                "command": command,
                "resourceRequirements": [
                    {"type": "VCPU", "value": vcpus},
                    {"type": "MEMORY", "value": memory},
                ],
                "networkConfiguration": {"assignPublicIp": "ENABLED"},
                "fargatePlatformConfiguration": {"platformVersion": "LATEST"},
            },
        }
        response = {
            "jobDefinitionName": job_definition_name,
            "jobDefinitionArn": job_definition_arn,
            "revision": revision,
        }
        self._stub_bifurcator(
            "register_job_definition",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_submit_job(
        self,
        job_name,
        job_queue,
        job_definition,
        job_id,
        job_arn,
        error_code=None,
    ):
        """Stub the submit_job function."""
        expected_params = {
            "jobName": job_name,
            "jobQueue": job_queue,
            "jobDefinition": job_definition,
        }
        response = {"jobName": job_name, "jobId": job_id, "jobArn": job_arn}
        self._stub_bifurcator(
            "submit_job", expected_params, response, error_code=error_code
        )

    def stub_describe_jobs(self, job_ids, jobs, error_code=None):
        """Stub the describe_jobs function."""
        expected_params = {"jobs": job_ids}
        response = {"jobs": jobs}
        self._stub_bifurcator(
            "describe_jobs", expected_params, response, error_code=error_code
        )

    def stub_list_jobs(self, job_queue, job_status, job_summaries, error_code=None):
        """Stub the list_jobs function (used by the paginator)."""
        expected_params = {"jobQueue": job_queue, "jobStatus": job_status}
        response = {"jobSummaryList": job_summaries}
        self._stub_bifurcator(
            "list_jobs", expected_params, response, error_code=error_code
        )

    def stub_deregister_job_definition(self, job_definition, error_code=None):
        """Stub the deregister_job_definition function."""
        expected_params = {"jobDefinition": job_definition}
        response = {}
        self._stub_bifurcator(
            "deregister_job_definition",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_update_job_queue(
        self, job_queue, state, job_queue_name, job_queue_arn, error_code=None
    ):
        """Stub the update_job_queue function."""
        expected_params = {"jobQueue": job_queue, "state": state}
        response = {"jobQueueName": job_queue_name, "jobQueueArn": job_queue_arn}
        self._stub_bifurcator(
            "update_job_queue", expected_params, response, error_code=error_code
        )

    def stub_delete_job_queue(self, job_queue, error_code=None):
        """Stub the delete_job_queue function."""
        expected_params = {"jobQueue": job_queue}
        response = {}
        self._stub_bifurcator(
            "delete_job_queue", expected_params, response, error_code=error_code
        )

    def stub_delete_compute_environment(self, compute_environment, error_code=None):
        """Stub the delete_compute_environment function."""
        expected_params = {"computeEnvironment": compute_environment}
        response = {}
        self._stub_bifurcator(
            "delete_compute_environment",
            expected_params,
            response,
            error_code=error_code,
        )
