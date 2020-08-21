# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to delete an Amazon DynamoDB table.
"""

# snippet-start:[dynamodb.python.codeexample.MoviesDeleteTable]
import boto3

def delete_movie_table(dynamodb=None):
    if not dynamodb:
        dynamodb = boto3.resource('dynamodb', endpoint_url="http://localhost:8000")

    table = dynamodb.Table('Movies')
    table.delete()


if __name__ == '__main__':
    delete_movie_table()
    print("Movies table deleted.")
# snippet-end:[dynamodb.python.codeexample.MoviesDeleteTable]
