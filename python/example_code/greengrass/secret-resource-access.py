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
# https://docs.aws.amazon.com/greengrass/latest/developerguide/secrets-console.html
#
# snippet-start:[greengrass.python.secret-resource-access.complete]
import greengrasssdk
 
# Create SDK clients.
secrets_client = greengrasssdk.client('secretsmanager')
message_client = greengrasssdk.client('iot-data')
message = ''

# This handler is called when the function is invoked.
# It uses the 'secretsmanager' client to get the value of the test secret using the secret name.
# The test secret is a text type, so the SDK returns a string. 
# For binary secret values, the SDK returns a base64-encoded string.
def function_handler(event, context):
    response = secrets_client.get_secret_value(SecretId='greengrass-TestSecret')
    secret_value = response.get('SecretString')
    if secret_value is None:
        message = 'Failed to retrieve secret.'
    else:
        message = 'Success! Retrieved secret.'
    
    message_client.publish(topic='secrets/output', payload=message)
    print('published: ' + message)
# snippet-end:[greengrass.python.secret-resource-access.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[Accesses a secret resource on the core.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AWS IoT Greengrass]
# snippet-keyword:[AWS IoT Greengrass Core SDK]
# snippet-keyword:[secretsmanager client]
# snippet-keyword:[get_secret_value]
# snippet-keyword:[iot-data client]
# snippet-keyword:[publish]
# snippet-keyword:[Secret resource]
# snippet-service:[greengrass]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-03]
# snippet-sourceauthor:[AWS]