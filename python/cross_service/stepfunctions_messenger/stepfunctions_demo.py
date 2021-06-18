# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Step Functions to
create a state machine that continuously reads message records from an Amazon DynamoDB
database and sends them to an Amazon Simple Queue Service (Amazon SQS) queue.
"""

import argparse
import logging
from pprint import pprint
import time
import boto3
from state_definitions import make_definition
from stepfunctions_statemachine import StepFunctionsStateMachine

logger = logging.getLogger(__name__)


def deploy(stack_name, cf_resource):
    """
    Deploys prerequisite resources used by the `usage_demo` script. The resources are
    defined in the associated `setup.yaml` AWS CloudFormation script and are deployed
    as a CloudFormation stack so they can be easily managed and destroyed.

    :param stack_name: The name of the CloudFormation stack.
    :param cf_resource: A Boto3 CloudFormation resource.
    """
    with open('setup.yaml') as setup_file:
        setup_template = setup_file.read()
    print(f"Creating {stack_name}.")
    stack = cf_resource.create_stack(
        StackName=stack_name,
        TemplateBody=setup_template,
        Capabilities=['CAPABILITY_NAMED_IAM'])
    print("Waiting for stack to deploy. This typically takes a minute or two.")
    waiter = cf_resource.meta.client.get_waiter('stack_create_complete')
    waiter.wait(StackName=stack.name)
    stack.load()
    print(f"Stack status: {stack.stack_status}")
    print("Created resources:")
    for resource in stack.resource_summaries.all():
        print(f"\t{resource.resource_type}, {resource.physical_resource_id}")


def poll_for_messages(queue):
    """
    Polls an Amazon SQS queue for messages until there are no more messages returned.

    :param queue: The queue to poll
    """
    messages = True
    while messages:
        messages = queue.receive_messages(
            MessageAttributeNames=['All'], MaxNumberOfMessages=10, WaitTimeSeconds=5)
        for msg in messages:
            print(f"Message {msg.message_attributes['message_id']['StringValue']} "
                  f"received from {msg.message_attributes['user']['StringValue']}: "
                  f"{msg.body}")
            msg.delete()


def usage_demo(state_machine_name, resources):
    """
    Creates and runs a Step Functions state machine that calls a Lambda function to
    retrieve message records from a DynamoDB table and record them as sent.
    The state machine is then updated to also send messages to an Amazon SQS
    queue and the state machine is run again.
    """
    state_machine = StepFunctionsStateMachine(boto3.client('stepfunctions'))
    table = boto3.resource('dynamodb').Table(resources['MessageTableName'])
    queue = boto3.resource('sqs').Queue(resources['SendQueueUrl'])

    state_machine_arn = state_machine.find(state_machine_name)
    if state_machine_arn is None:
        print("Create a message pump state machine.")
        definition = make_definition(resources, False)
        state_machine.create(state_machine_name, definition, resources['StepRoleArn'])

    print("Put three messages in the message table.")
    for user_name, message in [
            ('wills', 'Brevity is the soul of wit.'),
            ('janea', 'Let us never underestimate the power of a well-written letter.'),
            ('lewisc', "I have proved by actual trial that a letter, that takes an "
                       "hour to write, takes only about 3 minutes to read!")]:
        table.put_item(Item={
            'user_name': user_name, 'message': message,
            'message_id': str(time.time_ns()), 'sent': False})

    print("Start the state machine.")
    run_arn = state_machine.start_run(f"run-without-sqs-{time.time_ns()}")
    print("Wait a few seconds for the state machine to run...")
    time.sleep(10)
    print("Verify that the messages in DynamoDB are marked as sent.")
    messages = table.scan()['Items']
    pprint(messages)

    print("Stop the state machine.")
    state_machine.stop_run(run_arn, "Stop to update for demo.")
    runs = state_machine.list_runs('RUNNING')
    while runs:
        time.sleep(5)
        runs = state_machine.list_runs('RUNNING')

    print("Update the state machine so it sends messages to Amazon SQS.")
    definition = make_definition(resources, True)
    state_machine.update(definition)
    time.sleep(5)

    print("Reset the messages in the DynamoDB table to not sent.")
    for msg in table.scan()['Items']:
        table.update_item(
            Key={'user_name': msg['user_name'], 'message_id': msg['message_id']},
            UpdateExpression='SET sent=:s',
            ExpressionAttributeValues={':s': False})

    print("Restart the state machine.")
    run_arn = state_machine.start_run(f"run-with-sqs-{time.time_ns()}")
    print("Wait for state machine to process messages...")
    time.sleep(15)
    print("Retrieve messages from Amazon SQS.")
    poll_for_messages(queue)

    print("Put another message in the table.")
    table.put_item(
        Item={'user_name': 'wills', 'message': 'Action is eloquence.',
              'message_id': str(time.time_ns()), 'sent': False})
    print("Give the state machine time to find and process the message.")
    time.sleep(15)
    print("Get messages from Amazon SQS.")
    poll_for_messages(queue)

    print("Stop the run.")
    state_machine.stop_run(run_arn, "Done with demo.")


def destroy(state_machine_name, stack, cf_resource):
    """
    Destroys the state machine, the resources managed by the CloudFormation stack,
    and the CloudFormation stack itself.

    :param state_machine_name: The name of the state machine created for the demo.
    :param stack: The CloudFormation stack that manages the demo resources.
    :param cf_resource: A Boto3 CloudFormation resource.
    """
    print("Removing the state machine.")
    state_machine = StepFunctionsStateMachine(boto3.client('stepfunctions'))
    state_machine.find(state_machine_name)
    state_machine.delete()

    print(f"Deleting {stack.name}.")
    stack.delete()
    print("Waiting for stack removal.")
    waiter = cf_resource.meta.client.get_waiter('stack_delete_complete')
    waiter.wait(StackName=stack.name)
    print("Stack delete complete.")


def main():
    parser = argparse.ArgumentParser(
        description="Runs the AWS Step Functions demo. Run this script with the "
                    "'deploy' flag to deploy prerequisite resources, then with the "
                    "'demo' flag to see example usage. Run with the 'destroy' flag to "
                    "clean up all resources.")
    parser.add_argument(
        'action', choices=['deploy', 'demo', 'destroy'],
        help="Indicates the action the script performs.")
    args = parser.parse_args()

    print('-'*88)
    print("Welcome to the AWS Step Functions demo!")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    cf_resource = boto3.resource('cloudformation')
    stack = cf_resource.Stack('doc-example-stepfunctions-messages-stack')
    state_machine_name = 'doc-example-dynamodb-message-pump'

    if args.action == 'deploy':
        print("Deploying prerequisite resources for the demo.")
        deploy(stack.name, cf_resource)
        print("To see example usage, run the script again with the 'demo' flag.")
    elif args.action == 'demo':
        print("Demonstrating how to use AWS Step Functions.")
        usage_demo(
            state_machine_name,
            {o['OutputKey']: o['OutputValue'] for o in stack.outputs})
        print("To clean up all AWS resources created for the demo, run this script "
              "again with the 'destroy' flag.")
    elif args.action == 'destroy':
        print("Destroying AWS resources created for the demo.")
        destroy(state_machine_name, stack, cf_resource)

    print('-'*88)


if __name__ == '__main__':
    main()
