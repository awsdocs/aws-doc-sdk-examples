# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.s3control.Batch.scenario]
"""
This module provides functionality for AWS S3 Batch Operations.
It includes classes for managing CloudFormation stacks and S3 batch scenarios.
"""

import json
import time
import uuid

import boto3
from botocore.exceptions import ClientError, WaiterError

class CloudFormationHelper:
    """Helper class for managing CloudFormation stack operations."""
    # Change the value of 'region' to your preferred AWS Region.
    def __init__(self, region_name='us-west-2'):
        """
        Initialize CloudFormation helper.

        Args:
            region_name (str): AWS region name
        """
        # Create a CloudFormation client for the specified region
        self.cfn_client = boto3.client('cloudformation', region_name=region_name)

    def deploy_cloudformation_stack(self, stack_name):
        """
        Deploy a CloudFormation stack with S3 batch operation permissions.

        Args:
            stack_name (str): Name of the CloudFormation stack

        Raises:
            ClientError: If stack creation fails
        """
        try:
            # Define the CloudFormation template
            template = {
                "AWSTemplateFormatVersion": "2010-09-09",
                "Resources": {
                    "S3BatchRole": {
                        "Type": "AWS::IAM::Role",
                        "Properties": {
                            "AssumeRolePolicyDocument": {
                                "Version": "2012-10-17",
                                "Statement": [
                                    {
                                        "Effect": "Allow",
                                        "Principal": {
                                            "Service": 
                                            "batchoperations.s3.amazonaws.com"
                                        },
                                        "Action": "sts:AssumeRole"
                                    }
                                ]
                            },
                            "ManagedPolicyArns": [
                                "arn:aws:iam::aws:policy/AmazonS3FullAccess"
                            ],
                            "Policies": [
                                {
                                    "PolicyName": "S3BatchOperationsPolicy",
                                    "PolicyDocument": {
                                        "Version": "2012-10-17",
                                        "Statement": [
                                            {
                                                "Effect": "Allow",
                                                "Action": [
                                                    "s3:*",
                                                    "s3-object-lambda:*"
                                                ],
                                                "Resource": "*"
                                            }
                                        ]
                                    }
                                }
                            ]
                        }
                    }
                },
                "Outputs": {
                    "S3BatchRoleArn": {
                        "Description": "ARN of IAM Role for S3 Batch Operations",
                        "Value": {"Fn::GetAtt": ["S3BatchRole", "Arn"]}
                    }
                }
            }

            self.cfn_client.create_stack(
                StackName=stack_name,
                TemplateBody=json.dumps(template),
                Capabilities=['CAPABILITY_IAM']
            )

            print(f"Creating stack {stack_name}...")
            self._wait_for_stack_completion(stack_name, 'CREATE')
            print(f"Stack {stack_name} created successfully")

        except ClientError as e:
            print(f"Error creating CloudFormation stack: {e}")
            raise

    def get_stack_outputs(self, stack_name):
        """
        Get CloudFormation stack outputs.

        Args:
            stack_name (str): Name of the CloudFormation stack

        Returns:
            dict: Stack outputs

        Raises:
            ClientError: If getting stack outputs fails
        """
        try:
            response = self.cfn_client.describe_stacks(StackName=stack_name)
            outputs = {}
            if 'Stacks' in response and response['Stacks']:
                for output in response['Stacks'][0].get('Outputs', []):
                    outputs[output['OutputKey']] = output['OutputValue']
            return outputs

        except ClientError as e:
            print(f"Error getting stack outputs: {e}")
            raise

    def destroy_cloudformation_stack(self, stack_name):
        """
        Delete a CloudFormation stack.

        Args:
            stack_name (str): Name of the CloudFormation stack

        Raises:
            ClientError: If stack deletion fails
        """
        try:
            self.cfn_client.delete_stack(StackName=stack_name)
            print(f"Deleting stack {stack_name}...")
            self._wait_for_stack_completion(stack_name, 'DELETE')
            print(f"Stack {stack_name} deleted successfully")

        except ClientError as e:
            print(f"Error deleting CloudFormation stack: {e}")
            raise

    def _wait_for_stack_completion(self, stack_name, operation):
        """
        Wait for CloudFormation stack operation to complete.

        Args:
            stack_name (str): Name of the CloudFormation stack
            operation (str): Stack operation (CREATE or DELETE)

        Raises:
            WaiterError: If waiting for stack completion fails
        """
        try:
            waiter = self.cfn_client.get_waiter(
                'stack_create_complete' if operation == 'CREATE'
                else 'stack_delete_complete'
            )
            waiter.wait(
                StackName=stack_name,
                WaiterConfig={'Delay': 5, 'MaxAttempts': 60}
            )
        except WaiterError as e:
            print(f"Error waiting for stack {operation}: {e}")
            raise

