# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.rds.hello]
"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Amazon Relational Database Service
(Amazon RDS) to list the databases in your account.
"""

import boto3
from botocore.exceptions import ClientError

# Create an RDS client
rds_client = boto3.client('rds')

try:
    # Get a list of DB instances
    response = rds_client.describe_db_instances()

    # Check if any instances are returned and print the appropriate message
    if 'DBInstances' in response and response['DBInstances']:
        print("Your RDS instances are:")
        for db in response['DBInstances']:
            print(db['DBInstanceIdentifier'])
    else:
        print("No RDS instances found!")

except ClientError as e:
    print(f"Couldn't list RDS instances. Here's why: {e.response['Error']['Message']}")

# snippet-end:[python.example_code.rds.hello]
