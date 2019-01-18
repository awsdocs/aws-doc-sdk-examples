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
# https://docs.aws.amazon.com/greengrass/latest/developerguide/module3-II.html
#
# snippet-start:[greengrass.python.getting-started-helloworldcounter.complete]
import greengrasssdk
import platform
import time
import json

client = greengrasssdk.client('iot-data')

my_platform = platform.platform()

my_counter = 0

def function_handler(event, context):
    global my_counter
    my_counter = my_counter + 1
    if not my_platform:
        client.publish(
            topic='hello/world/counter',
            payload=json.dumps({'message': 'Hello world! Sent from Greengrass Core.  Invocation Count: {}'.format(my_counter)})
        )
    else:
        client.publish(
            topic='hello/world/counter',
            payload=json.dumps({'message': 'Hello world! Sent from Greengrass Core running on platform: {}.  Invocation Count: {}'
                                .format(my_platform, my_counter)})
        )
    time.sleep(20)
    return
# snippet-end:[greengrass.python.getting-started-helloworldcounter.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:['Hello World Counter' Lambda function sends MQTT messages.]
# snippet-keyword:[Python]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AWS IoT Greengrass]
# snippet-keyword:[AWS IoT Greengrass Core SDK]
# snippet-keyword:[iot-data client]
# snippet-keyword:[publish]
# snippet-service:[greengrass]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-02]
# snippet-sourceauthor:[AWS]