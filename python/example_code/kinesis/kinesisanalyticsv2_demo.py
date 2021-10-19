# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Kinesis and version 2 of
the Amazon Kinesis Data Analytics API to create an application that reads data from
an input stream, uses SQL code to transform the data, and writes it to an output
stream.
"""

import logging
from pprint import pprint
import sys
import threading
import time
import boto3

from analyticsv2.analytics_application import KinesisAnalyticsApplicationV2
from streams.kinesis_stream import KinesisStream
from streams.dg_anomaly import generate

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append('../..')
from demo_tools.custom_waiter import CustomWaiter, WaitState
from demo_tools.retries import exponential_retry

logger = logging.getLogger(__name__)


class ApplicationRunningWaiter(CustomWaiter):
    """
    Waits for the application to be in a running state.
    """
    def __init__(self, client):
        super().__init__(
            'ApplicationRunning', 'DescribeApplication',
            'ApplicationDetail.ApplicationStatus',
            {'RUNNING': WaitState.SUCCESS, 'STOPPING': WaitState.FAILURE},
            client)

    def wait(self, app_name):
        self._wait(ApplicationName=app_name)


# snippet-start:[python.example_code.kinesis-analytics-v2.Scenario_SqlTransform]
def usage_demo():
    print('-'*88)
    print("Welcome to the demo of version 2 of the Amazon Kinesis Data Analytics API.")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    kinesis_client = boto3.client('kinesis')
    analytics_client = boto3.client('kinesisanalyticsv2')
    iam_resource = boto3.resource('iam')
    application = KinesisAnalyticsApplicationV2(analytics_client)
    app_running_waiter = ApplicationRunningWaiter(analytics_client)

    input_stream_name = 'doc-example-stream-input'
    input_prefix = 'SOURCE_SQL_STREAM'
    output_stream_name = 'doc-example-stream-output'
    app_name = 'doc-example-app'
    role_name = 'doc-example-kinesis-read-write'

    print(f"Creating input stream {input_stream_name} and output stream "
          f"{output_stream_name}.")
    input_stream = KinesisStream(kinesis_client)
    input_stream.create(input_stream_name)
    output_stream = KinesisStream(kinesis_client)
    output_stream.create(output_stream_name)

    print("Starting data generator (on a separate thread) to put data into the "
          "input stream.")
    stream_thread = threading.Thread(
        target=generate, args=(input_stream.name, kinesis_client, False), daemon=True)
    stream_thread.start()

    print(f"Creating role {role_name} to let Kinesis Analytics read from the input "
          f"stream and write to the output stream.")
    role = application.create_read_write_role(
        role_name, input_stream.arn(), output_stream.arn(), iam_resource)
    print("Waiting for role to be ready.")
    time.sleep(10)

    print(f"Creating application {app_name}.")
    # Sometimes the role is still not ready and InvalidArgumentException is raised, so
    # continue to retry if this happens.
    app_data = exponential_retry('InvalidArgumentException')(
        application.create)(app_name, role.arn)
    pprint(app_data)
    print(f"Discovering schema of input stream {input_stream.name}.")
    input_schema = application.discover_input_schema(input_stream.arn(), role.arn)
    pprint(input_schema)

    print("Adding input stream to the application.")
    input_details = application.add_input(
        input_prefix, input_stream.arn(), input_schema)
    print("Input details:")
    pprint(input_details)

    print("Uploading SQL code to the application to process the input stream.")
    with open('analyticsv2/example.sql') as code_file:
        code = code_file.read()
    application.update_code(code)

    print("Adding output stream to the application.")
    application.add_output('DESTINATION_SQL_STREAM', output_stream.arn())

    print("Starting the application.")
    application.start(input_details['InputDescriptions'][0]['InputId'])
    print("Waiting for the application to start (this may take a minute or two).")
    app_running_waiter.wait(application.name)

    print("Application started. Getting records from the output stream.")
    for records in output_stream.get_records(50):
        if len(records) > 0:
            print(*[rec['Data'].decode() for rec in records], sep='\n')

    print("Cleaning up...")
    application.delete()
    input_stream.delete()
    output_stream.delete()
    print("Deleting read/write role.")
    for policy in role.attached_policies.all():
        role.detach_policy(PolicyArn=policy.arn)
        policy.delete()
    role.delete()
    print("Thanks for watching!")
    print('-'*88)
# snippet-end:[python.example_code.kinesis-analytics-v2.Scenario_SqlTransform]


if __name__ == '__main__':
    usage_demo()
