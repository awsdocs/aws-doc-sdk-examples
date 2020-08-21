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
# https://docs.aws.amazon.com/greengrass/latest/developerguide/raspberrypi-gpio-connector.html
#
# snippet-start:[greengrass.python.connector-raspberrypi-gpio-usage.complete]
import greengrasssdk
import json

iot_client = greengrasssdk.client('iot-data')
INPUT_GPIOS = [6, 17, 22]

thingName = os.environ['AWS_IOT_THING_NAME']

def get_read_topic(gpio_num):
    return '/'.join(['gpio', thingName, str(gpio_num), 'read'])

def get_write_topic(gpio_num):
    return '/'.join(['gpio', thingName, str(gpio_num), 'write'])

def send_message_to_connector(topic, message=''):
    iot_client.publish(topic=topic, payload=str(message))

def set_gpio_state(gpio, state):
    send_message_to_connector(get_write_topic(gpio), str(state))

def read_gpio_state(gpio):
    send_message_to_connector(get_read_topic(gpio))

def publish_basic_message():
    for i in INPUT_GPIOS:
    	read_gpio_state(i)

publish_basic_message()

def function_handler(event, context):
    return
# snippet-end:[greengrass.python.connector-raspberrypi-gpio-usage.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[Sends an input message to the Raspberry Pi GPIO connector.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AWS IoT Greengrass]
# snippet-keyword:[AWS IoT Greengrass Core SDK]
# snippet-keyword:[iot-data client]
# snippet-keyword:[publish]
# snippet-keyword:[Greengrass connector]
# snippet-service:[greengrass]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-02]
# snippet-sourceauthor:[AWS]