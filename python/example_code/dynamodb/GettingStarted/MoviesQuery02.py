# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to query for items from an Amazon DynamoDB table that stores movies
and projects the returned items so that a particular subset of data is returned.
Items are queried by a key expression that returns only items that were released
in a specified year and have titles that start within a specified range of the
alphabet.
Returned items are projected to return only year, title, genres, and the lead actor.
"""

# snippet-start:[dynamodb.python.codeexample.MoviesQuery02]
from pprint import pprint
import boto3
from boto3.dynamodb.conditions import Key


def query_and_project_movies(year, title_range, dynamodb=None):
    if not dynamodb:
        dynamodb = boto3.resource('dynamodb', endpoint_url="http://localhost:8000")

    table = dynamodb.Table('Movies')
    print(f"Get year, title, genres, and lead actor")

    # Expression attribute names can only reference items in the projection expression.
    response = table.query(
        ProjectionExpression="#yr, title, info.genres, info.actors[0]",
        ExpressionAttributeNames={"#yr": "year"},
        KeyConditionExpression=
            Key('year').eq(year) & Key('title').between(title_range[0], title_range[1])
    )
    return response['Items']


if __name__ == '__main__':
    query_year = 1992
    query_range = ('A', 'L')
    print(f"Get movies from {query_year} with titles from "
          f"{query_range[0]} to {query_range[1]}")
    movies = query_and_project_movies(query_year, query_range)
    for movie in movies:
        print(f"\n{movie['year']} : {movie['title']}")
        pprint(movie['info'])
# snippet-end:[dynamodb.python.codeexample.MoviesQuery02]
