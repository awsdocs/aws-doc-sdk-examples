# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: AWS Batch Basics scenario.

This scenario demonstrates the complete lifecycle of an AWS Batch workload:
1. Create a Fargate compute environment.
2. Create a job queue.
3. Register a job definition.
4. Submit a job.
5. Monitor the job to completion.
6. List jobs in the queue.
7. Clean up all resources in reverse dependency order.
"""

import logging
import os
import time

import boto3

from batch_wrapper import BatchWrapper

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.batch.BatchScenario]
class BatchScenario:
    """Runs the AWS Batch Basics scenario."""

    def __init__(self, batch_wrapper: BatchWrapper) -> None:
        """
        Initializes the scenario with a BatchWrapper.

        :param batch_wrapper: A BatchWrapper instance for Batch operations.
        """
        self.batch_wrapper = batch_wrapper
        self.ce_name = None
        self.ce_arn = None
        self.jq_name = None
        self.jq_arn = None
        self.jd_name = None
        self.jd_arn = None
        self.jd_revision = None
        self.job_id = None
        self.subnet_ids = None
        self.security_group_ids = None

    def setup(self, subnet_ids: list, security_group_ids: list, timestamp: str) -> None:
        """
        Sets up the prerequisite networking resource references.

        :param subnet_ids: Subnet IDs for the compute environment.
        :param security_group_ids: Security group IDs for the compute environment.
        :param timestamp: A unique timestamp string for naming resources.
        """
        self.subnet_ids = subnet_ids
        self.security_group_ids = security_group_ids
        self.ce_name = f"batch-basics-fargate-ce-{timestamp}"
        self.jq_name = f"batch-basics-job-queue-{timestamp}"
        self.jd_name = f"batch-basics-job-def-{timestamp}"
        print(
            f"\nSubnets: {', '.join(subnet_ids)}"
            f"\nSecurity Group: {', '.join(security_group_ids)}"
        )

    def create_compute_environment(self) -> None:
        """Step 1: Create a Fargate compute environment."""
        print("\n" + "-" * 80)
        print("Step 1: Create a Fargate compute environment")
        print(f"Creating compute environment: {self.ce_name}")

        response = self.batch_wrapper.create_compute_environment(
            compute_environment_name=self.ce_name,
            subnet_ids=self.subnet_ids,
            security_group_ids=self.security_group_ids,
        )
        self.ce_arn = response["computeEnvironmentArn"]
        print(f"Compute environment ARN: {self.ce_arn}")

        print("Waiting for compute environment to become VALID...")
        env = self.batch_wrapper.wait_for_compute_environment_valid(self.ce_name)
        print(
            f"Compute environment is VALID.\n"
            f"  Name: {env['computeEnvironmentName']}\n"
            f"  Type: {env.get('type', 'N/A')}\n"
            f"  State: {env.get('state', 'N/A')}\n"
            f"  Status: {env.get('status', 'N/A')}"
        )
        print("-" * 80)

    def create_job_queue(self) -> None:
        """Step 2: Create a job queue."""
        print("\n" + "-" * 80)
        print("Step 2: Create a job queue")
        print(f"Creating job queue: {self.jq_name}")

        response = self.batch_wrapper.create_job_queue(
            job_queue_name=self.jq_name,
            compute_environment_name=self.ce_name,
        )
        self.jq_arn = response["jobQueueArn"]
        print(f"Job queue ARN: {self.jq_arn}")
        print("Job queue is ready.")
        print("-" * 80)

    def register_job_definition(self) -> None:
        """Step 3: Register a job definition."""
        print("\n" + "-" * 80)
        print("Step 3: Register a job definition")
        print(f"Registering job definition: {self.jd_name}")

        response = self.batch_wrapper.register_job_definition(
            job_definition_name=self.jd_name
        )
        self.jd_arn = response["jobDefinitionArn"]
        self.jd_revision = response["revision"]
        print(f"Job definition ARN: {self.jd_arn}")
        print(f"Revision: {self.jd_revision}")
        print("-" * 80)

    def submit_job(self) -> None:
        """Step 4: Submit a job."""
        print("\n" + "-" * 80)
        print("Step 4: Submit a job")

        job_name = "batch-basics-hello-job"
        print(f"Submitting job: {job_name}")
        response = self.batch_wrapper.submit_job(
            job_name=job_name,
            job_queue=self.jq_name,
            job_definition=f"{self.jd_name}:{self.jd_revision}",
        )
        self.job_id = response["jobId"]
        print(
            f"Job submitted successfully.\n"
            f"  Job Name: {response['jobName']}\n"
            f"  Job ID: {self.job_id}\n"
            f"  Job ARN: {response.get('jobArn', 'N/A')}"
        )
        print("-" * 80)

    def monitor_job(self) -> None:
        """Step 5: Monitor the job."""
        print("\n" + "-" * 80)
        print("Step 5: Monitor the job")
        print(f"Waiting for job {self.job_id} to complete...")

        job = self.batch_wrapper.wait_for_job_complete(self.job_id)
        status = job.get("status", "UNKNOWN")
        if status == "SUCCEEDED":
            print("Job completed successfully!")
        else:
            print(f"Job ended with status: {status}")
            reason = job.get("statusReason", "No reason provided")
            print(f"  Status Reason: {reason}")

        print(f"  Final Status: {status}")
        container = job.get("container", {})
        exit_code = container.get("exitCode", "N/A")
        print(f"  Exit Code: {exit_code}")
        print("-" * 80)

    def list_jobs(self) -> None:
        """Step 6: List jobs in the queue."""
        print("\n" + "-" * 80)
        print("Step 6: List jobs in the queue")
        print(f"Listing SUCCEEDED jobs in queue: {self.jq_name}")

        job_summaries = self.batch_wrapper.list_jobs(
            job_queue=self.jq_name, job_status="SUCCEEDED"
        )
        if job_summaries:
            print(f"Found {len(job_summaries)} job(s):")
            for js in job_summaries:
                print(
                    f"  - Job ID: {js['jobId']} | "
                    f"Name: {js['jobName']} | "
                    f"Status: {js.get('status', 'N/A')}"
                )
        else:
            print("No SUCCEEDED jobs found.")
        print("-" * 80)

    def cleanup(self) -> None:
        """
        Cleans up all resources in reverse dependency order.
        Uses only Batch service operations (no CloudFormation).
        """
        print("\n" + "-" * 80)
        print("Cleaning up resources...")

        # Deregister job definition
        if self.jd_arn:
            try:
                print(f"Deregistering job definition: {self.jd_arn} ... ", end="")
                self.batch_wrapper.deregister_job_definition(self.jd_arn)
                print("done.")
            except Exception as e:
                logger.error("Error deregistering job definition: %s", e)
                print(f"error: {e}")

        # Disable and delete job queue
        if self.jq_name:
            try:
                print(f"Disabling job queue: {self.jq_name} ... ", end="")
                self.batch_wrapper.update_job_queue(self.jq_name, state="DISABLED")
                print("done.")
                # The queue must finish transitioning to DISABLED/VALID before
                # it can be deleted, so wait for that before calling delete.
                self.batch_wrapper.wait_for_job_queue_disabled(self.jq_name)
            except Exception as e:
                logger.error("Error disabling job queue: %s", e)
                print(f"error: {e}")

            try:
                print(f"Deleting job queue: {self.jq_name} ... ", end="")
                self.batch_wrapper.delete_job_queue(self.jq_name)
                print("done.")
            except Exception as e:
                logger.error("Error deleting job queue: %s", e)
                print(f"error: {e}")

        # Delete compute environment
        if self.ce_name:
            try:
                print(
                    f"Deleting compute environment: {self.ce_name} ... ",
                    end="",
                )
                self.batch_wrapper.delete_compute_environment(self.ce_name)
                print("done.")
            except Exception as e:
                logger.error("Error deleting compute environment: %s", e)
                print(f"error: {e}")

        print("All resources cleaned up successfully.")
        print("-" * 80)

    def run(self) -> None:
        """Runs the full scenario."""
        print("=" * 80)
        print("Welcome to the AWS Batch Basics scenario!")
        print("=" * 80)

        self.create_compute_environment()
        self.create_job_queue()
        self.register_job_definition()
        self.submit_job()
        self.monitor_job()
        self.list_jobs()


# snippet-end:[python.example_code.batch.BatchScenario]


def main() -> None:
    """Main entry point for the scenario."""
    logging.basicConfig(level=logging.INFO)

    batch_client = boto3.client("batch")
    wrapper = BatchWrapper(batch_client)
    scenario = BatchScenario(wrapper)

    timestamp = str(int(time.time()))
    # Read the networking configuration from the environment so the scenario
    # can run against a real account without editing the source. Provide
    # comma-separated values, e.g.:
    #   export BATCH_SUBNET_IDS=subnet-0abc1234,subnet-0def5678
    #   export BATCH_SECURITY_GROUP_IDS=sg-0aabbccdd
    subnet_ids = [s for s in os.environ.get("BATCH_SUBNET_IDS", "").split(",") if s]
    security_group_ids = [
        s for s in os.environ.get("BATCH_SECURITY_GROUP_IDS", "").split(",") if s
    ]
    if not subnet_ids or not security_group_ids:
        print(
            "Set BATCH_SUBNET_IDS and BATCH_SECURITY_GROUP_IDS (comma-separated) "
            "to the subnet and security group IDs from your VPC before running "
            "this scenario."
        )
        return

    try:
        scenario.setup(subnet_ids, security_group_ids, timestamp)
        scenario.run()
    finally:
        scenario.cleanup()

    print("\nScenario complete!")


if __name__ == "__main__":
    main()
