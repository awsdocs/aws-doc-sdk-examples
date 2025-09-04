# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to get started with AWS Config.
This is a simple example that demonstrates basic connectivity to the service.
"""

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.config-service.Hello]
def hello_config(config_client):
    """
    Use the AWS SDK for Python (Boto3) to create an AWS Config client and list
    any existing configuration recorders in your account.
    This example uses the default settings specified in your shared credentials
    and config files.

    :param config_client: A Boto3 AWS Config client.
    """
    print("Hello, AWS Config! Let's list your configuration recorders:")
    try:
        response = config_client.describe_configuration_recorders()
        recorders = response.get('ConfigurationRecorders', [])
        
        if recorders:
            print(f"Found {len(recorders)} configuration recorder(s):")
            for recorder in recorders:
                print(f"  - {recorder['name']} (Status: {'Recording' if recorder.get('recordingGroup', {}).get('allSupported', False) else 'Limited'})")
        else:
            print("No configuration recorders found in your account.")
            print("You can create one using the AWS Config basics scenario.")
            
    except ClientError as err:
        if err.response['Error']['Code'] == 'AccessDeniedException':
            print("You don't have permission to access AWS Config.")
            print("Make sure your AWS credentials have the necessary Config permissions.")
        else:
            logger.error(
                "Couldn't list configuration recorders. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise


# snippet-end:[python.example_code.config-service.Hello]


def main():
    config_client = boto3.client('config')
    hello_config(config_client)


if __name__ == '__main__':
    main()