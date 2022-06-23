# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to write and retrieve Amazon DynamoDB
data using batch functions.

Boto3 features a `batch_writer` function that handles all of the necessary intricacies
of the Amazon DynamoDB batch writing API on your behalf. This includes buffering,
removing duplicates, and retrying unprocessed items.
"""

# snippet-start:[python.example_code.dynamodb.Batching_imports]
import decimal
import json
import logging
import os
import pprint
import time
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)
dynamodb = boto3.resource('dynamodb')

MAX_GET_SIZE = 100  # Amazon DynamoDB rejects a get batch larger than 100 items.

# snippet-end:[python.example_code.dynamodb.Batching_imports]


def create_table(table_name, schema):
    """
    Creates an Amazon DynamoDB table with the specified schema.

    :param table_name: The name of the table.
    :param schema: The schema of the table. The schema defines the format
                   of the keys that identify items in the table.
    :return: The newly created table.
    """
    try:
        table = dynamodb.create_table(
            TableName=table_name,
            KeySchema=[{
                'AttributeName': item['name'], 'KeyType': item['key_type']
            } for item in schema],
            AttributeDefinitions=[{
                'AttributeName': item['name'], 'AttributeType': item['type']
            } for item in schema],
            ProvisionedThroughput={'ReadCapacityUnits': 10, 'WriteCapacityUnits': 10}
        )
        table.wait_until_exists()
        logger.info("Created table %s.", table.name)
    except ClientError:
        logger.exception("Couldn't create movie table.")
        raise
    else:
        return table


# snippet-start:[python.example_code.dynamodb.BatchGetItem]
def do_batch_get(batch_keys):
    """
    Gets a batch of items from Amazon DynamoDB. Batches can contain keys from
    more than one table.

    When Amazon DynamoDB cannot process all items in a batch, a set of unprocessed
    keys is returned. This function uses an exponential backoff algorithm to retry
    getting the unprocessed keys until all are retrieved or the specified
    number of tries is reached.

    :param batch_keys: The set of keys to retrieve. A batch can contain at most 100
                       keys. Otherwise, Amazon DynamoDB returns an error.
    :return: The dictionary of retrieved items grouped under their respective
             table names.
    """
    tries = 0
    max_tries = 5
    sleepy_time = 1  # Start with 1 second of sleep, then exponentially increase.
    retrieved = {key: [] for key in batch_keys}
    while tries < max_tries:
        response = dynamodb.batch_get_item(RequestItems=batch_keys)
        # Collect any retrieved items and retry unprocessed keys.
        for key in response.get('Responses', []):
            retrieved[key] += response['Responses'][key]
        unprocessed = response['UnprocessedKeys']
        if len(unprocessed) > 0:
            batch_keys = unprocessed
            unprocessed_count = sum(
                [len(batch_key['Keys']) for batch_key in batch_keys.values()])
            logger.info(
                "%s unprocessed keys returned. Sleep, then retry.",
                unprocessed_count)
            tries += 1
            if tries < max_tries:
                logger.info("Sleeping for %s seconds.", sleepy_time)
                time.sleep(sleepy_time)
                sleepy_time = min(sleepy_time * 2, 32)
        else:
            break

    return retrieved
# snippet-end:[python.example_code.dynamodb.BatchGetItem]


# snippet-start:[python.example_code.dynamodb.PutItem_BatchWriter]
def fill_table(table, table_data):
    """
    Fills an Amazon DynamoDB table with the specified data, using the Boto3
    Table.batch_writer() function to put the items in the table.
    Inside the context manager, Table.batch_writer builds a list of
    requests. On exiting the context manager, Table.batch_writer starts sending
    batches of write requests to Amazon DynamoDB and automatically
    handles chunking, buffering, and retrying.

    :param table: The table to fill.
    :param table_data: The data to put in the table. Each item must contain at least
                       the keys required by the schema that was specified when the
                       table was created.
    """
    try:
        with table.batch_writer() as writer:
            for item in table_data:
                writer.put_item(Item=item)
        logger.info("Loaded data into table %s.", table.name)
    except ClientError:
        logger.exception("Couldn't load data into table %s.", table.name)
        raise
# snippet-end:[python.example_code.dynamodb.PutItem_BatchWriter]


# snippet-start:[python.example_code.dynamodb.BatchGetItem_CallBatchGet]
def get_batch_data(movie_table, movie_list, actor_table, actor_list):
    """
    Gets data from the specified movie and actor tables. Data is retrieved in batches.

    :param movie_table: The table from which to retrieve movie data.
    :param movie_list: A list of keys that identify movies to retrieve.
    :param actor_table: The table from which to retrieve actor data.
    :param actor_list: A list of keys that identify actors to retrieve.
    :return: The dictionary of retrieved items grouped under the respective
             movie and actor table names.
    """
    batch_keys = {
        movie_table.name: {
            'Keys': [{'year': movie[0], 'title': movie[1]} for movie in movie_list]
        },
        actor_table.name: {
            'Keys': [{'name': actor} for actor in actor_list]
        }
    }
    try:
        retrieved = do_batch_get(batch_keys)
        for response_table, response_items in retrieved.items():
            logger.info("Got %s items from %s.", len(response_items), response_table)
    except ClientError:
        logger.exception(
            "Couldn't get items from %s and %s.", movie_table.name, actor_table.name)
        raise
    else:
        return retrieved
# snippet-end:[python.example_code.dynamodb.BatchGetItem_CallBatchGet]


# snippet-start:[python.example_code.dynamodb.Usage_ArchiveMovies]
def archive_movies(movie_table, movie_data):
    """
    Archives a list of movies to a newly created archive table and then deletes the
    movies from the original table.

    Uses the Boto3 Table.batch_writer() function to handle putting items into the
    archive table and deleting them from the original table. Shows how to configure
    the batch_writer to ensure there are no duplicates in the batch. If a batch
    contains duplicates, Amazon DynamoDB rejects the request and returns a
    ValidationException.

    :param movie_table: The table that contains movie data.
    :param movie_data: The list of keys that identify the movies to archive.
    :return: The newly created archive table.
    """
    try:
        # Copy the schema and attribute definition from the original movie table to
        # create the archive table.
        archive_table = dynamodb.create_table(
            TableName=f'{movie_table.name}-archive',
            KeySchema=movie_table.key_schema,
            AttributeDefinitions=movie_table.attribute_definitions,
            ProvisionedThroughput={
                'ReadCapacityUnits':
                    movie_table.provisioned_throughput['ReadCapacityUnits'],
                'WriteCapacityUnits':
                    movie_table.provisioned_throughput['WriteCapacityUnits']
            })
        logger.info("Table %s created, wait until exists.", archive_table.name)
        archive_table.wait_until_exists()
    except ClientError:
        logger.exception("Couldn't create archive table for %s.", movie_table.name)
        raise

    try:
        # When the list of items in the batch contains duplicates, Amazon DynamoDB
        # rejects the request. By default, the batch_writer keeps duplicates.
        with archive_table.batch_writer() as archive_writer:
            for item in movie_data:
                archive_writer.put_item(Item=item)
        logger.info("Put movies into %s.", archive_table.name)
    except ClientError as error:
        if error.response['Error']['Code'] == 'ValidationException':
            logger.info(
                "Got expected exception when trying to put duplicate records into the "
                "archive table.")
        else:
            logger.exception(
                "Got unexpected exception when trying to put duplicate records into "
                "the archive table.")
            raise

    try:
        # When `overwrite_by_pkeys` is specified, the batch_writer overwrites any
        # duplicate in the batch with the new item.
        with archive_table.batch_writer(
                overwrite_by_pkeys=['year', 'title']) as archive_writer:
            for item in movie_data:
                archive_writer.put_item(Item=item)
        logger.info("Put movies into %s.", archive_table.name)
    except ClientError:
        logger.exception(
            "Couldn't put movies into %s.", archive_table.name)
        raise

    try:
        with movie_table.batch_writer(
                overwrite_by_pkeys=['year', 'title']) as movie_writer:
            for item in movie_data:
                movie_writer.delete_item(
                    Key={'year': item['year'], 'title': item['title']})
        logger.info("Deleted movies from %s.", movie_table.name)
    except ClientError:
        logger.exception(
            "Couldn't delete movies from %s.", movie_table.name)
        raise

    return archive_table
# snippet-end:[python.example_code.dynamodb.Usage_ArchiveMovies]


# snippet-start:[python.example_code.dynamodb.Usage_BatchFunctions]
def usage_demo():
    """
    Shows how to use the Amazon DynamoDB batch functions.
    """
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    print('-'*88)
    print("Welcome to the Amazon DynamoDB batch usage demo.")
    print('-'*88)

    movies_file_name = 'moviedata.json'
    print(f"Getting movie data from {movies_file_name}.")
    try:
        with open(movies_file_name) as json_file:
            movie_data = json.load(json_file, parse_float=decimal.Decimal)
            movie_data = movie_data[:500]  # Only use the first 500 movies for the demo.
    except FileNotFoundError:
        print(f"The file moviedata.json was not found in the current working directory "
              f"{os.getcwd()}.\n"
              f"1. Download the zip file from https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip.\n"
              f"2. Extract '{movies_file_name}' to {os.getcwd()}.\n"
              f"3. Run the usage demo again.")
        return

    # Build a second table centered around actors.
    actor_set = {}
    for movie in movie_data:
        try:
            actors = movie['info']['actors']
            for actor in actors:
                if actor not in actor_set:
                    actor_set[actor] = {'directors': set(), 'costars': set()}
                actor_set[actor]['directors'].update(movie['info']['directors'])
                actor_set[actor]['costars'].update([a for a in actors if a != actor])
        except KeyError:
            logger.warning("%s doesn't have any actors.", movie['title'])
    actor_data = []
    for key, value in actor_set.items():
        actor_item = {'name': key}
        if len(value['directors']) > 0:
            actor_item['directors'] = value['directors']
        if len(value['costars']) > 0:
            actor_item['costars'] = value['costars']
        actor_data.append(actor_item)

    movie_schema = [
        {'name': 'year', 'key_type': 'HASH', 'type': 'N'},
        {'name': 'title', 'key_type': 'RANGE', 'type': 'S'}
    ]
    actor_schema = [
        {'name': 'name', 'key_type': 'HASH', 'type': 'S'},
    ]

    print(f"Creating movie and actor tables and waiting until they exist...")
    movie_table = create_table(f'demo-batch-movies-{time.time_ns()}', movie_schema)
    actor_table = create_table(f'demo-batch-actors-{time.time_ns()}', actor_schema)
    print(f"Created {movie_table.name} and {actor_table.name}.")

    print(f"Putting {len(movie_data)} movies into {movie_table.name}.")
    fill_table(movie_table, movie_data)

    print(f"Putting {len(actor_data)} actors into {actor_table.name}.")
    fill_table(actor_table, actor_data)

    movie_list = [(movie['year'], movie['title'])
                  for movie in movie_data[0:int(MAX_GET_SIZE/2)]]
    actor_list = [actor['name']
                  for actor in actor_data[0:int(MAX_GET_SIZE/2)]]
    items = get_batch_data(movie_table, movie_list, actor_table, actor_list)
    print(f"Got {len(items[movie_table.name])} movies from {movie_table.name}\n"
          f"and {len(items[actor_table.name])} actors from {actor_table.name}.")
    print("The first 2 movies returned are: ")
    pprint.pprint(items[movie_table.name][:2])
    print(f"The first 2 actors returned are: ")
    pprint.pprint(items[actor_table.name][:2])

    print(
        "Archiving the first 10 movies by creating a table to store archived "
        "movies and deleting them from the main movie table.")
    # Duplicate the movies in the list to demonstrate how the batch writer can be
    # configured to remove duplicate requests from the batch.
    movie_list = movie_data[0:10] + movie_data[0:10]
    archive_table = archive_movies(movie_table, movie_list)
    print(f"Movies successfully archived to {archive_table.name}.")

    archive_table.delete()
    movie_table.delete()
    actor_table.delete()
    print(f"Deleted {movie_table.name}, {archive_table.name}, and {actor_table.name}.")
    print("Thanks for watching!")
# snippet-end:[python.example_code.dynamodb.Usage_BatchFunctions]


if __name__ == '__main__':
    usage_demo()
