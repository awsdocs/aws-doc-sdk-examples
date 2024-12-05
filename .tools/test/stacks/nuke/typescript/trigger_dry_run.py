import json

import boto3


def get_step_function_arn(stack_name):
    """
    Retrieve the ARN of the Step Function from the NukeCleanser CloudFormation stack.

    :param stack_name: The name of the CloudFormation stack.
    :return: The ARN of the Step Function, or None if not found.
    """
    cloudformation = boto3.client("cloudformation")

    try:
        response = cloudformation.describe_stack_resources(StackName=stack_name)
        resources = response["StackResources"]

        for resource in resources:
            if resource["ResourceType"] == "AWS::StepFunctions::StateMachine":
                return resource["PhysicalResourceId"]

        print(f"No Step Function found in stack '{stack_name}'.")
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
    stepfunctions = boto3.client("stepfunctions")

    try:
        response = stepfunctions.start_execution(
            stateMachineArn=step_function_arn, input=json.dumps(payload)
        )
        print(f"Step Function triggered successfully.")
        print(f"Execution ARN: {response['executionArn']}")
        return response["executionArn"]

    except stepfunctions.exceptions.ClientError as e:
        print(f"Error triggering Step Function: {e}")
        return None


if __name__ == "__main__":
    # CloudFormation stack name
    stack_name = "NukeCleanser"

    # Input payload
    input_payload = {
        "InputPayLoad": {
            "nuke_dry_run": "true",
            "nuke_version": "2.21.2",
            "region_list": ["us-east-1"],
        }
    }

    # Get the ARN of the Step Function from the stack
    step_function_arn = get_step_function_arn(stack_name)

    if step_function_arn:
        print(f"Found Step Function ARN: {step_function_arn}")
        # Trigger the Step Function
        trigger_step_function(step_function_arn, input_payload)
    else:
        print(f"Failed to find a Step Function in stack '{stack_name}'.")
