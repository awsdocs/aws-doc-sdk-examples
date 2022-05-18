# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon DynamoDB to
use PartiQL to query a table that stores data about movies.

* Use PartiQL statements to add, get, update, and delete data for individual movies.
"""

# snippet-start:[python.example_code.dynamodb.helper.PartiQLWrapper.imports]
from datetime import datetime
from decimal import Decimal
import logging
from pprint import pprint

import boto3
from botocore.exceptions import ClientError

from scaffold import Scaffold

logger = logging.getLogger(__name__)
# snippet-end:[python.example_code.dynamodb.helper.PartiQLWrapper.imports]


# snippet-start:[python.example_code.dynamodb.helper.PartiQLWrapper.class_full]
# snippet-start:[python.example_code.dynamodb.helper.PartiQLWrapper.class_decl]
class PartiQLWrapper:
    """
    Encapsulates a DynamoDB resource to run PartiQL statements.
    """
    def __init__(self, dyn_resource):
        """
        :param dyn_resource: A Boto3 DynamoDB resource.
        """
        self.dyn_resource = dyn_resource
# snippet-end:[python.example_code.dynamodb.helper.PartiQLWrapper.class_decl]

# snippet-start:[python.example_code.dynamodb.ExecuteStatement]
    def run_partiql(self, statement, params):
        """
        Runs a PartiQL statement. A Boto3 resource is used even though
        `execute_statement` is called on the underlying `client` object because the
        resource transforms input and output from plain old Python objects (POPOs) to
        the DynamoDB format. If you create the client directly, you must do these
        transforms yourself.

        :param statement: The PartiQL statement.
        :param params: The list of PartiQL parameters. These are applied to the
                       statement in the order they are listed.
        :return: The items returned from the statement, if any.
        """
        try:
            output = self.dyn_resource.meta.client.execute_statement(
                Statement=statement, Parameters=params)
        except ClientError as err:
            if err.response['Error']['Code'] == 'ResourceNotFoundException':
                logger.error(
                    "Couldn't execute PartiQL '%s' because the table does not exist.",
                    statement)
            else:
                logger.error(
                    "Couldn't execute PartiQL '%s'. Here's why: %s: %s", statement,
                    err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return output
# snippet-end:[python.example_code.dynamodb.ExecuteStatement]
# snippet-end:[python.example_code.dynamodb.helper.PartiQLWrapper.class_full]


# snippet-start:[python.example_code.dynamodb.Scenario_PartiQLSingle]
def run_scenario(scaffold, wrapper, table_name):
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    print('-'*88)
    print("Welcome to the Amazon DynamoDB PartiQL single statement demo.")
    print('-'*88)

    print(f"Creating table '{table_name}' for the demo...")
    scaffold.create_table(table_name)
    print('-'*88)

    title = "24 Hour PartiQL People"
    year = datetime.now().year
    plot = "A group of data developers discover a new query language they can't stop using."
    rating = Decimal('9.9')

    print(f"Inserting movie '{title}' released in {year}.")
    wrapper.run_partiql(
        f"INSERT INTO \"{table_name}\" VALUE {{'title': ?, 'year': ?, 'info': ?}}",
        [title, year, {'plot': plot, 'rating': rating}])
    print("Success!")
    print('-'*88)

    print(f"Getting data for movie '{title}' released in {year}.")
    output = wrapper.run_partiql(
        f"SELECT * FROM \"{table_name}\" WHERE title=? AND year=?", [title, year])
    for item in output['Items']:
        print(f"\n{item['title']}, {item['year']}")
        pprint(output['Items'])
    print('-'*88)

    rating = Decimal('2.4')
    print(f"Updating movie '{title}' with a rating of {float(rating)}.")
    wrapper.run_partiql(
        f"UPDATE \"{table_name}\" SET info.rating=? WHERE title=? AND year=?",
        [rating, title, year])
    print("Success!")
    print('-'*88)

    print(f"Getting data again to verify our update.")
    output = wrapper.run_partiql(
        f"SELECT * FROM \"{table_name}\" WHERE title=? AND year=?", [title, year])
    for item in output['Items']:
        print(f"\n{item['title']}, {item['year']}")
        pprint(output['Items'])
    print('-'*88)

    print(f"Deleting movie '{title}' released in {year}.")
    wrapper.run_partiql(
        f"DELETE FROM \"{table_name}\" WHERE title=? AND year=?", [title, year])
    print("Success!")
    print('-'*88)

    print(f"Deleting table '{table_name}'...")
    scaffold.delete_table()
    print('-'*88)

    print("\nThanks for watching!")
    print('-'*88)


if __name__ == '__main__':
    try:
        dyn_res = boto3.resource('dynamodb')
        scaffold = Scaffold(dyn_res)
        movies = PartiQLWrapper(dyn_res)
        run_scenario(scaffold, movies, 'doc-example-table-partiql-movies')
    except Exception as e:
        print(f"Something went wrong with the demo! Here's what: {e}")
# snippet-end:[python.example_code.dynamodb.Scenario_PartiQLSingle]
