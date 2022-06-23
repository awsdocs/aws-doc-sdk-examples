# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Lambda to do the following:

1. Create an AWS Identity and Access Management (IAM) role that grants Lambda
permission to write to logs.
2. Create a Lambda function and upload handler code.
3. Invoke the function with a single parameter and get results.
4. Update the function code and configure its Lambda environment with an environment
variable.
5. Invoke the function with new parameters and get results. Display the execution
log that's returned from the invocation.
6. List the functions for your account.
7. Delete the IAM role and the Lambda function.
"""

import base64
import json
import logging
import sys
import boto3
from lambda_basics import LambdaWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append('../..')
from demo_tools.custom_waiter import CustomWaiter, WaitState
import demo_tools.question as q
from demo_tools.retries import wait

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.lambda.Scenario_GettingStartedFunctions]
class UpdateFunctionWaiter(CustomWaiter):
    """A custom waiter that waits until a function is successfully updated."""
    def __init__(self, client):
        super().__init__(
            'UpdateSuccess', 'GetFunction',
            'Configuration.LastUpdateStatus',
            {'Successful': WaitState.SUCCESS, 'Failed': WaitState.FAILURE},
            client)

    def wait(self, function_name):
        self._wait(FunctionName=function_name)


def run_scenario(lambda_client, iam_resource, basic_file, calculator_file, lambda_name):
    """
    Runs the scenario.

    :param lambda_client: A Boto3 Lambda client.
    :param iam_resource: A Boto3 IAM resource.
    :param basic_file: The name of the file that contains the basic Lambda handler.
    :param calculator_file: The name of the file that contains the calculator Lambda handler.
    :param lambda_name: The name to give resources created for the scenario, such as the
                        IAM role and the Lambda function.
    """
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    print('-'*88)
    print("Welcome to the AWS Lambda getting started with functions demo.")
    print('-'*88)

    wrapper = LambdaWrapper(lambda_client, iam_resource)

    print("Checking for IAM role for Lambda...")
    iam_role, should_wait = wrapper.create_iam_role_for_lambda(lambda_name)
    if should_wait:
        logger.info("Giving AWS time to create resources...")
        wait(10)

    print(f"Looking for function {lambda_name}...")
    function = wrapper.get_function(lambda_name)
    if function is None:
        print("Zipping the Python script into a deployment package...")
        deployment_package = wrapper.create_deployment_package(basic_file, f"{lambda_name}.py")
        print(f"...and creating the {lambda_name} Lambda function.")
        wrapper.create_function(
            lambda_name, f'{lambda_name}.lambda_handler', iam_role, deployment_package)
    else:
        print(f"Function {lambda_name} already exists.")
    print('-'*88)

    print(f"Let's invoke {lambda_name}. This function increments a number.")
    action_params = {
        'action': 'increment',
        'number': q.ask("Give me a number to increment: ", q.is_int)}
    print(f"Invoking {lambda_name}...")
    response = wrapper.invoke_function(lambda_name, action_params)
    print(f"Incrementing {action_params['number']} resulted in "
          f"{json.load(response['Payload'])}")
    print('-'*88)

    print(f"Let's update the function to an arithmetic calculator.")
    q.ask("Press Enter when you're ready.")
    print("Creating a new deployment package...")
    deployment_package = wrapper.create_deployment_package(calculator_file, f"{lambda_name}.py")
    print(f"...and updating the {lambda_name} Lambda function.")
    update_waiter = UpdateFunctionWaiter(lambda_client)
    wrapper.update_function_code(lambda_name, deployment_package)
    update_waiter.wait(lambda_name)
    print(f"This function uses an environment variable to control logging level.")
    print(f"Let's set it to DEBUG to get the most logging.")
    wrapper.update_function_configuration(
        lambda_name, {'LOG_LEVEL': logging.getLevelName(logging.DEBUG)})

    actions = ['plus', 'minus', 'times', 'divided-by']
    want_invoke = True
    while want_invoke:
        print(f"Let's invoke {lambda_name}. You can invoke these actions:")
        for index, action in enumerate(actions):
            print(f"{index + 1}: {action}")
        action_params = {}
        action_index = q.ask(
            "Enter the number of the action you want to take: ",
            q.is_int, q.in_range(1, len(actions)))
        action_params['action'] = actions[action_index - 1]
        print(f"You've chosen to invoke 'x {action_params['action']} y'.")
        action_params['x'] = q.ask("Enter a value for x: ", q.is_int)
        action_params['y'] = q.ask("Enter a value for y: ", q.is_int)
        print(f"Invoking {lambda_name}...")
        response = wrapper.invoke_function(lambda_name, action_params, True)
        print(f"Calculating {action_params['x']} {action_params['action']} {action_params['y']} "
              f"resulted in {json.load(response['Payload'])}")
        q.ask("Press Enter to see the logs from the call.")
        print(base64.b64decode(response['LogResult']).decode())
        want_invoke = q.ask("That was fun. Shall we do it again? (y/n) ", q.is_yesno)
    print('-'*88)

    if q.ask("Do you want to list all of the functions in your account? (y/n) "):
        wrapper.list_functions()
    print('-'*88)

    if q.ask("Ready to delete the function and role? (y/n) ", q.is_yesno):
        for policy in iam_role.attached_policies.all():
            policy.detach_role(RoleName=iam_role.name)
        iam_role.delete()
        print(f"Deleted role {lambda_name}.")
        wrapper.delete_function(lambda_name)
        print(f"Deleted function {lambda_name}.")

    print("\nThanks for watching!")
    print('-'*88)


if __name__ == '__main__':
    try:
        run_scenario(
            boto3.client('lambda'), boto3.resource('iam'), 'lambda_handler_basic.py',
            'lambda_handler_calculator.py', 'doc_example_lambda_calculator')
    except Exception:
        logging.exception("Something went wrong with the demo!")
# snippet-end:[python.example_code.lambda.Scenario_GettingStartedFunctions]
