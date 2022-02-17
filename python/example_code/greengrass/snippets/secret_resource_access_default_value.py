# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that uses AWS IoT Greengrass Core SDK
to get a secret.
"""

# snippet-start:[greengrass.python.secret-resource-access-default-value.complete]
import greengrasssdk

secrets_client = greengrasssdk.client('secretsmanager')
secret_name = 'greengrass-MySecret-abc'


def function_handler(event, context):
    response = secrets_client.get_secret_value(SecretId=secret_name)
    secret = response.get('SecretString')
# snippet-end:[greengrass.python.secret-resource-access-default-value.complete]
