import json
import logging
import os
from typing import Optional

import boto3
from botocore.exceptions import ClientError

# Configure logging
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s"
)

# Constants
STACK_NAME = "AccountNukerStack"
REGION = "us-east-1"
FILES_TO_UPLOAD = ["nuke_generic_config.yaml", "nuke_config_update.py"]
INPUT_PAYLOAD = {
    "InputPayLoad": {
        "nuke_dry_run": "false",
        "nuke_version": "2.21.2",
        "region_list": ["us-east-1"],
    }
}

# Use session instead of client to avoid
# credential conflict issue related to provider.
session = boto3.Session()


def get_resource_from_stack(stack_name: str, resource_type: str) -> Optional[str]:
    """
    Retrieve a specific resource from a CloudFormation stack.

    :param stack_name: The name of the CloudFormation stack.
    :param resource_type: The type of resource to retrieve (e.g., 'AWS::S3::Bucket', 'AWS::StepFunctions::StateMachine').
    :return: The physical resource ID, or None if not found.
    """
    cloudformation = session.client("cloudformation", region_name=REGION)

    try:
        response = cloudformation.describe_stack_resources(StackName=stack_name)
        resources = response["StackResources"]

        for resource in resources:
            if resource["ResourceType"] == resource_type:
                return resource["PhysicalResourceId"]

        logging.warning(f"No {resource_type} found in stack '{stack_name}'.")
        return None

    except ClientError as e:
        logging.error(f"Error describing stack: {e}")
        return None


def get_s3_bucket_from_stack(stack_name: str) -> Optional[str]:
    """
    Retrieve the S3 bucket name from a CloudFormation stack.

    :param stack_name: The name of the CloudFormation stack.
    :return: The name of the S3 bucket, or None if not found.
    """
    return get_resource_from_stack(stack_name, "AWS::S3::Bucket")


def get_step_function_arn(stack_name: str) -> Optional[str]:
    """
    Retrieve the ARN of the Step Function from a CloudFormation stack.

    :param stack_name: The name of the CloudFormation stack.
    :return: The ARN of the Step Function, or None if not found.
    """
    return get_resource_from_stack(stack_name, "AWS::StepFunctions::StateMachine")


def trigger_step_function(step_function_arn: str, payload: dict) -> Optional[str]:
    """
    Trigger a Step Functions execution with the given input payload.

    :param step_function_arn: The ARN of the Step Function to trigger.
    :param payload: The input payload to pass to the Step Function execution.
    :return: The execution ARN, or None if an error occurred.
    """
    stepfunctions = session.client("stepfunctions", region_name=REGION)

    try:
        response = stepfunctions.start_execution(
            stateMachineArn=step_function_arn, input=json.dumps(payload)
        )
        logging.info(f"Step Function triggered successfully.")
        logging.info(f"Execution ARN: {response['executionArn']}")
        return response["executionArn"]

    except ClientError as e:
        logging.error(f"Error triggering Step Function: {e}")
        return None


def upload_file_to_s3(bucket_name: str, file_name: str):
    """
    Upload a file to an S3 bucket from the current directory.

    :param bucket_name: The name of the S3 bucket.
    :param file_name: The name of the file to upload.
    """
    s3 = session.client("s3", region_name=REGION)
    file_path = os.path.join(os.getcwd(), file_name)

    try:
        s3.upload_file(file_path, bucket_name, file_name)
        logging.info(f"Uploaded {file_path} to s3://{bucket_name}/{file_name}")
    except Exception as e:
        logging.error(f"Error uploading {file_name}: {e}")


def process_stack_and_upload_files():
    """
    Retrieve the S3 bucket and Step Function ARN from the CloudFormation stack,
    upload files to the S3 bucket, and trigger the Step Function.
    """
    bucket_name = get_s3_bucket_from_stack(STACK_NAME)
    if not bucket_name:
        logging.error(f"Failed to find an S3 bucket in stack '{STACK_NAME}'.")
        return

    logging.info(f"Found S3 bucket: {bucket_name}")

    # Upload files to the bucket
    for file_name in FILES_TO_UPLOAD:
        upload_file_to_s3(bucket_name, file_name)

    step_function_arn = get_step_function_arn(STACK_NAME)
    if not step_function_arn:
        logging.error(f"Failed to find a Step Function in stack '{STACK_NAME}'.")
        return

    logging.info(f"Found Step Function ARN: {step_function_arn}")

    # Trigger the Step Function
    trigger_step_function(step_function_arn, INPUT_PAYLOAD)
