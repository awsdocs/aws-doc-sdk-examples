# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function running in AWS IoT Greengrass core
that accesses local file system resources.
"""

# snippet-start:[greengrass.python.local-resource-access-volume.complete]
import logging
import os
import greengrasssdk

iot_client = greengrasssdk.client('iot-data')
volume_path = '/dest/LRAtest'


def function_handler(event, context):
    """
    Shows how to access local resources in an AWS Lambda function.
    Gets volume information for the local file system and publishes it.
    Writes a file named 'test' and then reads the file and publishes its contents.
    """
    iot_client.publish(topic='LRA/test', payload='Sent from AWS IoT Greengrass Core.')
    try:
        volume_info = os.stat(volume_path)
        iot_client.publish(topic='LRA/test', payload=str(volume_info))
        with open(volume_path + '/test', 'a') as output:
            output.write('Successfully write to a file.\n')
        with open(volume_path + '/test', 'r') as file:
            data = file.read()
        iot_client.publish(topic='LRA/test', payload=data)
    except Exception as err:
        logging.exception("Got error : %s", err)
# snippet-end:[greengrass.python.local-resource-access-volume.complete]
