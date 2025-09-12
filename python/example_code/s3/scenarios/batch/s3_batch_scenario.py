# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
S3 Batch Operations Scenario

This scenario demonstrates how to use AWS S3 Batch Operations to perform large-scale
operations on S3 objects. The scenario includes the following steps:

1. Create S3 Batch Job - Creates a batch job to tag objects
2. Update Job Priority - Modifies the job priority and activates the job
3. Cancel Job - Optionally cancels the batch job
4. Describe Job Details - Shows detailed information about the job
5. Get Job Tags - Retrieves tags associated with the job
6. Put Job Tags - Adds additional tags to the job
7. List Jobs - Lists all batch jobs for the account
8. Delete Job Tags - Removes tags from the job

The scenario uses CloudFormation to create necessary IAM roles and demonstrates
proper resource cleanup at the end.
"""

import time
import uuid
import sys
from typing import Tuple

import boto3
from cloudformation_helper import CloudFormationHelper
from s3_batch_wrapper import S3BatchWrapper
sys.path.append("../../../..")
import demo_tools.question as q

# snippet-start:[python.example_code.s3control.helper.S3BatchScenario]
class S3BatchScenario:
    """Manages the S3 Batch Operations scenario."""

    DASHES = "-" * 80
    STACK_NAME = "MyS3Stack"

    def __init__(self, s3_batch_wrapper: S3BatchWrapper, cfn_helper: CloudFormationHelper) -> None:
        """
        Initialize the S3 Batch scenario.

        Args:
            s3_batch_wrapper: S3BatchWrapper instance
            cfn_helper: CloudFormationHelper instance
        """
        self.s3_batch_wrapper = s3_batch_wrapper
        self.cfn_helper = cfn_helper

    def wait_for_input(self) -> None:
        """Wait for user input to continue."""
        q.ask("\nPress Enter to continue...")
        print()

    def setup_resources(self, bucket_name: str, file_names: list) -> Tuple[str, str]:
        """
        Set up initial resources for the scenario.

        Args:
            bucket_name (str): Name of the bucket to create
            file_names (list): List of files to upload

        Returns:
            tuple: Manifest location and report bucket ARN
        """
        print("\nSetting up required resources...")
        self.s3_batch_wrapper.create_bucket(bucket_name)
        report_bucket_arn = f"arn:aws:s3:::{bucket_name}"
        manifest_location = f"arn:aws:s3:::{bucket_name}/job-manifest.csv"
        self.s3_batch_wrapper.upload_files_to_bucket(bucket_name, file_names)
        return manifest_location, report_bucket_arn

    def run_scenario(self) -> None:
        """Run the S3 Batch Operations scenario."""
        account_id = self.s3_batch_wrapper.get_account_id()
        bucket_name = f"demo-s3-batch-{str(uuid.uuid4())}"
        file_names = [
            "job-manifest.csv",
            "object-key-1.txt",
            "object-key-2.txt",
            "object-key-3.txt",
            "object-key-4.txt"
        ]

        print(self.DASHES)
        print("Welcome to the Amazon S3 Batch basics scenario.")
        print("""
    S3 Batch operations enables efficient and cost-effective processing of large-scale 
    data stored in Amazon S3. It automatically scales resources to handle varying workloads 
    without the need for manual intervention.
    
    This Python program walks you through Amazon S3 Batch operations.
    """)

        try:
            # Deploy CloudFormation stack for IAM roles
            print("Deploying CloudFormation stack...")
            self.cfn_helper.deploy_cloudformation_stack(self.STACK_NAME)
            stack_outputs = self.cfn_helper.get_stack_outputs(self.STACK_NAME)
            iam_role_arn = stack_outputs.get('S3BatchRoleArn')
            
            # Set up S3 bucket and upload test files
            manifest_location, report_bucket_arn = self.setup_resources(
                bucket_name, file_names
            )

            self.wait_for_input()

            print("\n1. Creating S3 Batch Job...")
            job_id = self.s3_batch_wrapper.create_s3_batch_job(
                account_id,
                iam_role_arn,
                manifest_location,
                report_bucket_arn
            )

            time.sleep(5)
            failure_reasons = self.s3_batch_wrapper.check_job_failure_reasons(job_id, account_id)
            if failure_reasons:
                print("\nJob failed. Please fix the issues and try again.")
                if not q.ask(
                    "Do you want to proceed with the rest of the operations? (y/n): ", q.is_yesno
                ):
                    raise ValueError("Job failed, stopping execution")

            self.wait_for_input()
            print("\n" + self.DASHES)
            print("2. Update an existing S3 Batch Operations job's priority")
            print("In this step, we modify the job priority value. The higher the number, the higher the priority.")
            self.s3_batch_wrapper.update_job_priority(job_id, account_id)
            
            self.wait_for_input()
            print("\n" + self.DASHES)
            print("3. Cancel the S3 Batch job")
            cancel_job = q.ask("Do you want to cancel the Batch job? (y/n): ", q.is_yesno)
            if cancel_job:
                self.s3_batch_wrapper.cancel_job(job_id, account_id)
            else:
                print(f"Job {job_id} was not canceled.")
                
            self.wait_for_input()
            print("\n" + self.DASHES)
            print("4. Describe the job that was just created")
            self.s3_batch_wrapper.describe_job_details(job_id, account_id)
            
            self.wait_for_input()
            print("\n" + self.DASHES)
            print("5. Describe the tags associated with the job")
            self.s3_batch_wrapper.get_job_tags(job_id, account_id)
            
            self.wait_for_input()
            print("\n" + self.DASHES)
            print("6. Update Batch Job Tags")
            self.s3_batch_wrapper.put_job_tags(job_id, account_id)
            
            self.wait_for_input()
            print("\n" + self.DASHES)
            print("7. List Batch Jobs")
            self.s3_batch_wrapper.list_jobs(account_id)
            
            self.wait_for_input()
            print("\n" + self.DASHES)
            print("8. Delete the Amazon S3 Batch job tagging")
            delete_tags = q.ask("Do you want to delete Batch job tagging? (y/n): ", q.is_yesno)
            if delete_tags:
                self.s3_batch_wrapper.delete_job_tags(job_id, account_id)

            print("\n" + self.DASHES)
            if q.ask(
                "Do you want to delete the AWS resources used in this scenario? (y/n): ", q.is_yesno
            ):
                self.s3_batch_wrapper.cleanup_resources(bucket_name, file_names)
                self.cfn_helper.destroy_cloudformation_stack(self.STACK_NAME)

        except Exception as e:
            print(f"An error occurred: {e}")
            print("\nCleaning up resources due to failure...")
            try:
                self.s3_batch_wrapper.cleanup_resources(bucket_name, file_names)
                self.cfn_helper.destroy_cloudformation_stack(self.STACK_NAME)
            except Exception as cleanup_error:
                print(f"Error during cleanup: {cleanup_error}")
            raise

        print("\nThe Amazon S3 Batch scenario has successfully completed.")
        print(self.DASHES)
# snippet-end:[python.example_code.s3control.helper.S3BatchScenario]

def main() -> None:
    """
    Main function to run the S3 Batch Operations scenario.
    
    This example uses the default settings specified in your shared credentials
    and config files.
    """
    s3_client = boto3.client('s3')
    s3control_client = boto3.client('s3control')
    sts_client = boto3.client('sts')
    cfn_client = boto3.client('cloudformation')
    
    s3_batch_wrapper = S3BatchWrapper(s3_client, s3control_client, sts_client)
    cfn_helper = CloudFormationHelper(cfn_client)
    
    scenario = S3BatchScenario(s3_batch_wrapper, cfn_helper)
    scenario.run_scenario()


if __name__ == "__main__":
    main()