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
# https://docs.aws.amazon.com/greengrass/latest/developerguide/modbus-protocol-adapter-connector.html
#
#snippet-start:[greengrass.python.connector-modbus-rtu-usage.complete]
import greengrasssdk
import json

TOPIC_REQUEST = 'modbus/adapter/request'

# Creating a greengrass core sdk client
iot_client = greengrasssdk.client('iot-data')

def create_read_coils_request():
	request = {
		"request": {
			"operation": "ReadCoilsRequest",
			"device": 1,
			"address": 0x01,
			"count": 1
		},
		"id": "TestRequest"
	}
	return request

def publish_basic_request():
	iot_client.publish(payload=json.dumps(create_read_coils_request()), topic=TOPIC_REQUEST)

publish_basic_request()

def function_handler(event, context):
	return
#snippet-end:[greengrass.python.connector-modbus-rtu-usage.complete]
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[Sends an input message to the Modbus-RTU Protocol Adapter connector.]
#snippet-keyword:[Python]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS IoT Greengrass]
#snippet-keyword:[AWS IoT Greengrass Core SDK]
#snippet-keyword:[iot-data client]
#snippet-keyword:[publish]
#snippet-keyword:[Greengrass connector]
#snippet-service:[greengrass]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2019-02-19]
#snippet-sourceauthor:[AWS]