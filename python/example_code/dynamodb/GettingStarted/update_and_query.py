# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon DynamoDB to
perform more advanced actions, such as arithmetic and conditional updates. You can also
run queries with multiple conditions that are combined with projection expressions.
"""

from decimal import Decimal
import logging
from pprint import pprint
import boto3
from boto3.dynamodb.conditions import Key
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.dynamodb.helper.UpdateQueryWrapper.decl]
class UpdateQueryWrapper:
    def __init__(self, table):
        self.table = table
# snippet-end:[python.example_code.dynamodb.helper.UpdateQueryWrapper.decl]

    # snippet-start:[python.example_code.dynamodb.UpdateItem.UpdateExpression.Arithmetic]
    def update_rating(self, title, year, rating_change):
        """
        Updates the quality rating of a movie in the table by using an arithmetic
        operation in the update expression. By specifying an arithmetic operation,
        you can adjust a value in a single request, rather than first getting its
        value and then setting its new value.

        :param title: The title of the movie to update.
        :param year: The release year of the movie to update.
        :param rating_change: The amount to add to the current rating for the movie.
        :return: The updated rating.
        """
        try:
            response = self.table.update_item(
                Key={'year': year, 'title': title},
                UpdateExpression="set info.rating = info.rating + :val",
                ExpressionAttributeValues={':val': Decimal(str(rating_change))},
                ReturnValues="UPDATED_NEW")
        except ClientError as err:
            logger.error(
                "Couldn't update movie %s in table %s. Here's why: %s: %s",
                title, self.table.name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response['Attributes']
    # snippet-end:[python.example_code.dynamodb.UpdateItem.UpdateExpression.Arithmetic]

    # snippet-start:[python.example_code.dynamodb.UpdateItem.ConditionalExpression]
    def remove_actors(self, title, year, actor_threshold):
        """
        Removes an actor from a movie, but only when the number of actors is greater
        than a specified threshold. If the movie does not list more than the threshold,
        no actors are removed.

        :param title: The title of the movie to update.
        :param year: The release year of the movie to update.
        :param actor_threshold: The threshold of actors to check.
        :return: The movie data after the update.
        """
        try:
            response = self.table.update_item(
                Key={'year': year, 'title': title},
                UpdateExpression="remove info.actors[0]",
                ConditionExpression="size(info.actors) > :num",
                ExpressionAttributeValues={':num': actor_threshold},
                ReturnValues="ALL_NEW")
        except ClientError as err:
            if err.response['Error']['Code'] == "ConditionalCheckFailedException":
                logger.warning(
                    "Didn't update %s because it has fewer than %s actors.",
                    title, actor_threshold + 1)
            else:
                logger.error(
                    "Couldn't update movie %s. Here's why: %s: %s", title,
                    err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response['Attributes']
    # snippet-end:[python.example_code.dynamodb.UpdateItem.ConditionalExpression]

    # snippet-start:[python.example_code.dynamodb.DeleteItem.ConditionExpression]
    def delete_underrated_movie(self, title, year, rating):
        """
        Deletes a movie only if it is rated below a specified value. By using a
        condition expression in a delete operation, you can specify that an item is
        deleted only when it meets certain criteria.

        :param title: The title of the movie to delete.
        :param year: The release year of the movie to delete.
        :param rating: The rating threshold to check before deleting the movie.
        """
        try:
            self.table.delete_item(
                Key={'year': year, 'title': title},
                ConditionExpression="info.rating <= :val",
                ExpressionAttributeValues={":val": Decimal(str(rating))})
        except ClientError as err:
            if err.response['Error']['Code'] == "ConditionalCheckFailedException":
                logger.warning(
                    "Didn't delete %s because its rating is greater than %s.",
                    title, rating)
            else:
                logger.error(
                    "Couldn't delete movie %s. Here's why: %s: %s", title,
                    err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.dynamodb.DeleteItem.ConditionExpression]

    # snippet-start:[python.example_code.dynamodb.Query.ProjectionExpression]
    def query_and_project_movies(self, year, title_bounds):
        """
        Query for movies that were released in a specified year and that have titles
        that start within a range of letters. A projection expression is used
        to return a subset of data for each movie.

        :param year: The release year to query.
        :param title_bounds: The range of starting letters to query.
        :return: The list of movies.
        """
        try:
            response = self.table.query(
                ProjectionExpression="#yr, title, info.genres, info.actors[0]",
                ExpressionAttributeNames={"#yr": "year"},
                KeyConditionExpression=(
                    Key('year').eq(year) &
                    Key('title').between(title_bounds['first'], title_bounds['second'])))
        except ClientError as err:
            if err.response['Error']['Code'] == "ValidationException":
                logger.warning(
                    "There's a validation error. Here's the message: %s: %s",
                    err.response['Error']['Code'], err.response['Error']['Message'])
            else:
                logger.error(
                    "Couldn't query for movies. Here's why: %s: %s",
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise
        else:
            return response['Items']
    # snippet-end:[python.example_code.dynamodb.Query.ProjectionExpression]


def usage_demo(table):
    print('-'*88)
    print("Welcome to the Amazon DynamoDB updates and queries usage demo!")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    try:
        table.load()
    except ClientError as err:
        if err.response['Error']['Code'] == 'ResourceNotFoundException':
            print('\nThis demo is intended to be used with an existing table filled with'
                  '\nmovie data. To create one, run scenario_getting_started_movies.py '
                  '\nand keep the table that it creates.')
            print('-' * 88)
        raise

    wrapper = UpdateQueryWrapper(table)

    title = "The Lord of the Rings: The Fellowship of the Ring"
    increase = 3.3
    print(
        f"\nYou can use an arithmetic operation in an expression to update a numeric value."
        f"\nUpdating the rating of {title} by 3.3.")
    updated = wrapper.update_rating(title, 2001, increase)
    print(f"\nRating updated: {updated}.")
    print('-'*88)

    actor_count = 5
    print(
        f"\nYou can use a conditional update to remove actors listed for a movie,"
        f"\nbut only if there are more than {actor_count} actors listed."
        f"\nAttempting to remove an actor from {title}...")
    try:
        wrapper.remove_actors(title, 2001, actor_count)
    except ClientError as err:
        if err.response['Error']['Code'] == "ConditionalCheckFailedException":
            print(f"\nMore than {actor_count} actors must be listed. "
                  f"Let's try again with a limit of 2.")
            actor_count = 2
            updated = wrapper.remove_actors(title, 2001, actor_count)
            print(f"\nThat worked! Removed an actor from {title}. "
                  f"\nHere's how it looks now:")
            pprint(updated)
    print('-'*88)

    title = "One Direction: This Is Us"
    rating = 2
    print(
        f"\nSimilarly, you can delete a movie, but only if it meets a certain condition."
        f"\nAttempting to remove '{title}', but only if it's rated below {rating}...")
    try:
        wrapper.delete_underrated_movie(title, 2013, rating)
    except ClientError as err:
        if err.response['Error']['Code'] == "ConditionalCheckFailedException":
            print(
                f"\nIt looks like {title} is rated higher than {rating}, for some reason."
                f"\nLet's try again with a rating threshold of 5.")
            rating = 5
            wrapper.delete_underrated_movie(title, 2013, rating)
            print(f"\nThat worked. Removed {title} from the table.")
    print('-'*88)

    letters = {'first': 'P', 'second': 'V'}
    release_year = 2000
    print(
        f"\nYou can combine query conditions, such as to query for movies released in"
        f"\na certain year that start with letters in a range, and you can "
        f"\nproject the output to return only the fields that you want.")
    releases = wrapper.query_and_project_movies(release_year, letters)
    if releases:
        print(f"\nFound {len(releases)} movies released in {release_year} with titles"
              f"\nthat start between {letters['first']} and {letters['second']}. They are:")
        pprint(releases)
    else:
        print(f"I don't know about any movies released in {release_year} with titles "
              f"between {letters['first']} and {letters['second']}.")
    print('-'*88)

    print("Don't forget to delete the table when you're done.")
    print("Thanks for watching!")
    print('-'*88)


if __name__ == '__main__':
    try:
        usage_demo(boto3.resource('dynamodb').Table('doc-example-table-movies'))
    except Exception as e:
        print(f"Something went wrong with the demo! Here's what: {e}")
