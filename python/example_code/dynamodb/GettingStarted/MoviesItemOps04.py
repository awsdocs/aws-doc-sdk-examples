# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to increment a counter in an item in an Amazon DynamoDB table
that stores movies.
The update is performed using an update expression that defines how a
movie's rating counter is changed during the update.
"""

# snippet-start:[dynamodb.python.codeexample.MoviesItemOps04]
from decimal import Decimal
from pprint import pprint
import boto3


def increase_rating(title, year, rating_increase, dynamodb=None):
    if not dynamodb:
        dynamodb = boto3.resource('dynamodb', endpoint_url="http://localhost:8000")

    table = dynamodb.Table('Movies')

    response = table.update_item(
        Key={
            'year': year,
            'title': title
        },
        UpdateExpression="set info.rating = info.rating + :val",
        ExpressionAttributeValues={
            ':val': Decimal(rating_increase)
        },
        ReturnValues="UPDATED_NEW"
    )
    return response


if __name__ == '__main__':
    update_response = increase_rating("The Big New Movie", 2015, 1)
    print("Update movie succeeded:")
    pprint(update_response, sort_dicts=False)
# snippet-end:[dynamodb.python.codeexample.MoviesItemOps04]
