# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Wrapper class for AWS Batch operations.

This module provides a wrapper around the AWS Batch SDK client for common
operations including managing compute environments, job queues, job definitions,
and jobs.
"""

import logging
import time
from typing import Any, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.batch.BatchWrapper.decl]
class BatchWrapper:
    """Encapsulates AWS Batch operations."""

    def __init__(self, batch_client: Any) -> None:
        """
        Initializes the BatchWrapper with an AWS Batch client.

        :param batch_client: A Boto3 AWS Batch client.
        """
        self.batch_client = batch_client

    # snippet-end:[python.example_code.batch.BatchWrapper.decl]

    # snippet-start:[python.example_code.batch.DescribeComputeEnvironments]
    def describe_compute_environments(
        self, compute_environment_names: Optional[list] = None
    ) -> list:
        """
        Describes one or more compute environments.

        :param compute_environment_names: Optional list of compute environment names
            or ARNs to describe. If None, describes all compute environments.
        :return: A list of compute environment detail dictionaries.
        """
        try:
            params = dict()
            if compute_environment_names is not None:
                params["computeEnvironments"] = compute_environment_names
            response = self.batch_client.describe_compute_environments(**params)
            environments = response.get("computeEnvironments", list())
            logger.info("Described %d compute environment(s).", len(environments))
            return environments
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error describing compute environments: %s",
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.batch.DescribeComputeEnvironments]

    # snippet-start:[python.example_code.batch.CreateComputeEnvironment]
    def create_compute_environment(
        self,
        compute_environment_name: str,
        subnet_ids: list,
        security_group_ids: list,
        max_vcpus: int = 4,
    ) -> dict:
        """
        Creates a managed Fargate compute environment.

        :param compute_environment_name: The name for the compute environment.
        :param subnet_ids: A list of subnet IDs for the compute resources.
        :param security_group_ids: A list of security group IDs.
        :param max_vcpus: Maximum number of vCPUs (default 4).
        :return: A dictionary with the compute environment name and ARN.
        """
        try:
            response = self.batch_client.create_compute_environment(
                computeEnvironmentName=compute_environment_name,
                type="MANAGED",
                state="ENABLED",
                computeResources={
                    "type": "FARGATE",
                    "maxvCpus": max_vcpus,
                    "subnets": subnet_ids,
                    "securityGroupIds": security_group_ids,
                },
            )
            logger.info(
                "Created compute environment %s: %s",
                response["computeEnvironmentName"],
                response["computeEnvironmentArn"],
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error creating compute environment %s: %s",
                    compute_environment_name,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.batch.CreateComputeEnvironment]

    # snippet-start:[python.example_code.batch.CreateJobQueue]
    def create_job_queue(
        self,
        job_queue_name: str,
        compute_environment_name: str,
        priority: int = 1,
    ) -> dict:
        """
        Creates a job queue associated with a compute environment.

        :param job_queue_name: The name for the job queue.
        :param compute_environment_name: The compute environment to associate.
        :param priority: The priority of the job queue (default 1).
        :return: A dictionary with the job queue name and ARN.
        """
        try:
            response = self.batch_client.create_job_queue(
                jobQueueName=job_queue_name,
                state="ENABLED",
                priority=priority,
                computeEnvironmentOrder=[
                    {
                        "order": 1,
                        "computeEnvironment": compute_environment_name,
                    }
                ],
            )
            logger.info(
                "Created job queue %s: %s",
                response["jobQueueName"],
                response["jobQueueArn"],
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error creating job queue %s: %s",
                    job_queue_name,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.batch.CreateJobQueue]

    # snippet-start:[python.example_code.batch.RegisterJobDefinition]
    def register_job_definition(
        self,
        job_definition_name: str,
        image: str = "public.ecr.aws/amazonlinux/amazonlinux:2023",
        command: Optional[list] = None,
        vcpus: str = "0.25",
        memory: str = "512",
    ) -> dict:
        """
        Registers a Fargate job definition.

        :param job_definition_name: The name for the job definition.
        :param image: The container image to use.
        :param command: The command to run in the container.
        :param vcpus: The number of vCPUs (as a string).
        :param memory: The memory in MiB (as a string).
        :return: A dictionary with the job definition name, ARN, and revision.
        """
        if command is None:
            command = ["echo", "Hello from AWS Batch!"]
        try:
            response = self.batch_client.register_job_definition(
                jobDefinitionName=job_definition_name,
                type="container",
                containerProperties={
                    "image": image,
                    "command": command,
                    "resourceRequirements": [
                        {"type": "VCPU", "value": vcpus},
                        {"type": "MEMORY", "value": memory},
                    ],
                    "networkConfiguration": {"assignPublicIp": "ENABLED"},
                    "fargatePlatformConfiguration": {"platformVersion": "LATEST"},
                },
            )
            logger.info(
                "Registered job definition %s revision %d: %s",
                response["jobDefinitionName"],
                response["revision"],
                response["jobDefinitionArn"],
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error registering job definition %s: %s",
                    job_definition_name,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.batch.RegisterJobDefinition]

    # snippet-start:[python.example_code.batch.SubmitJob]
    def submit_job(
        self, job_name: str, job_queue: str, job_definition: str
    ) -> dict:
        """
        Submits a job to a job queue.

        :param job_name: A descriptive name for the job.
        :param job_queue: The job queue name or ARN.
        :param job_definition: The job definition name:revision or ARN.
        :return: A dictionary with the job name, ID, and ARN.
        """
        try:
            response = self.batch_client.submit_job(
                jobName=job_name,
                jobQueue=job_queue,
                jobDefinition=job_definition,
            )
            logger.info(
                "Submitted job %s (ID: %s): %s",
                response["jobName"],
                response["jobId"],
                response.get("jobArn", ""),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error submitting job %s: %s",
                    job_name,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.batch.SubmitJob]

    # snippet-start:[python.example_code.batch.DescribeJobs]
    def describe_jobs(self, job_ids: list) -> list:
        """
        Describes one or more jobs.

        :param job_ids: A list of job IDs to describe.
        :return: A list of job detail dictionaries.
        """
        try:
            response = self.batch_client.describe_jobs(jobs=job_ids)
            jobs = response.get("jobs", list())
            logger.info("Described %d job(s).", len(jobs))
            return jobs
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error describing jobs: %s",
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.batch.DescribeJobs]

    # snippet-start:[python.example_code.batch.ListJobs]
    def list_jobs(self, job_queue: str, job_status: str = "SUCCEEDED") -> list:
        """
        Lists jobs in a job queue with a specific status.

        :param job_queue: The job queue name or ARN.
        :param job_status: The job status to filter by (default SUCCEEDED).
        :return: A list of job summary dictionaries.
        """
        try:
            paginator = self.batch_client.get_paginator("list_jobs")
            job_summaries = list()
            for page in paginator.paginate(
                jobQueue=job_queue, jobStatus=job_status
            ):
                job_summaries.extend(page.get("jobSummaryList", list()))
            logger.info(
                "Listed %d %s job(s) in queue %s.",
                len(job_summaries),
                job_status,
                job_queue,
            )
            return job_summaries
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error listing jobs in queue %s: %s",
                    job_queue,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.batch.ListJobs]

    # snippet-start:[python.example_code.batch.DeregisterJobDefinition]
    def deregister_job_definition(self, job_definition: str) -> None:
        """
        Deregisters a job definition.

        :param job_definition: The job definition name:revision or ARN.
        """
        try:
            self.batch_client.deregister_job_definition(
                jobDefinition=job_definition
            )
            logger.info("Deregistered job definition %s.", job_definition)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error deregistering job definition %s: %s",
                    job_definition,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.batch.DeregisterJobDefinition]

    # snippet-start:[python.example_code.batch.UpdateJobQueue]
    def update_job_queue(self, job_queue: str, state: str) -> dict:
        """
        Updates a job queue, such as disabling it before deletion.

        :param job_queue: The job queue name or ARN.
        :param state: The new state (ENABLED or DISABLED).
        :return: The response dictionary.
        """
        try:
            response = self.batch_client.update_job_queue(
                jobQueue=job_queue, state=state
            )
            logger.info("Updated job queue %s to state %s.", job_queue, state)
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error updating job queue %s: %s",
                    job_queue,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.batch.UpdateJobQueue]

    # snippet-start:[python.example_code.batch.DeleteJobQueue]
    def delete_job_queue(self, job_queue: str) -> None:
        """
        Deletes a job queue. The queue must be disabled first.

        :param job_queue: The job queue name or ARN to delete.
        """
        try:
            self.batch_client.delete_job_queue(jobQueue=job_queue)
            logger.info("Deleted job queue %s.", job_queue)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error deleting job queue %s: %s",
                    job_queue,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.batch.DeleteJobQueue]

    # snippet-start:[python.example_code.batch.DeleteComputeEnvironment]
    def delete_compute_environment(self, compute_environment: str) -> None:
        """
        Deletes a compute environment.

        :param compute_environment: The compute environment name or ARN to delete.
        """
        try:
            self.batch_client.delete_compute_environment(
                computeEnvironment=compute_environment
            )
            logger.info(
                "Deleted compute environment %s.", compute_environment
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "ClientException":
                logger.error(
                    "Client error deleting compute environment %s: %s",
                    compute_environment,
                    err.response["Error"]["Message"],
                )
            raise
    # snippet-end:[python.example_code.batch.DeleteComputeEnvironment]

    def wait_for_compute_environment_valid(
        self,
        compute_environment_name: str,
        poll_interval: int = 10,
        max_wait: int = 300,
    ) -> dict:
        """
        Polls until a compute environment reaches VALID status.

        :param compute_environment_name: The name of the compute environment.
        :param poll_interval: Seconds between polls (default 10).
        :param max_wait: Maximum seconds to wait (default 300).
        :return: The compute environment detail dictionary.
        :raises TimeoutError: If the environment does not become VALID in time.
        """
        elapsed = 0
        while elapsed < max_wait:
            environments = self.describe_compute_environments(
                [compute_environment_name]
            )
            if environments:
                env = environments[0]
                status = env.get("status", "")
                if status == "VALID":
                    logger.info(
                        "Compute environment %s is VALID.",
                        compute_environment_name,
                    )
                    return env
                elif status == "INVALID":
                    raise RuntimeError(
                        f"Compute environment {compute_environment_name} "
                        f"is INVALID: {env.get('statusReason', 'unknown')}"
                    )
            time.sleep(poll_interval)
            elapsed += poll_interval
        raise TimeoutError(
            f"Compute environment {compute_environment_name} did not become "
            f"VALID within {max_wait} seconds."
        )

    def wait_for_job_complete(
        self,
        job_id: str,
        poll_interval: int = 10,
        max_wait: int = 600,
    ) -> dict:
        """
        Polls until a job reaches a terminal state (SUCCEEDED or FAILED).

        :param job_id: The ID of the job to monitor.
        :param poll_interval: Seconds between polls (default 10).
        :param max_wait: Maximum seconds to wait (default 600).
        :return: The job detail dictionary.
        :raises TimeoutError: If the job does not complete in time.
        """
        terminal_states = {"SUCCEEDED", "FAILED"}
        elapsed = 0
        last_status = None
        while elapsed < max_wait:
            jobs = self.describe_jobs([job_id])
            if jobs:
                job = jobs[0]
                status = job.get("status", "")
                if status != last_status:
                    logger.info("Job %s status: %s", job_id, status)
                    last_status = status
                if status in terminal_states:
                    return job
            time.sleep(poll_interval)
            elapsed += poll_interval
        raise TimeoutError(
            f"Job {job_id} did not complete within {max_wait} seconds."
        )
