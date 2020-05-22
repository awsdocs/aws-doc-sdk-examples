# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to load items into an Amazon DynamoDB table that stores movies.
The items are retrieved from a JSON-formatted file and put directly into the table.
This works because every item contains the required year and title keys. All
additional data is added as non-key attributes for the item.
"""

# snippet-start:[dynamodb.python.codeexample.MoviesLoadData]
from decimal import Decimal
import json
import boto3


def load_movies(movies, dynamodb=None):
    if not dynamodb:
        dynamodb = boto3.resource('dynamodb', endpoint_url="http://localhost:8000")

    table = dynamodb.Table('Movies')
    for movie in movies:
        year = int(movie['year'])
        title = movie['title']
        print("Adding movie:", year, title)
        table.put_item(Item=movie)


if __name__ == '__main__':
    with open("moviedata.json") as json_file:
        movie_list = json.load(json_file, parse_float=Decimal)
    load_movies(movie_list)
# snippet-end:[dynamodb.python.codeexample.MoviesLoadData]
