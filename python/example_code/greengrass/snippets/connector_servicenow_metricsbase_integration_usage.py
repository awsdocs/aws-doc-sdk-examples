# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that publishes messages to an
AWS IoT Greengrass connector.
"""

# snippet-start:[greengrass.python.connector-servicenow-metricsbase-integration-usage.complete]
import json
import greengrasssdk

iot_client = greengrasssdk.client('iot-data')
send_topic = 'servicenow/metricbase/metric'


def create_request():
    return {
        "request": {
             "subject": '2efdf6badbd523803acfae441b961961',
             "metric_name": 'u_count',
             "value": 1234,
             "timestamp": '2018-10-20T20:22:20',
             "table": 'u_greengrass_metricbase_test'}}


def publish_basic_message():
    message = create_request()
    print(f"Message to publish: {message}")
    iot_client.publish(topic=send_topic, payload=json.dumps(message))


publish_basic_message()


# In this example, the required AWS Lambda handler is never called.
def function_handler(event, context):
    return
# snippet-end:[greengrass.python.connector-servicenow-metricsbase-integration-usage.complete]
