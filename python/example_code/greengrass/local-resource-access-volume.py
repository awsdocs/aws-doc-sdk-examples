# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.
#
#snippet-start:[greengrass.python.local-resource-access-volume.complete]
# Demonstrates a simple use case of local resource access.
# This Lambda function writes a file "test" to a volume mounted inside
# the Lambda environment under "/dest/LRAtest". Then it reads the file and 
# publishes the content to the AWS IoT "LRA/test" topic. 

import sys
import greengrasssdk
import platform
import os
import logging

# Create a Greengrass Core SDK client.
client = greengrasssdk.client('iot-data')
volumePath = '/dest/LRAtest'

def function_handler(event, context):
    client.publish(topic='LRA/test', payload='Sent from AWS IoT Greengrass Core.')
    try:
        volumeInfo = os.stat(volumePath)
        client.publish(topic='LRA/test', payload=str(volumeInfo))
        with open(volumePath + '/test', 'a') as output:
            output.write('Successfully write to a file.\n')
        with open(volumePath + '/test', 'r') as myfile:
            data = myfile.read()
        client.publish(topic='LRA/test', payload=data)
    except Exception as e:
        logging.error("Experiencing error :{}".format(e))
    return
#snippet-end:[greengrass.python.local-resource-access-volume.complete]
#
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.].]
#snippet-sourcedescription:[Accesses a local volume resource. <ulink url="&url-gg-dev;lra-console.html">How to Configure Local Resource Access Using the AWS Management Console</ulink>]
#snippet-keyword:[Python]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS IoT Greengrass]
#snippet-keyword:[AWS IoT Greengrass Core SDK]
#snippet-keyword:[iot-data client]
#snippet-keyword:[publish]
#snippet-keyword:[Local resource]
#snippet-service:[greengrass]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2019-01-02]
#snippet-sourceauthor:[AWS]
