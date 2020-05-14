# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to update an item in an Amazon DynamoDB table that stores movies.
The update is performed in two steps.
    1. The item is retrieved by using its primary and secondary keys.
    2. The item is updated on the client and put into the table with updated data.
The item is retrieved again to verify the update was made as expected.
"""

# snippet-start:[dynamodb.python.codeexample.MoviesItemOps03a]
from decimal import Decimal
from pprint import pprint
import boto3


def update_movie(title, year, rating, plot, actors, dynamodb=None):
    if not dynamodb:
        dynamodb = boto3.resource('dynamodb', endpoint_url="http://localhost:8000")

    table = dynamodb.Table('Movies')

    resp = table.get_item(Key={'year': year, 'title': title})
    item = resp['Item']
    item['info']['rating'] = Decimal(rating)
    item['info']['plot'] = plot
    item['info']['actors'] = actors

    table.put_item(Item=item)
    return table.get_item(Key={'year': year, 'title': title})['Item']


if __name__ == '__main__':
    movie = update_movie(
        "The Big New Movie", 2015, 5.5, "Everything happens all at once.",
        ["Larry", "Moe", "Curly"])
    print("Update movie succeeded:")
    pprint(movie, sort_dicts=False)
# snippet-end:[dynamodb.python.codeexample.MoviesItemOps03a]
