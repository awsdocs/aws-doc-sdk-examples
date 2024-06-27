# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.s3.Hello]

import boto3

def hello_s3():
    """
    Demonstrates how to list the Amazon S3 buckets in your AWS account using the AWS SDK for Python.
    """
    # Create an S3 client
    s3 = boto3.client('s3')

    # Use the S3 client to list the buckets
    response = s3.list_buckets()

    # Print the names of all the buckets
    print(f"Your Amazon S3 buckets are:")
    for bucket in response['Buckets']:
        print(f"  {bucket['Name']}")

if __name__ == '__main__':
    hello_s3()

# snippet-end:[python.example_code.s3.Hello]