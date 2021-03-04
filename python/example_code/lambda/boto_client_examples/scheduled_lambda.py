# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to register an AWS Lambda function
that is invoked by Amazon EventBridge on a regular schedule.

Instead of using the low-level Boto3 client APIs shown in this example, you can use
AWS Chalice to more easily create a scheduled AWS Lambda function. For more
information on AWS Chalice, see https://github.com/aws/chalice.
"""

import logging
import time
import boto3
from botocore.exceptions import ClientError

import lambda_basics

logger = logging.getLogger(__name__)


def schedule_lambda_function(
        eventbridge_client, event_rule_name, event_schedule,
        lambda_client, lambda_function_name, lambda_function_arn):
    """
    Creates a schedule rule with Amazon EventBridge and registers an AWS Lambda
    function to be invoked according to the specified schedule.

    :param eventbridge_client: The Boto3 EventBridge client.
    :param event_rule_name: The name of the scheduled event rule.
    :param event_schedule: The specified schedule in either cron or rate format.
    :param lambda_client: The Boto3 Lambda client.
    :param lambda_function_name: The name of the AWS Lambda function to invoke.
    :param lambda_function_arn: The Amazon Resource Name (ARN) of the function.
    :return: The ARN of the EventBridge rule.
    """
    try:
        response = eventbridge_client.put_rule(
            Name=event_rule_name, ScheduleExpression=event_schedule)
        event_rule_arn = response['RuleArn']
        logger.info("Put rule %s with ARN %s.", event_rule_name, event_rule_arn)
    except ClientError:
        logger.exception("Couldn't put rule %s.", event_rule_name)
        raise

    try:
        lambda_client.add_permission(
            FunctionName=lambda_function_name,
            StatementId=f'{lambda_function_name}-invoke',
            Action='lambda:InvokeFunction',
            Principal='events.amazonaws.com',
            SourceArn=event_rule_arn)
        logger.info(
            "Granted permission to let Amazon EventBridge call function %s",
            lambda_function_name)
    except ClientError:
        logger.exception(
            "Couldn't add permission to let Amazon EventBridge call function %s.",
            lambda_function_name)
        raise

    try:
        response = eventbridge_client.put_targets(
            Rule=event_rule_name,
            Targets=[{'Id': lambda_function_name, 'Arn': lambda_function_arn}])
        if response['FailedEntryCount'] > 0:
            logger.error(
                "Couldn't set %s as the target for %s.",
                lambda_function_name, event_rule_name)
        else:
            logger.info(
                "Set %s as the target of %s.", lambda_function_name, event_rule_name)
    except ClientError:
        logger.exception(
            "Couldn't set %s as the target of %s.", lambda_function_name,
            event_rule_name)
        raise

    return event_rule_arn


def update_event_rule(eventbridge_client, event_rule_name, enable):
    """
    Updates the schedule event rule by enabling or disabling it.

    :param eventbridge_client: The Boto3 EventBridge client.
    :param event_rule_name: The name of the rule to update.
    :param enable: When True, the rule is enabled. Otherwise, it is disabled.
    """
    try:
        if enable:
            eventbridge_client.enable_rule(Name=event_rule_name)
        else:
            eventbridge_client.disable_rule(Name=event_rule_name)
        logger.info(
            "%s is now %s.", event_rule_name, 'enabled' if enable else 'disabled')
    except ClientError:
        logger.exception(
            "Couldn't %s %s.", 'enable' if enable else 'disable', event_rule_name)
        raise


def get_event_rule_enabled(eventbridge_client, event_rule_name):
    """
    Indicates whether the specified rule is enabled or disabled.

    :param eventbridge_client: The Boto3 EventBridge client.
    :param event_rule_name: The name of the rule query.
    :return: True when the rule is enabled. Otherwise, False.
    """
    try:
        response = eventbridge_client.describe_rule(Name=event_rule_name)
        enabled = response['State'] == 'ENABLED'
        logger.info("%s is %s.", event_rule_name, response['State'])
    except ClientError:
        logger.exception("Couldn't get state of %s.", event_rule_name)
        raise
    else:
        return enabled


def delete_event_rule(eventbridge_client, event_rule_name, lambda_function_name):
    """
    Removes the specified targets from the event rule and deletes the rule.

    :param eventbridge_client: The Boto3 EventBridge client.
    :param event_rule_name: The name of the rule to delete.
    :param lambda_function_name: The name of the AWS Lambda function to remove
                                 as a target.
    """
    try:
        eventbridge_client.remove_targets(
            Rule=event_rule_name, Ids=[lambda_function_name])
        eventbridge_client.delete_rule(Name=event_rule_name)
        logger.info("Removed rule %s.", event_rule_name)
    except ClientError:
        logger.exception("Couldn't remove rule %s.", event_rule_name)
        raise


def usage_demo():
    """
    Shows how to deploy an AWS Lambda function, create an Amazon EventBridge schedule
    rule that invokes the function, and how to clean up the resources after the demo
    completes.
    """
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    print('-'*88)
    print("Welcome to the AWS Lambda basics demo.")
    print('-'*88)

    lambda_function_filename = 'lambda_handler_scheduled.py'
    lambda_handler_name = 'lambda_handler_scheduled.lambda_handler'
    lambda_role_name = 'demo-lambda-role'
    lambda_function_name = 'demo-lambda-scheduled'
    event_rule_name = 'demo-event-scheduled'
    event_schedule = 'rate(1 minute)'

    iam_resource = boto3.resource('iam')
    lambda_client = boto3.client('lambda')
    eventbridge_client = boto3.client('events')
    logs_client = boto3.client('logs')

    print(f"Creating AWS Lambda function {lambda_function_name} from the "
          f"{lambda_handler_name} function in {lambda_function_filename}...")
    deployment_package = lambda_basics.create_lambda_deployment_package(
        lambda_function_filename)
    iam_role = lambda_basics.create_iam_role_for_lambda(iam_resource, lambda_role_name)
    lambda_function_arn = lambda_basics.exponential_retry(
        lambda_basics.deploy_lambda_function, 'InvalidParameterValueException',
        lambda_client, lambda_function_name, lambda_handler_name, iam_role,
        deployment_package)

    print(f"Scheduling {lambda_function_name} to run once per minute...")
    schedule_lambda_function(
        eventbridge_client, event_rule_name, event_schedule, lambda_client,
        lambda_function_name, lambda_function_arn)

    print(f"Sleeping for 3 minutes to let our function trigger a few times...")
    time.sleep(3*60)

    print(f"Getting last 20 Amazon CloudWatch log events for {lambda_function_name}...")
    log_group_name = f'/aws/lambda/{lambda_function_name}'
    log_streams = logs_client.describe_log_streams(
        logGroupName=log_group_name, orderBy='LastEventTime', descending=True, limit=1)
    log_events = logs_client.get_log_events(
        logGroupName=log_group_name,
        logStreamName=log_streams['logStreams'][0]['logStreamName'],
        limit=20)
    print(*[evt['message'] for evt in log_events['events']])

    print(f"Disabling event {event_rule_name}...")
    update_event_rule(eventbridge_client, event_rule_name, False)
    get_event_rule_enabled(eventbridge_client, event_rule_name)

    print("Cleaning up all resources created for the demo...")
    delete_event_rule(eventbridge_client, event_rule_name, lambda_function_name)
    lambda_basics.delete_lambda_function(lambda_client, lambda_function_name)
    print(f"Deleted {lambda_function_name}.")
    for policy in iam_role.attached_policies.all():
        policy.detach_role(RoleName=iam_role.name)
    iam_role.delete()
    print(f"Deleted {iam_role.name}.")
    print("Thanks for watching!")


if __name__ == '__main__':
    usage_demo()
