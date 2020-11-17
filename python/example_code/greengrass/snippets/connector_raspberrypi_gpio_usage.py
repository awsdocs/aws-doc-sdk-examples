# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that publishes messages to an
AWS IoT Greengrass connector.
"""

# snippet-start:[greengrass.python.connector-raspberrypi-gpio-usage.complete]
import os
import greengrasssdk

iot_client = greengrasssdk.client('iot-data')
INPUT_GPIOS = [6, 17, 22]
thing_name = os.environ['AWS_IOT_THING_NAME']


def publish_basic_message():
    for gpio_num in INPUT_GPIOS:
        topic = '/'.join(['gpio', thing_name, str(gpio_num), 'read'])
        iot_client.publish(topic=topic, payload=f'Hello from GPIO {gpio_num}!')


publish_basic_message()


# In this example, the required AWS Lambda handler is never called.
def function_handler(event, context):
    return
# snippet-end:[greengrass.python.connector-raspberrypi-gpio-usage.complete]
