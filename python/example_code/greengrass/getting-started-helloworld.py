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
#snippet-start:[greengrass.python.getting-started-helloworld.complete]
import greengrasssdk
import platform
from threading import Timer
import time

client = greengrasssdk.client('iot-data')
my_platform = platform.platform()

#snippet-start:[greengrass.python.helloworld-publish-to-one-topic]
def greengrass_hello_world_run():
    if not my_platform:
        client.publish(topic='hello/world', payload='Hello world! Sent from Greengrass Core.')
    else:
        client.publish(topic='hello/world', payload='Hello world! Sent from Greengrass Core running on platform: {}'.format(my_platform))
    Timer(5, greengrass_hello_world_run).start()
#snippet-end:[greengrass.python.helloworld-publish-to-one-topic]

greengrass_hello_world_run()

def function_handler(event, context):
    return
#snippet-end:[greengrass.python.getting-started-helloworld.complete]
#
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:['Hello World' Lambda function sends MQTT messages. https://docs.aws.amazon.com/greengrass/latest/developerguide/module3-I.html ]
#snippet-keyword:[Python]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS IoT Greengrass]
#snippet-keyword:[AWS IoT Greengrass Core SDK]
#snippet-keyword:[iot-data client]
#snippet-keyword:[publish]
#snippet-service:[greengrass]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2019-01-02]
#snippet-sourceauthor:[AWS]