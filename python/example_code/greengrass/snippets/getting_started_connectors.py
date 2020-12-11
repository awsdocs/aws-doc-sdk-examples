# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that publishes messages to an
AWS IoT Greengrass connector.
"""

# snippet-start:[greengrass.python.getting-started-connectors.complete]
import json
import random
import greengrasssdk

iot_client = greengrasssdk.client('iot-data')
send_topic = 'twilio/txt'


def create_request(event):
    return {
        "request": {
            "recipient": {
                "name": event['to_name'],
                "phone_number": event['to_number'],
                "message": f"temperature:{event['temperature']}"}},
        "id": f"request_{random.randint(1, 101)}"}


# Publish to the Twilio Notifications connector through the twilio/txt topic.
def function_handler(event, context):
    temperature = event['temperature']

    # If temperature is greater than 30C, send a notification.
    if temperature > 30:
        message = create_request(event)
        iot_client.publish(topic='twilio/txt', payload=json.dumps(message))
        print(f'Published: {message}')

    print(f'Temperature: {temperature}')
# snippet-end:[greengrass.python.getting-started-connectors.complete]
