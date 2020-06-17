# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to list all of the tables in an Amazon DynamoDB account.
"""

# snippet-start:[dynamodb.python.codeexample.MoviesListTables]
import boto3


def print_tables(dynamodb=None):
    if not dynamodb:
        dynamodb = boto3.resource('dynamodb', endpoint_url="http://localhost:8000")

    for table in dynamodb.tables.all():
        print(table.name)


if __name__ == '__main__':
    print_tables()
# snippet-end:[dynamodb.python.codeexample.MoviesListTables]