class S3BatchScenario:
    """Class for managing S3 Batch Operations scenarios."""

    DASHES = "-" * 80
    STACK_NAME = "MyS3Stack"

    def __init__(self, region_name='us-west-2'):
        """
        Initialize S3 Batch Operations scenario.

        Args:
            region_name (str): AWS region name
        """
        self.region_name = region_name
        self.s3_client = boto3.client('s3', region_name=region_name)
        self.s3control_client = boto3.client('s3control', region_name=region_name)
        self.sts_client = boto3.client('sts', region_name=region_name)

    def get_account_id(self):
        """
        Get AWS account ID.

        Returns:
            str: AWS account ID
        """
        return self.sts_client.get_caller_identity()["Account"]

    def create_bucket(self, bucket_name):
        """
        Create an S3 bucket.

        Args:
            bucket_name (str): Name of the bucket to create

        Raises:
            ClientError: If bucket creation fails
        """
        try:
            if self.region_name != 'us-east-1':
                self.s3_client.create_bucket(
                    Bucket=bucket_name,
                    CreateBucketConfiguration={
                        'LocationConstraint': self.region_name
                    }
                )
            else:
                self.s3_client.create_bucket(Bucket=bucket_name)
            print(f"Created bucket: {bucket_name}")
        except ClientError as e:
            print(f"Error creating bucket: {e}")
            raise

    def upload_files_to_bucket(self, bucket_name, file_names):
        """
        Upload files to S3 bucket including manifest file.

        Args:
            bucket_name (str): Target bucket name
            file_names (list): List of file names to upload

        Returns:
            str: ETag of the manifest file

        Raises:
            ClientError: If file upload fails
        """
        try:
            for file_name in file_names:
                if file_name != "job-manifest.csv":
                    content = f"Content for {file_name}"
                    self.s3_client.put_object(
                        Bucket=bucket_name,
                        Key=file_name,
                        Body=content.encode('utf-8')
                    )
                    print(f"Uploaded {file_name} to {bucket_name}")

            manifest_content = ""
            for file_name in file_names:
                if file_name != "job-manifest.csv":
                    manifest_content += f"{bucket_name},{file_name}\n"

            manifest_response = self.s3_client.put_object(
                Bucket=bucket_name,
                Key="job-manifest.csv",
                Body=manifest_content.encode('utf-8')
            )
            print(f"Uploaded manifest file to {bucket_name}")
            print(f"Manifest content:\n{manifest_content}")
            return manifest_response['ETag'].strip('"')

        except ClientError as e:
            print(f"Error uploading files: {e}")
            raise

    def create_s3_batch_job(self, account_id, role_arn, manifest_location,
                           report_bucket_name):
        """
        Create an S3 batch operation job.

        Args:
            account_id (str): AWS account ID
            role_arn (str): IAM role ARN for batch operations
            manifest_location (str): Location of the manifest file
            report_bucket_name (str): Bucket for job reports

        Returns:
            str: Job ID

        Raises:
            ClientError: If job creation fails
        """
        try:
            # Extract bucket name from manifest location
            bucket_name = manifest_location.split(':::')[1].split('/')[0]
            manifest_key = 'job-manifest.csv'
            # Get the ETag of the manifest file for verification
            manifest_obj = self.s3_client.head_object(
                Bucket=bucket_name,
                Key=manifest_key
            )
            etag = manifest_obj['ETag'].strip('"')
            # Create the batch job with specified parameters
            response = self.s3control_client.create_job(
                AccountId=account_id,
                # Define the operation (in this case, adding tags to objects)
                Operation={
                    'S3PutObjectTagging': {
                        'TagSet': [
                            {
                                'Key': 'BatchTag',
                                'Value': 'BatchValue'
                            },
                        ]
                    }
                },
                # Configure job completion report settings
                Report={
                    'Bucket': report_bucket_name,
                    'Format': 'Report_CSV_20180820',
                    'Enabled': True,
                    'Prefix': 'batch-op-reports',
                    'ReportScope': 'AllTasks'
                },
                Manifest={
                    'Spec': {
                        'Format': 'S3BatchOperations_CSV_20180820',
                        'Fields': ['Bucket', 'Key']
                    },
                    'Location': {
                        'ObjectArn': manifest_location,
                        'ETag': etag
                    }
                },
                Priority=10,
                RoleArn=role_arn,
                Description='Batch job for tagging objects',
                # Set to False to avoid confirmation requirement
                ConfirmationRequired=False
            )
            job_id = response['JobId']
            print(f"Created batch job with ID: {job_id}")
            print("Job created and should start automatically")
            return job_id
        except ClientError as e:
            print(f"Error creating batch job: {e}")
            if 'Message' in str(e):
                print(f"Detailed error message: {e.response['Message']}")
            raise
    def check_job_failure_reasons(self, job_id, account_id):
        """
        Check for any failure reasons of a batch job.

        Args:
            job_id (str): ID of the batch job
            account_id (str): AWS account ID

        Returns:
            list: List of failure reasons

        Raises:
            ClientError: If checking job failure reasons fails
        """
        try:
            response = self.s3control_client.describe_job(
                AccountId=account_id,
                JobId=job_id
            )
            if 'FailureReasons' in response['Job']:
                print("Job failure reasons:")
                for reason in response['Job']['FailureReasons']:
                    print(f"- {reason}")
            return response['Job'].get('FailureReasons', [])
        except ClientError as e:
            print(f"Error checking job failure reasons: {e}")
            raise

    def wait_for_job_ready(self, job_id, account_id, desired_status='Ready'):
        """
        Wait for a job to reach the desired status.

        Args:
            job_id (str): ID of the batch job
            account_id (str): AWS account ID
            desired_status (str): Target status to wait for

        Returns:
            bool: True if desired status is reached, False otherwise

        Raises:
            ClientError: If checking job status fails
        """
        print(f"Waiting for job to become {desired_status}...")
        max_attempts = 60
        attempt = 0
        while attempt < max_attempts:
            try:
                response = self.s3control_client.describe_job(
                    AccountId=account_id,
                    JobId=job_id
                )
                current_status = response['Job']['Status']
                print(f"Current job status: {current_status}")
                if current_status == desired_status:
                    return True
                # For jobs with ConfirmationRequired=True, they start in Suspended state
                # and need to be activated
                if current_status == 'Suspended':
                    print("Job is in Suspended state, can proceed with activation")
                    return True
                if current_status in ['Active', 'Failed', 'Cancelled', 'Complete']:
                    print(f"Job is in {current_status} state, cannot update priority")
                    if 'FailureReasons' in response['Job']:
                        print("Failure reasons:")
                        for reason in response['Job']['FailureReasons']:
                            print(f"- {reason}")
                    return False

                time.sleep(20)
                attempt += 1
            except ClientError as e:
                print(f"Error checking job status: {e}")
                raise
        print(f"Timeout waiting for job to become {desired_status}")
        return False

    def update_job_priority(self, job_id, account_id):
        """
        Update the priority of a batch job and start it.

        Args:
            job_id (str): ID of the batch job
            account_id (str): AWS account ID

        Raises:
            ClientError: If updating job priority fails
        """
        try:
            # Check current job status
            response = self.s3control_client.describe_job(
                AccountId=account_id,
                JobId=job_id
            )
            current_status = response['Job']['Status']
            print(f"Current job status before update: {current_status}")
            print(f"Full job details: {response['Job']}")
            
            # First try to update the job priority
            try:
                self.s3control_client.update_job_priority(
                    AccountId=account_id,
                    JobId=job_id,
                    Priority=20
                )
                print(f"Successfully updated priority for job {job_id}")
            except ClientError as e:
                print(f"Warning: Could not update job priority: {e}")
                # Continue anyway to try activating the job
            
            # Then try to activate the job
            try:
                self.s3control_client.update_job_status(
                    AccountId=account_id,
                    JobId=job_id,
                    RequestedJobStatus='Active'
                )
                print(f"Successfully activated job {job_id}")
            except ClientError as e:
                print(f"Error activating job: {e}")
                if 'Message' in str(e):
                    print(f"Detailed error message: {e.response.get('Message', '')}")
                raise
        except ClientError as e:
            print(f"Error updating job priority: {e}")
            raise

    def cleanup_resources(self, bucket_name, file_names):
        """
        Clean up all resources created during the scenario.

        Args:
            bucket_name (str): Name of the bucket to clean up
            file_names (list): List of files to delete

        Raises:
            ClientError: If cleanup fails
        """
        try:
            for file_name in file_names:
                self.s3_client.delete_object(Bucket=bucket_name, Key=file_name)
                print(f"Deleted {file_name}")

            response = self.s3_client.list_objects_v2(
                Bucket=bucket_name,
                Prefix='batch-op-reports/'
            )
            if 'Contents' in response:
                for obj in response['Contents']:
                    self.s3_client.delete_object(
                        Bucket=bucket_name,
                        Key=obj['Key']
                    )
                    print(f"Deleted {obj['Key']}")

            self.s3_client.delete_bucket(Bucket=bucket_name)
            print(f"Deleted bucket {bucket_name}")
        except ClientError as e:
            print(f"Error in cleanup: {e}")
            raise


