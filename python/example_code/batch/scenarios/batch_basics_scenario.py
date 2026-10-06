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

import json
import logging
import os
import sys
import time

import boto3
from botocore.exceptions import ClientError

# When this script is run directly from the scenarios/ directory, only that
# directory is on sys.path, so batch_wrapper.py in the parent directory is not
# importable. Add the parent directory before importing the wrapper.
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from batch_wrapper import BatchWrapper

logger = logging.getLogger(__name__)

# Trust policy for the ECS task execution role used by Fargate jobs.
ECS_TASKS_TRUST_POLICY = {
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Principal": {"Service": "ecs-tasks.amazonaws.com"},
            "Action": "sts:AssumeRole",
        }
    ],
}
ECS_EXECUTION_POLICY_ARN = (
    "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
)


# snippet-start:[python.example_code.batch.BatchScenario]
class BatchScenario:
    """Runs the AWS Batch Basics scenario."""

    def __init__(
        self,
        batch_wrapper: BatchWrapper,
        iam_client=None,
        ec2_client=None,
    ) -> None:
        """
        Initializes the scenario.

        The scenario self-provisions the networking (default VPC subnet and
        security group) and the ECS task execution role that Fargate jobs
        require, so it can be run with only AWS credentials. All provisioned
        resources are removed in ``cleanup``.

        :param batch_wrapper: A BatchWrapper instance for Batch operations.
        :param iam_client: A Boto3 IAM client (created if not supplied).
        :param ec2_client: A Boto3 EC2 client (created if not supplied).
        """
        self.batch_wrapper = batch_wrapper
        self.iam_client = iam_client or boto3.client("iam")
        self.ec2_client = ec2_client or boto3.client("ec2")
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
        self.execution_role_name = None
        self.execution_role_arn = None

    def discover_networking(self) -> None:
        """
        Finds a subnet and security group in the default VPC.

        This lets the scenario run without the user supplying networking IDs,
        matching the self-contained behavior of the other Batch Basics examples.

        :raises RuntimeError: If no default VPC or no subnet is found.
        """
        vpcs = self.ec2_client.describe_vpcs(
            Filters=[{"Name": "isDefault", "Values": ["true"]}]
        ).get("Vpcs", [])
        if not vpcs:
            raise RuntimeError(
                "No default VPC found. Create a default VPC or run this scenario "
                "in a region that has one."
            )
        vpc_id = vpcs[0]["VpcId"]

        subnets = self.ec2_client.describe_subnets(
            Filters=[{"Name": "vpc-id", "Values": [vpc_id]}]
        ).get("Subnets", [])
        if not subnets:
            raise RuntimeError(f"No subnets found in default VPC {vpc_id}.")
        # Prefer a subnet that assigns public IPs so the Fargate task can pull
        # its public ECR image; fall back to the first subnet otherwise. One
        # subnet is sufficient for the Basics scenario.
        public_subnets = [s for s in subnets if s.get("MapPublicIpOnLaunch")]
        chosen = public_subnets[0] if public_subnets else subnets[0]
        self.subnet_ids = [chosen["SubnetId"]]

        groups = self.ec2_client.describe_security_groups(
            Filters=[
                {"Name": "vpc-id", "Values": [vpc_id]},
                {"Name": "group-name", "Values": ["default"]},
            ]
        ).get("SecurityGroups", [])
        if not groups:
            raise RuntimeError(f"No default security group found in VPC {vpc_id}.")
        self.security_group_ids = [groups[0]["GroupId"]]

    def create_execution_role(self) -> None:
        """
        Creates an ECS task execution role for Fargate jobs.

        Fargate requires an execution role that trusts ecs-tasks.amazonaws.com
        and has the AmazonECSTaskExecutionRolePolicy so the agent can pull the
        container image and write logs. The role is deleted in ``cleanup``.
        """
        response = self.iam_client.create_role(
            RoleName=self.execution_role_name,
            AssumeRolePolicyDocument=json.dumps(ECS_TASKS_TRUST_POLICY),
            Description="ECS task execution role for the Batch Basics scenario.",
        )
        self.execution_role_arn = response["Role"]["Arn"]
        self.iam_client.attach_role_policy(
            RoleName=self.execution_role_name,
            PolicyArn=ECS_EXECUTION_POLICY_ARN,
        )
        # IAM is eventually consistent; give the role a moment to propagate
        # before Batch/Fargate tries to assume it.
        time.sleep(10)
        print(f"Created execution role: {self.execution_role_arn}")

    def setup(self, timestamp: str) -> None:
        """
        Provisions all prerequisites: networking discovery and the ECS task
        execution role, and derives the resource names.

        :param timestamp: A unique timestamp string for naming resources.
        """
        self.ce_name = f"batch-basics-fargate-ce-{timestamp}"
        self.jq_name = f"batch-basics-job-queue-{timestamp}"
        self.jd_name = f"batch-basics-job-def-{timestamp}"
        self.execution_role_name = f"batch-basics-exec-role-{timestamp}"

        print("\nDiscovering default VPC networking...")
        self.discover_networking()
        print(
            f"  Subnets: {', '.join(self.subnet_ids)}\n"
            f"  Security Group: {', '.join(self.security_group_ids)}"
        )
        print("Creating ECS task execution role...")
        self.create_execution_role()

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
            job_definition_name=self.jd_name,
            execution_role_arn=self.execution_role_arn,
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

        Uses only Batch service operations (no CloudFormation). Each resource
        is waited into a stable state before the next transition so deletes do
        not fail with "resource is being modified". A success message is only
        printed when every step succeeds; otherwise the resources that could
        not be removed are listed so the user can delete them manually.
        """
        print("\n" + "-" * 80)
        print("Cleaning up resources...")

        leftovers = []

        # Deregister job definition.
        if self.jd_arn:
            try:
                print(f"Deregistering job definition: {self.jd_arn} ... ", end="")
                self.batch_wrapper.deregister_job_definition(self.jd_arn)
                print("done.")
            except Exception as e:
                logger.error("Error deregistering job definition: %s", e)
                print(f"error: {e}")
                leftovers.append(f"job definition {self.jd_arn}")

        # Disable and delete the job queue. The queue must be VALID before it
        # can be updated, and DISABLED/VALID before it can be deleted.
        if self.jq_name:
            queue_deleted = False
            try:
                print(f"Disabling job queue: {self.jq_name} ... ", end="")
                self.batch_wrapper.wait_for_job_queue_valid(self.jq_name)
                self.batch_wrapper.update_job_queue(self.jq_name, state="DISABLED")
                self.batch_wrapper.wait_for_job_queue_disabled(self.jq_name)
                print("done.")
            except Exception as e:
                logger.error("Error disabling job queue: %s", e)
                print(f"error: {e}")

            try:
                print(f"Deleting job queue: {self.jq_name} ... ", end="")
                self.batch_wrapper.delete_job_queue(self.jq_name)
                # The queue is fully gone only after it leaves the DELETING
                # state; wait so the compute environment can be deleted next.
                self.batch_wrapper.wait_for_job_queue_disabled(self.jq_name)
                print("done.")
                queue_deleted = True
            except Exception as e:
                logger.error("Error deleting job queue: %s", e)
                print(f"error: {e}")
            if not queue_deleted:
                leftovers.append(f"job queue {self.jq_name}")

        # Disable and delete the compute environment. It must be DISABLED and
        # VALID before deletion, otherwise AWS Batch rejects the delete.
        if self.ce_name:
            ce_deleted = False
            try:
                print(f"Disabling compute environment: {self.ce_name} ... ", end="")
                self.batch_wrapper.wait_for_compute_environment_valid(self.ce_name)
                self.batch_wrapper.update_compute_environment(
                    self.ce_name, state="DISABLED"
                )
                self.batch_wrapper.wait_for_compute_environment_disabled(self.ce_name)
                print("done.")
            except Exception as e:
                logger.error("Error disabling compute environment: %s", e)
                print(f"error: {e}")

            try:
                print(f"Deleting compute environment: {self.ce_name} ... ", end="")
                self.batch_wrapper.delete_compute_environment(self.ce_name)
                print("done.")
                ce_deleted = True
            except Exception as e:
                logger.error("Error deleting compute environment: %s", e)
                print(f"error: {e}")
            if not ce_deleted:
                leftovers.append(f"compute environment {self.ce_name}")

        # Delete the ECS task execution role the scenario created. Detach the
        # managed policy first; a role cannot be deleted while policies are
        # attached.
        if self.execution_role_name:
            role_deleted = False
            try:
                print(
                    f"Deleting execution role: {self.execution_role_name} ... ",
                    end="",
                )
                self.iam_client.detach_role_policy(
                    RoleName=self.execution_role_name,
                    PolicyArn=ECS_EXECUTION_POLICY_ARN,
                )
                self.iam_client.delete_role(RoleName=self.execution_role_name)
                print("done.")
                role_deleted = True
            except ClientError as e:
                logger.error("Error deleting execution role: %s", e)
                print(f"error: {e}")
            if not role_deleted:
                leftovers.append(f"IAM role {self.execution_role_name}")

        if leftovers:
            print(
                "\nCleanup incomplete. The following resources were NOT deleted "
                "and must be removed manually to avoid charges:"
            )
            for item in leftovers:
                print(f"  - {item}")
        else:
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
    # The scenario provisions its own networking and ECS task execution role,
    # so it runs with only AWS credentials configured. Everything it creates
    # is removed in cleanup().
    try:
        scenario.setup(timestamp)
        scenario.run()
    finally:
        scenario.cleanup()

    print("\nScenario complete!")


if __name__ == "__main__":
    main()
