# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that publishes messages to an
AWS IoT Greengrass connector.
"""

# snippet-start:[greengrass.python.connector-serial-stream-usage.complete]
import json
import greengrasssdk

iot_client = greengrasssdk.client('iot-data')
send_topic = 'serial/CORE_THING_NAME/write/dev/serial1'


def create_serial_stream_request():
    return {
        "data": "TEST",
        "type": "ascii",
        "id": "abc123"}


def publish_basic_message():
    iot_client.publish(
        topic=send_topic, payload=json.dumps(create_serial_stream_request()))


publish_basic_message()


# In this example, this dummy AWS Lambda handler is never called.
def function_handler(event, context):
    return
# snippet-end:[greengrass.python.connector-serial-stream-usage.complete]
