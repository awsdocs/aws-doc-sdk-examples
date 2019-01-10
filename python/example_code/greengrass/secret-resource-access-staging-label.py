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
#snippet-start:[greengrass.python.secret-resource-access-staging-label.complete]
import greengrasssdk
 
# Creating a greengrass core sdk client
client = greengrasssdk.client('secretsmanager')
 
# This handler is called when the function is invoked
# It uses the secretsmanager client to get the value of a secret
def function_handler(event, context):
    response = client.get_secret_value(SecretId='greengrass-MySecret-abc')
    raw_secret = response.get('SecretString')
#snippet-end:[greengrass.python.secret-resource-access-staging-label.complete]
#
#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[Accesses a specific version of a secret on the core. https://docs.aws.amazon.com/greengrass/latest/developerguide/secrets-using.html ]
#snippet-keyword:[Python]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS IoT Greengrass]
#snippet-keyword:[AWS IoT Greengrass Core SDK]
#snippet-keyword:[secretsmanager client]
#snippet-keyword:[get_secret_value]
#snippet-keyword:[Secret resource]
#snippet-service:[greengrass]
#snippet-sourcetype:[snippet]
#snippet-sourcedate:[2019-01-03]
#snippet-sourceauthor:[AWS]