# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use a condition to control whether an update is performed on an item
in an Amazon DynamoDB table that stores movies.
The update removes the actors from a movie only when the number of actors is greater
than the specified count. Otherwise, the update is rejected and an error is raised.
"""

# snippet-start:[dynamodb.python.codeexample.MoviesItemOps05]
from pprint import pprint
import boto3
from botocore.exceptions import ClientError


def remove_actors(title, year, actor_count, dynamodb=None):
    if not dynamodb:
        dynamodb = boto3.resource('dynamodb', endpoint_url="http://localhost:8000")

    table = dynamodb.Table('Movies')

    try:
        response = table.update_item(
            Key={
                'year': year,
                'title': title
            },
            UpdateExpression="remove info.actors[0]",
            ConditionExpression="size(info.actors) > :num",
            ExpressionAttributeValues={':num': actor_count},
            ReturnValues="UPDATED_NEW"
        )
    except ClientError as e:
        if e.response['Error']['Code'] == "ConditionalCheckFailedException":
            print(e.response['Error']['Message'])
        else:
            raise
    else:
        return response


if __name__ == '__main__':
    print("Attempting conditional update (expecting failure)...")
    update_response = remove_actors("The Big New Movie", 2015, 3)
    if update_response:
        print("Update movie succeeded:")
        pprint(update_response, sort_dicts=False)
# snippet-end:[dynamodb.python.codeexample.MoviesItemOps05]
