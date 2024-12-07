"""
This module is part of the AccountNuker stack deployment process.
It's invoked by the stacks/deploy.py script to support integration testing.

Main purposes:
1. Retrieve resources (S3 bucket and Step Function ARN) from a CloudFormation stack.
2. Upload configuration and helper files to the S3 bucket.
3. Trigger a Step Function execution to initiate the account nuking procedure.

The uploaded files and Step Function execution are used by CodeBuild to execute the nuking procedure.
"""

import json
import logging
import os
from typing import Optional, Dict

import boto3
from botocore.exceptions import ClientError

# Configuration
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

# Logging setup
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s"
)
logger = logging.getLogger(__name__)

# AWS session
session = boto3.Session(region_name=REGION)


def _get_resource_from_stack(stack_name: str, resource_type: str) -> Optional[str]:
    """Retrieve a specific resource from a CloudFormation stack."""
    cloudformation = session.client("cloudformation")
    try:
        response = cloudformation.describe_stack_resources(StackName=stack_name)
        for resource in response["StackResources"]:
            if resource["ResourceType"] == resource_type:
                return resource["PhysicalResourceId"]
        logger.warning(f"No {resource_type} found in stack '{stack_name}'.")
    except ClientError as e:
        logger.error(f"Error describing stack: {e}")
    return None


def _get_s3_bucket_from_stack(stack_name: str) -> Optional[str]:
    """Retrieve the S3 bucket name from a CloudFormation stack."""
    return _get_resource_from_stack(stack_name, "AWS::S3::Bucket")


def _get_step_function_arn(stack_name: str) -> Optional[str]:
    """Retrieve the ARN of the Step Function from a CloudFormation stack."""
    return _get_resource_from_stack(stack_name, "AWS::StepFunctions::StateMachine")


def _trigger_step_function(step_function_arn: str, payload: Dict) -> Optional[str]:
    """Trigger a Step Functions execution with the given input payload."""
    stepfunctions = session.client("stepfunctions")
    try:
        response = stepfunctions.start_execution(
            stateMachineArn=step_function_arn, input=json.dumps(payload)
        )
        logger.info(
            f"Step Function triggered successfully. Execution ARN: {response['executionArn']}"
        )
        return response["executionArn"]
    except ClientError as e:
        logger.error(f"Error triggering Step Function: {e}")
    return None


def _upload_file_to_s3(bucket_name: str, file_name: str) -> bool:
    """Upload a file to an S3 bucket from the current directory."""
    s3 = session.client("s3")
    file_path = os.path.join(os.getcwd(), file_name)
    try:
        s3.upload_file(file_path, bucket_name, file_name)
        logger.info(f"Uploaded {file_path} to s3://{bucket_name}/{file_name}")
        return True
    except FileNotFoundError:
        logger.error(f"The file {file_name} was not found in the current directory.")
    except ClientError as e:
        logger.error(f"Error uploading {file_name}: {e}")
    return False


def process_stack_and_upload_files():
    """
    Main function to process the stack, upload files, and trigger the Step Function.
    This is the only public function intended to be called from outside this module.
    """
    bucket_name = _get_s3_bucket_from_stack(STACK_NAME)
    if not bucket_name:
        logger.error(f"Failed to find an S3 bucket in stack '{STACK_NAME}'.")
        return

    logger.info(f"Found S3 bucket: {bucket_name}")

    # Upload files to the bucket
    for file_name in FILES_TO_UPLOAD:
        _upload_file_to_s3(bucket_name, file_name)

    step_function_arn = _get_step_function_arn(STACK_NAME)
    if not step_function_arn:
        logger.error(f"Failed to find a Step Function in stack '{STACK_NAME}'.")
        return

    logger.info(f"Found Step Function ARN: {step_function_arn}")

    # Trigger the Step Function
    _trigger_step_function(step_function_arn, INPUT_PAYLOAD)
