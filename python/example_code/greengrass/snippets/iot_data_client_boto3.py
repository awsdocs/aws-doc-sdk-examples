# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS IoT Greengrass to publish a
message to a topic.
"""

# snippet-start:[greengrass.python.iot-data-client-boto3.complete]
import boto3

iot_client = boto3.client('iot-data')
response = iot_client.publish(
    topic='some/topic',
    qos=0,
    payload='Some payload'.encode()
)
# snippet-end:[greengrass.python.iot-data-client-boto3.complete]
