# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that publishes messages to an
AWS IoT Greengrass connector.
"""

# snippet-start:[greengrass.python.connector-sns-usage.complete]
import json
import greengrasssdk

iot_client = greengrasssdk.client('iot-data')
send_topic = 'sns/message'


def create_request():
    return {
        "request": {
            "message": "Message from SNS Connector Test"},
        "id": "req_123"}


def publish_basic_message():
    message = create_request()
    print(f"Message to publish: {message}")
    iot_client.publish(topic=send_topic, payload=json.dumps(message))


# In this example, this dummy AWS Lambda handler is never called.
def function_handler(event, context):
    publish_basic_message()
# snippet-end:[greengrass.python.connector-sns-usage.complete]
