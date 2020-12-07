# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that publishes messages to an
AWS IoT Greengrass connector.
"""

# snippet-start:[greengrass.python.connector-cloudwatch-metrics-usage.complete]
import json
from threading import Timer
import time
import greengrasssdk

iot_client = greengrasssdk.client('iot-data')
send_topic = 'cloudwatch/metric/put'


def create_request():
    return {
        "request": {
            "namespace": "Greengrass_CW_Connector",
            "metricData": {
                "metricName": "Count1",
                "dimensions": [{"name": "test", "value": "test"}],
                "value": 1,
                "unit": "Seconds",
                "timestamp": time.time()}}}


def publish_basic_message():
    message = create_request()
    print(f"Message to publish: {message}")
    iot_client.publish(topic=send_topic, payload=json.dumps(message))
    Timer(5, publish_basic_message).start()


publish_basic_message()


# In this example, the required AWS Lambda handler is never called.
def function_handler(event, context):
    return
# snippet-end:[greengrass.python.connector-cloudwatch-metrics-usage.complete]
