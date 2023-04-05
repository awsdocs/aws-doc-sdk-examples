# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.support.Hello]
import boto3

def hello_support(support_client):
    """
    Use the AWS SDK for Python (Boto3) to create an AWS Support client and count
    the available services in your account.
    This example uses the default settings specified in your shared credentials
    and config files.

    :param support_client: A Boto3 Support Client object.
    """
    print("Hello, AWS Support! Let's count the available Support services:")
    response = support_client.describe_services()
    print(f"There are {len(response['services'])} services available.")

if __name__ == '__main__':
    hello_support(boto3.client('support'))
# snippet-end:[python.example_code.support.Hello]
