# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that publishes messages to an
AWS IoT Greengrass connector.
"""

# snippet-start:[greengrass.python.connector-twilio-notifications-usage.complete]
import json
import greengrasssdk

iot_client = greengrasssdk.client('iot-data')
txt_input_topic = 'twilio/txt'


def publish_basic_message():
    message = {
        "request": {
            "recipient" : {
                "name": "Darla",
                "phone_number": "+12345000000",
                "message": 'Hello from the edge'},
            "from_number" : "+19999999999"},
        "id": "request123"}
    print(f"Message to publish: {message}")
    iot_client.publish(topic=txt_input_topic, payload=json.dumps(message))


publish_basic_message()


# In this example, the required AWS Lambda handler is never called.
def function_handler(event, context):
    return
# snippet-end:[greengrass.python.connector-twilio-notifications-usage.complete]
