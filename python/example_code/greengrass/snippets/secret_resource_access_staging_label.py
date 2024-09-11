# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that uses AWS IoT Greengrass Core SDK
to get the specific version of a secret.
"""

# snippet-start:[greengrass.python.secret-resource-access-staging-label.complete]
import greengrasssdk

secrets_client = greengrasssdk.client("secretsmanager")
secret_name = "greengrass-TestSecret"
secret_version = "MyTargetLabel"


# Get the value of a specific secret version
def function_handler(event, context):
    response = secrets_client.get_secret_value(
        SecretId=secret_name, VersionStage=secret_version
    )
    response.get("SecretString")


# snippet-end:[greengrass.python.secret-resource-access-staging-label.complete]
