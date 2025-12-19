# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to get started with AWS IoT by describing the IoT endpoint.
"""

import boto3
from botocore.exceptions import ClientError, NoCredentialsError


# snippet-start:[python.example_code.iot.Hello]
def hello_iot():
    """
    Use the AWS SDK for Python (Boto3) to create an AWS IoT client and describe
    the IoT endpoint for your account.
    This example uses the default settings specified in your shared credentials
    and config files.
    """
    try:
        iot_client = boto3.client("iot")
        response = iot_client.describe_endpoint(endpointType="iot:Data-ATS")
        endpoint = response["endpointAddress"]
        print(f"Hello, AWS IoT! Your endpoint is: {endpoint}")
    except ClientError as e:
        if e.response["Error"]["Code"] == "UnauthorizedException":
            print("You don't have permission to access AWS IoT.")
        else:
            print(f"Couldn't access AWS IoT. Error: {e}")
    except NoCredentialsError:
        print("No AWS credentials found. Please configure your credentials.")
    except Exception as e:
        print(f"An unexpected error occurred: {e}")


# snippet-end:[python.example_code.iot.Hello]


if __name__ == "__main__":
    hello_iot()
