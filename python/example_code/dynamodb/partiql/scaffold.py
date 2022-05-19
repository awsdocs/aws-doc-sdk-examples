# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Scaffolding for the Amazon DynamoDB PartiQL scenarios.

* Creates and deletes a DynamoDB movie table.
"""

import logging

from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class Scaffold:
    """
    Encapsulates scaffolding functions to deploy and destroy resources used by the demo.
    """
    def __init__(self, dyn_resource):
        """
        :param dyn_resource: A Boto3 DynamoDB resource.
        """
        self.dyn_resource = dyn_resource
        self.table = None

    def create_table(self, table_name):
        """
        Creates a DynamoDB table that can be used to store movie data.
        The table uses the release year of the movie as the partition key and the
        title as the sort key.

        :param table_name: The name of the table to create.
        :return: The newly created table.
        """
        try:
            self.table = self.dyn_resource.create_table(
                TableName=table_name,
                KeySchema=[
                    {'AttributeName': 'year', 'KeyType': 'HASH'},  # Partition key
                    {'AttributeName': 'title', 'KeyType': 'RANGE'}  # Sort key
                ],
                AttributeDefinitions=[
                    {'AttributeName': 'year', 'AttributeType': 'N'},
                    {'AttributeName': 'title', 'AttributeType': 'S'}
                ],
                ProvisionedThroughput={'ReadCapacityUnits': 10, 'WriteCapacityUnits': 10})
            self.table.wait_until_exists()
        except ClientError as err:
            if err.response['Error']['Code'] == 'ResourceInUseException':
                logger.info("Table %s already exists.", table_name)
            else:
                logger.error(
                    "Couldn't create table %s. Here's why: %s: %s", table_name,
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise

    def delete_table(self):
        """
        Deletes a table, if one was created for the demo.
        """
        try:
            if self.table is not None:
                self.table.delete()
                self.table = None
            else:
                logger.warning("Not deleting table because it wasn't created by this demo.")
        except ClientError as err:
            logger.error(
                "Couldn't delete table. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
