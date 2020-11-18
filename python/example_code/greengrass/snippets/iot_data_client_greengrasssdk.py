# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS IoT Greengrass Core SDK to publish a
message to a topic.
"""

# snippet-start:[greengrass.python.iot-data-client-greengrasssdk.complete]
import greengrasssdk

iot_client = greengrasssdk.client('iot-data')
iot_client.publish(
    topic='some/topic',
    qos=0,
    payload='Some payload'.encode()
)
# snippet-end:[greengrass.python.iot-data-client-greengrasssdk.complete]
