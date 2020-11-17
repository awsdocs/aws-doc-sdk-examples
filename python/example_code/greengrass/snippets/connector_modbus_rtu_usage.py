# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that publishes messages to an
AWS IoT Greengrass connector.
"""

# snippet-start:[greengrass.python.connector-modbus-rtu-usage.complete]
import json
import greengrasssdk

iot_client = greengrasssdk.client('iot-data')
send_topic = 'modbus/adapter/request'


def create_read_coils_request():
    return {
        "request": {
            "operation": "ReadCoilsRequest",
            "device": 1,
            "address": 0x01,
            "count": 1},
        "id": "TestRequest"}


def publish_basic_message():
    iot_client.publish(
        topic=send_topic, payload=json.dumps(create_read_coils_request()))


publish_basic_message()


# In this example, the required AWS Lambda handler is never called.
def function_handler(event, context):
    return
# snippet-end:[greengrass.python.connector-modbus-rtu-usage.complete]