def wait_for_input():
    """
    Wait for user input to continue.

    Returns:
        None
    """
    while True:
        user_input = input("\nEnter 'c' followed by <ENTER> to continue: ")
        if user_input.lower() == 'c':
            print("Continuing with the program...\n")
            break
        print("Invalid input. Please try again.")


def setup_resources(scenario, bucket_name, file_names):
    """
    Set up initial resources for the scenario.

    Args:
        scenario: S3BatchScenario instance
        bucket_name (str): Name of the bucket to create
        file_names (list): List of files to upload

    Returns:
        tuple: Manifest location and report bucket ARN
    """
    print("\nSetting up required resources...")
    scenario.create_bucket(bucket_name)
    report_bucket_arn = f"arn:aws:s3:::{bucket_name}"
    manifest_location = f"arn:aws:s3:::{bucket_name}/job-manifest.csv"
    scenario.upload_files_to_bucket(bucket_name, file_names)
    return manifest_location, report_bucket_arn


def main():
    """Main function to run the S3 Batch Operations scenario."""
    region_name = 'us-west-2'
    scenario = S3BatchScenario(region_name)
    cfn_helper = CloudFormationHelper(region_name)
    account_id = scenario.get_account_id()
    # Generate a unique bucket name using UUID
    bucket_name = f"demo-s3-batch-{str(uuid.uuid4())}"
    # Define test files to be created and processed
    file_names = [
        "job-manifest.csv",
        "object-key-1.txt",
        "object-key-2.txt",
        "object-key-3.txt",
        "object-key-4.txt"
    ]

    print(scenario.DASHES)
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
        cfn_helper.deploy_cloudformation_stack(scenario.STACK_NAME)
        # Get the created IAM role ARN from stack outputs
        stack_outputs = cfn_helper.get_stack_outputs(scenario.STACK_NAME)
        iam_role_arn = stack_outputs.get('S3BatchRoleArn')
        # Set up S3 bucket and upload test files
        manifest_location, report_bucket_arn = setup_resources(
            scenario, bucket_name, file_names
        )

        wait_for_input()

        print("\n1. Creating S3 Batch Job...")
        job_id = scenario.create_s3_batch_job(
            account_id,
            iam_role_arn,
            manifest_location,
            report_bucket_arn
        )

        time.sleep(5)
        failure_reasons = scenario.check_job_failure_reasons(job_id, account_id)
        if failure_reasons:
            print("\nJob failed. Please fix the issues and try again.")
            if input(
                "Do you want to proceed with the rest of the operations? (y/n): "
            ).lower() != 'y':
                raise ValueError("Job failed, stopping execution")

        wait_for_input()
        print("\n2. Checking job status...")
        # Get current job status instead of trying to update priority
        response = scenario.s3control_client.describe_job(
            AccountId=account_id,
            JobId=job_id
        )
        current_status = response['Job']['Status']
        print(f"Current job status: {current_status}")
        
        # Only try to update priority if job is not already active
        if current_status not in ['Active', 'Complete']:
            print("\nUpdating job priority...")
            scenario.update_job_priority(job_id, account_id)
        else:
            print("Job is already active or complete, no need to update priority.")

        print("\nCleanup")
        if input(
            "Do you want to delete the AWS resources used in this scenario? (y/n): "
        ).lower() == 'y':
            scenario.cleanup_resources(bucket_name, file_names)
            cfn_helper.destroy_cloudformation_stack(scenario.STACK_NAME)

    except Exception as e:
        print(f"An error occurred: {e}")
        raise

    print("\nThe Amazon S3 Batch scenario has successfully completed.")
    print(scenario.DASHES)


if __name__ == "__main__":
    main()
# snippet-end:[python.example_code.s3control.Batch.scenario]
