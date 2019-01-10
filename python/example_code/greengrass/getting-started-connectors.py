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
# This sample is used in the AWS IoT Greengrass Developer Guide: 
# https://docs.aws.amazon.com/greengrass/latest/developerguide/connectors-console.html
#
#snippet-start:[greengrass.python.getting-started-connectors.complete]
from __future__ import print_function
import greengrasssdk
import json
import random

client = greengrasssdk.client('iot-data')

# publish to the Twilio Notifications connector through the twilio/txt topic
def function_handler(event, context):
    temp = event['temperature']
    
    # check the temperature
    # if greater than 30C, send a notification
    if temp > 30:
        data = build_request(event)
        client.publish(topic='twilio/txt', payload=json.dumps(data))
        print('published:' + str(data))
        
    print('temperature:' + str(temp))
    return

# build the Twilio request from the input data
def build_request(event):
    to_name = event['to_name']
    to_number = event['to_number']
    temp_report = 'temperature:' + str(event['temperature'])

    return {
        "request": {
            "recipient": {
                "name": to_name,
                "phone_number": to_number,
                "message": temp_report
            }
        },
        "id": "request_" + str(random.randint(1,101))
    }
#snippet-end:[greengrass.python.getting-started-connectors.complete]
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[Sends an input message to the Twilio Notifications connector.]
#snippet-keyword:[Python]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS IoT Greengrass]
#snippet-keyword:[AWS IoT Greengrass Core SDK]
#snippet-keyword:[iot-data client]
#snippet-keyword:[publish]
#snippet-keyword:[Greengrass connector]
#snippet-service:[greengrass]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2019-01-03]
#snippet-sourceauthor:[AWS]