import boto3
import os
import json

def get_s3_bucket_from_stack(stack_name):
    """
    Get the S3 bucket name created by a CloudFormation stack.

    :param stack_name: The name of the CloudFormation stack.
    :return: The name of the S3 bucket, or None if not found.
    """
    session = boto3.Session()
    cloudformation = session.client('cloudformation', region_name='us-east-1')

    try:
        response = cloudformation.describe_stack_resources(StackName=stack_name)
        resources = response['StackResources']

        for resource in resources:
            if resource['ResourceType'] == 'AWS::S3::Bucket':
                return resource['PhysicalResourceId']

        print(f"No S3 bucket found in stack '{stack_name}'.")
        return None

    except cloudformation.exceptions.ClientError as e:
        print(f"Error describing stack: {e}")
        return None


def trigger_step_function(step_function_arn, payload):
    """
    Trigger a Step Functions execution with the given input payload.

    :param step_function_arn: The ARN of the Step Function to trigger.
    :param payload: The input payload to pass to the Step Function execution.
    """
    session = boto3.Session()
    stepfunctions = session.client('stepfunctions')

    try:
        response = stepfunctions.start_execution(
            stateMachineArn=step_function_arn,
            input=json.dumps(payload)
        )
        print(f"Step Function triggered successfully.")
        print(f"Execution ARN: {response['executionArn']}")
        return response['executionArn']

    except stepfunctions.exceptions.ClientError as e:
        print(f"Error triggering Step Function: {e}")
        return None


def upload_file_to_s3(bucket_name, file_name, region="us-east-1"):
    """
    Upload a file to an S3 bucket from the `nuke` directory.

    :param bucket_name: The name of the S3 bucket.
    :param file_name: The name of the file to upload (relative to the `nuke` directory).
    :param region: The AWS region.
    """
    session = boto3.Session()
    s3 = session.client('s3', region_name=region)
    file_path = os.path.join(file_name)

    try:
        s3.upload_file(file_path, bucket_name, file_name)
        print(f"Uploaded {file_path} to s3://{bucket_name}/{file_name}")
    except Exception as e:
        print(f"Error uploading {file_name}: {e}")


def process_stack_and_upload_files():
    """
    Retrieve the S3 bucket, upload files from the `nuke` directory, and trigger the Step Function.

    """
    # Retrieve the S3 bucket name from the stack
    stack_name = "NukeCleanser"
    files = ["nuke_generic_config.yaml", "nuke_config_update.py"]
    region = 'us-east-1'
    bucket_name = get_s3_bucket_from_stack(stack_name)
    if not bucket_name:
        print(f"Failed to find an S3 bucket in stack '{stack_name}'.")
        return

    print(f"Found S3 bucket: {bucket_name}")

    # Upload files to the bucket
    for file_name in files:
        upload_file_to_s3(bucket_name, file_name, region=region)

    # Retrieve the Step Function ARN from the stack
    step_function_arn = get_step_function_arn(stack_name)
    if not step_function_arn:
        print(f"Failed to find a Step Function in stack '{stack_name}'.")
        return

    print(f"Found Step Function ARN: {step_function_arn}")

    # Trigger the Step Function
    input_payload = {
        "InputPayLoad": {
            "nuke_dry_run": "false",
            "nuke_version": "2.21.2",
            "region_list": [
                "us-east-1"
            ]
        }
    }
    trigger_step_function(step_function_arn, input_payload)


def get_step_function_arn(stack_name):
    """
    Retrieve the ARN of the Step Function from a CloudFormation stack.

    :param stack_name: The name of the CloudFormation stack.
    :return: The ARN of the Step Function, or None if not found.
    """
    session = boto3.Session()
    cloudformation = session.client('cloudformation')

    try:
        response = cloudformation.describe_stack_resources(StackName=stack_name)
        resources = response['StackResources']

        for resource in resources:
            if resource['ResourceType'] == 'AWS::StepFunctions::StateMachine':
                return resource['PhysicalResourceId']

        print(f"No Step Function found in stack '{stack_name}'.")
        return None

    except cloudformation.exceptions.ClientError as e:
        print(f"Error describing stack: {e}")
        return None
