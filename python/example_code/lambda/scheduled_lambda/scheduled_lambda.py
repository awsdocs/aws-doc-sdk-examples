# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[scheduled_lambda.py demonstrates how a Lambda function can be invoked based on a time schedule.]
# snippet-service:[lambda]
# snippet-keyword:[AWS Lambda]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-07-29]
# snippet-sourceauthor:[AWS]

# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.


import argparse
import logging
import os
import boto3
from botocore.exceptions import ClientError
from lambda_util import create_lambda_function, delete_lambda_function


# Global configuration constants. Change as desired
REGION = 'us-west-2'
LAMBDA_NAME = 'ScheduledLambda'
LAMBDA_SRCFILE = 'lambda_function.py'
LAMBDA_HANDLER_NAME = 'lambda_handler'
LAMBDA_ROLE_NAME = 'scheduled-lambda-role'

EVENT_RULE_NAME = 'scheduled_lambda_rule'
EVENT_SCHEDULE = 'cron(0/1 * * * ? *)'  # Trigger every minute of every day


def create_scheduled_lambda_function(lambda_function_name,
                                     lambda_srcfile,
                                     event_rule_name, event_schedule,
                                     region):
    """Create an AWS Lambda function that is invoked on a scheduled basis

    :param lambda_function_name: Name of Lambda function
    :param lambda_srcfile: Lambda source code filename
    :param event_rule_name: Name of EventBridge scheduled rule
    :param event_schedule: cron-formatted event schedule
    :param region: Region in which to locate the Lambda function
    :return: ARN of the Lambda function. If error, return None.
    """

    # Create an EventBridge rule with the desired schedule
    events_client = boto3.client('events', region_name=region)
    try:
        response = events_client.put_rule(Name=event_rule_name,
                                          ScheduleExpression=event_schedule)
    except ClientError as e:
        logging.error(e)
        return None
    event_rule_arn = response['RuleArn']

    # Set the current working directory
    # Note: This is necessary so our Lambda srcfile will be found by the
    # lambda_util package which is located in another directory.
    os.chdir(os.path.dirname(os.path.abspath(__file__)))

    # Create the Lambda function (initially unscheduled)
    lambda_arn = create_lambda_function(lambda_function_name,
                                        lambda_srcfile,
                                        LAMBDA_HANDLER_NAME,
                                        LAMBDA_ROLE_NAME,
                                        region)
    if lambda_arn is None:
        return None

    # Grant invoke permissions on the Lambda function so it can be called by
    # EventBridge/CloudWatch Events.
    # Note: To retrieve the Lambda function's permissions, call
    # LambdaClient.get_policy()
    lambda_client = boto3.client('lambda', region_name=region)
    try:
        lambda_client.add_permission(FunctionName=lambda_function_name,
                                     StatementId=f'{lambda_function_name}-invoke',
                                     Action='lambda:InvokeFunction',
                                     Principal='events.amazonaws.com',
                                     SourceArn=event_rule_arn)
    except ClientError as e:
        logging.error(e)
        return None

    # Add the Lambda function as the target of the scheduled-event rule
    scheduled_lambda = [
        {
            'Id': lambda_function_name,
            'Arn': lambda_arn,
        }
    ]
    try:
        response = events_client.put_targets(Rule=event_rule_name,
                                             Targets=scheduled_lambda)
    except ClientError as e:
        logging.error(e)
        return None
    if response['FailedEntryCount'] != 0:
        logging.error(f'Could not set {lambda_function_name} as the target '
                      f'for {event_rule_name}')
        return None
    return lambda_arn


def toggle_scheduled_event(event_rule_name, region):
    """Toggle the enabled/disabled state of the scheduled-event rule

    :param event_rule_name: Name of event rule
    :param region: Region of EventBridge schedule rule
    :return: New state ('ENABLED' | 'DISABLED'). If error, return None.
    """

    # Retrieve the scheduled-event rule's current state
    events_client = boto3.client('events', region_name=region)
    try:
        response = events_client.describe_rule(Name=event_rule_name)
    except ClientError as e:
        logging.error(e)
        return None

    # Toggle state and return new state
    if response['State'] == 'ENABLED':
        try:
            events_client.disable_rule(Name=event_rule_name)
        except ClientError as e:
            logging.error(e)
            return None
        return 'DISABLED'

    try:
        events_client.enable_rule(Name=event_rule_name)
    except ClientError as e:
        logging.error(e)
        return None
    return 'ENABLED'


def delete_lambda_resources(region):
    """Delete the AWS resources allocated for the scheduled Lambda function

    :param region: Region in which Lambda function is located
    """

    # Remove Lambda target from the Events scheduled rule
    events_client = boto3.client('events', region_name=region)
    try:
        events_client.remove_targets(Rule=EVENT_RULE_NAME,
                                     Ids=[LAMBDA_NAME])
    except ClientError as e:
        logging.error(e)
    else:
        logging.info(f'Removed target {LAMBDA_NAME} '
                     f'from scheduled rule {EVENT_RULE_NAME}')

    # Delete the EventBridge rule
    try:
        events_client.delete_rule(Name=EVENT_RULE_NAME)
    except ClientError as e:
        logging.error(e)
    else:
        logging.info(f'Deleted EventBridge rule: {EVENT_RULE_NAME}')

    # Delete the scheduled Lambda function and its IAM role
    if delete_lambda_function(LAMBDA_NAME, LAMBDA_ROLE_NAME, region):
        logging.info(f'Deleted Lambda function: {LAMBDA_NAME}')


def main():
    """Exercise the lambda infrastructure methods"""

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Process command-line arguments
    arg_parser = argparse.ArgumentParser(description='Scheduled Lambda Example')
    arg_parser.add_argument('-d', '--delete', action='store_true',
                            help='delete allocated resources')
    arg_parser.add_argument('-t', '--toggle', action='store_true',
                            help='enable/disable scheduled event')
    args = arg_parser.parse_args()
    delete_resources = args.delete
    toggle_event = args.toggle

    # Delete the allocated Lambda resources?
    if delete_resources:
        delete_lambda_resources(REGION)
        exit(0)

    # Toggle the state of the scheduled event (enable/disable)
    if toggle_event:
        new_state = toggle_scheduled_event(EVENT_RULE_NAME, REGION)
        if new_state is not None:
            logging.info(f'Current scheduled event state: {new_state}')
        exit(0)

    # Create a Lambda function that is invoked on a schedule
    lambda_arn = create_scheduled_lambda_function(LAMBDA_NAME, LAMBDA_SRCFILE,
                                                  EVENT_RULE_NAME, EVENT_SCHEDULE,
                                                  REGION)
    if lambda_arn is not None:
        logging.info(f'Created scheduled Lambda function {LAMBDA_NAME}')
    exit(0)


if __name__ == '__main__':
    main()
