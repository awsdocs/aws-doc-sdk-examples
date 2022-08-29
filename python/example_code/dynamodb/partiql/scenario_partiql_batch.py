# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon DynamoDB and PartiQL
to run batches of queries against a table that stores data about movies.

* Use batches of PartiQL statements to add, get, update, and delete data for
  individual movies.
"""

# snippet-start:[python.example_code.dynamodb.helper.PartiQLBatchWrapper.imports]
from datetime import datetime
from decimal import Decimal
import logging
from pprint import pprint

import boto3
from botocore.exceptions import ClientError

from scaffold import Scaffold

logger = logging.getLogger(__name__)
# snippet-end:[python.example_code.dynamodb.helper.PartiQLBatchWrapper.imports]


# snippet-start:[python.example_code.dynamodb.helper.PartiQLBatchWrapper.class_full]
# snippet-start:[python.example_code.dynamodb.helper.PartiQLBatchWrapper.class_decl]
class PartiQLBatchWrapper:
    """
    Encapsulates a DynamoDB resource to run PartiQL statements.
    """
    def __init__(self, dyn_resource):
        """
        :param dyn_resource: A Boto3 DynamoDB resource.
        """
        self.dyn_resource = dyn_resource
# snippet-end:[python.example_code.dynamodb.helper.PartiQLBatchWrapper.class_decl]

    # snippet-start:[python.example_code.dynamodb.BatchExecuteStatement]
    def run_partiql(self, statements, param_list):
        """
        Runs a PartiQL statement. A Boto3 resource is used even though
        `execute_statement` is called on the underlying `client` object because the
        resource transforms input and output from plain old Python objects (POPOs) to
        the DynamoDB format. If you create the client directly, you must do these
        transforms yourself.

        :param statements: The batch of PartiQL statements.
        :param param_list: The batch of PartiQL parameters that are associated with
                           each statement. This list must be in the same order as the
                           statements.
        :return: The responses returned from running the statements, if any.
        """
        try:
            output = self.dyn_resource.meta.client.batch_execute_statement(
                Statements=[{
                    'Statement': statement, 'Parameters': params
                } for statement, params in zip(statements, param_list)])
        except ClientError as err:
            if err.response['Error']['Code'] == 'ResourceNotFoundException':
                logger.error(
                    "Couldn't execute batch of PartiQL statements because the table "
                    "does not exist.")
            else:
                logger.error(
                    "Couldn't execute batch of PartiQL statements. Here's why: %s: %s",
                    err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return output
    # snippet-end:[python.example_code.dynamodb.BatchExecuteStatement]
# snippet-end:[python.example_code.dynamodb.helper.PartiQLBatchWrapper.class_full]


# snippet-start:[python.example_code.dynamodb.Scenario_PartiQLBatch]
def run_scenario(scaffold, wrapper, table_name):
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    print('-'*88)
    print("Welcome to the Amazon DynamoDB PartiQL batch statement demo.")
    print('-'*88)

    print(f"Creating table '{table_name}' for the demo...")
    scaffold.create_table(table_name)
    print('-'*88)

    movie_data = [{
        'title': f"House PartiQL",
        'year': datetime.now().year - 5,
        'info': {
            'plot': "Wacky high jinks result from querying a mysterious database.",
            'rating': Decimal('8.5')}}, {
        'title': f"House PartiQL 2",
        'year': datetime.now().year - 3,
        'info': {
            'plot': "Moderate high jinks result from querying another mysterious database.",
            'rating': Decimal('6.5')}}, {
        'title': f"House PartiQL 3",
        'year': datetime.now().year - 1,
        'info': {
            'plot': "Tepid high jinks result from querying yet another mysterious database.",
            'rating': Decimal('2.5')}}]

    print(f"Inserting a batch of movies into table '{table_name}.")
    statements = [
        f"INSERT INTO \"{table_name}\" "
        f"VALUE {{'title': ?, 'year': ?, 'info': ?}}"] * len(movie_data)
    params = [list(movie.values()) for movie in movie_data]
    wrapper.run_partiql(statements, params)
    print("Success!")
    print('-'*88)

    print(f"Getting data for a batch of movies.")
    statements = [
        f"SELECT * FROM \"{table_name}\" WHERE title=? AND year=?"] * len(movie_data)
    params = [[movie['title'], movie['year']] for movie in movie_data]
    output = wrapper.run_partiql(statements, params)
    for item in output['Responses']:
        print(f"\n{item['Item']['title']}, {item['Item']['year']}")
        pprint(item['Item'])
    print('-'*88)

    ratings = [Decimal('7.7'), Decimal('5.5'), Decimal('1.3')]
    print(f"Updating a batch of movies with new ratings.")
    statements = [
        f"UPDATE \"{table_name}\" SET info.rating=? "
        f"WHERE title=? AND year=?"] * len(movie_data)
    params = [
        [rating, movie['title'], movie['year']] for rating, movie in zip(ratings, movie_data)]
    wrapper.run_partiql(statements, params)
    print("Success!")
    print('-'*88)

    print(f"Getting projected data from the table to verify our update.")
    output = wrapper.dyn_resource.meta.client.execute_statement(
        Statement=f'SELECT title, info.rating FROM "{table_name}"')
    pprint(output['Items'])
    print('-'*88)

    print(f"Deleting a batch of movies from the table.")
    statements = [
        f"DELETE FROM \"{table_name}\" WHERE title=? AND year=?"] * len(movie_data)
    params = [[movie['title'], movie['year']] for movie in movie_data]
    wrapper.run_partiql(statements, params)
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
        movies = PartiQLBatchWrapper(dyn_res)
        run_scenario(scaffold, movies, 'doc-example-table-partiql-movies')
    except Exception as e:
        print(f"Something went wrong with the demo! Here's what: {e}")
# snippet-end:[python.example_code.dynamodb.Scenario_PartiQLBatch]
