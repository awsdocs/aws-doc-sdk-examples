# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.keyspaces.KeyspaceWrapper.class]
# snippet-start:[python.example_code.keyspaces.KeyspaceWrapper.decl]
class KeyspaceWrapper:
    """Encapsulates Amazon Keyspaces (for Apache Cassandra) keyspace and table actions."""
    def __init__(self, keyspaces_client):
        """
        :param keyspaces_client: A Boto3 Amazon Keyspaces client.
        """
        self.keyspaces_client = keyspaces_client
        self.ks_name = None
        self.ks_arn = None
        self.table_name = None

    @classmethod
    def from_client(cls):
        keyspaces_client = boto3.client('keyspaces')
        return cls(keyspaces_client)
# snippet-end:[python.example_code.keyspaces.KeyspaceWrapper.decl]

    # snippet-start:[python.example_code.keyspaces.CreateKeyspace]
    def create_keyspace(self, name):
        """
        Creates a keyspace.

        :param name: The name to give the keyspace.
        :return: The Amazon Resource Name (ARN) of the new keyspace.
        """
        try:
            response = self.keyspaces_client.create_keyspace(keyspaceName=name)
            self.ks_name = name
            self.ks_arn = response['resourceArn']
        except ClientError as err:
            logger.error(
                "Couldn't create %s. Here's why: %s: %s", name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return self.ks_arn
    # snippet-end:[python.example_code.keyspaces.CreateKeyspace]

    # snippet-start:[python.example_code.keyspaces.GetKeyspace]
    def exists_keyspace(self, name):
        """
        Checks whether a keyspace exists.

        :param name: The name of the keyspace to look up.
        :return: True when the keyspace exists. Otherwise, False.
        """
        try:
            response = self.keyspaces_client.get_keyspace(keyspaceName=name)
            self.ks_name = response['keyspaceName']
            self.ks_arn = response['resourceArn']
            exists = True
        except ClientError as err:
            if err.response['Error']['Code'] == 'ResourceNotFoundException':
                logger.info("Keyspace %s does not exist.", name)
                exists = False
            else:
                logger.error(
                    "Couldn't verify %s exists. Here's why: %s: %s", name,
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise
        return exists
    # snippet-end:[python.example_code.keyspaces.GetKeyspace]

    # snippet-start:[python.example_code.keyspaces.ListKeyspaces]
    def list_keyspaces(self, limit):
        """
        Lists the keyspaces in your account.

        :param limit: The maximum number of keyspaces to list.
        """
        try:
            ks_paginator = self.keyspaces_client.get_paginator('list_keyspaces')
            for page in ks_paginator.paginate(PaginationConfig={'MaxItems': limit}):
                for ks in page['keyspaces']:
                    print(ks['keyspaceName'])
                    print(f"\t{ks['resourceArn']}")
        except ClientError as err:
            logger.error(
                "Couldn't list keyspaces. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.keyspaces.ListKeyspaces]

    # snippet-start:[python.example_code.keyspaces.CreateTable]
    def create_table(self, table_name):
        """
        Creates a table in the  keyspace.
        The table is created with a schema for storing movie data
        and has point-in-time recovery enabled.

        :param table_name: The name to give the table.
        :return: The ARN of the new table.
        """
        try:
            response = self.keyspaces_client.create_table(
                keyspaceName=self.ks_name, tableName=table_name,
                schemaDefinition={
                    'allColumns': [
                        {'name': 'title', 'type': 'text'},
                        {'name': 'year', 'type': 'int'},
                        {'name': 'release_date', 'type': 'timestamp'},
                        {'name': 'plot', 'type': 'text'},
                    ],
                    'partitionKeys': [{'name': 'year'}, {'name': 'title'}]
                },
                pointInTimeRecovery={'status': 'ENABLED'})
        except ClientError as err:
            logger.error(
                "Couldn't create table %s. Here's why: %s: %s", table_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return response['resourceArn']
    # snippet-end:[python.example_code.keyspaces.CreateTable]

    # snippet-start:[python.example_code.keyspaces.GetTable]
    def get_table(self, table_name):
        """
        Gets data about a table in the keyspace.

        :param table_name: The name of the table to look up.
        :return: Data about the table.
        """
        try:
            response = self.keyspaces_client.get_table(
                keyspaceName=self.ks_name, tableName=table_name)
            self.table_name = table_name
        except ClientError as err:
            if err.response['Error']['Code'] == 'ResourceNotFoundException':
                logger.info("Table %s does not exist.", table_name)
                self.table_name = None
                response = None
            else:
                logger.error(
                    "Couldn't verify %s exists. Here's why: %s: %s", table_name,
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise
        return response
    # snippet-end:[python.example_code.keyspaces.GetTable]

    # snippet-start:[python.example_code.keyspaces.ListTables]
    def list_tables(self):
        """
        Lists the tables in the keyspace.
        """
        try:
            table_paginator = self.keyspaces_client.get_paginator('list_tables')
            for page in table_paginator.paginate(keyspaceName=self.ks_name):
                for table in page['tables']:
                    print(table['tableName'])
                    print(f"\t{table['resourceArn']}")
        except ClientError as err:
            logger.error(
                "Couldn't list tables in keyspace %s. Here's why: %s: %s", self.ks_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.keyspaces.ListTables]

    # snippet-start:[python.example_code.keyspaces.UpdateTable]
    def update_table(self):
        """
        Updates the schema of the table.
        
        This example updates a table of movie data by adding a new column
        that tracks whether the movie has been watched.
        """
        try:
            self.keyspaces_client.update_table(
                keyspaceName=self.ks_name, tableName=self.table_name,
                addColumns=[{'name': 'watched', 'type': 'boolean'}])
        except ClientError as err:
            logger.error(
                "Couldn't update table %s. Here's why: %s: %s", self.table_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.keyspaces.UpdateTable]

    # snippet-start:[python.example_code.keyspaces.RestoreTable]
    def restore_table(self, restore_timestamp):
        """
        Restores the table to a previous point in time. The table is restored
        to a new table in the same keyspace.

        :param restore_timestamp: The point in time to restore the table. This time
                                  must be in UTC format.
        :return: The name of the restored table.
        """
        try:
            restored_table_name = f"{self.table_name}_restored"
            self.keyspaces_client.restore_table(
                sourceKeyspaceName=self.ks_name, sourceTableName=self.table_name,
                targetKeyspaceName=self.ks_name, targetTableName=restored_table_name,
                restoreTimestamp=restore_timestamp)
        except ClientError as err:
            logger.error(
                "Couldn't restore table %s. Here's why: %s: %s", restore_timestamp,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
        else:
            return restored_table_name
    # snippet-end:[python.example_code.keyspaces.RestoreTable]

    # snippet-start:[python.example_code.keyspaces.DeleteTable]
    def delete_table(self):
        """
        Deletes the table from the keyspace.
        """
        try:
            self.keyspaces_client.delete_table(
                keyspaceName=self.ks_name, tableName=self.table_name)
            self.table_name = None
        except ClientError as err:
            logger.error(
                "Couldn't delete table %s. Here's why: %s: %s", self.table_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.keyspaces.DeleteTable]

    # snippet-start:[python.example_code.keyspaces.DeleteKeyspace]
    def delete_keyspace(self):
        """
        Deletes the keyspace.
        """
        try:
            self.keyspaces_client.delete_keyspace(keyspaceName=self.ks_name)
            self.ks_name = None
        except ClientError as err:
            logger.error(
                "Couldn't delete keyspace %s. Here's why: %s: %s", self.ks_name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            raise
    # snippet-end:[python.example_code.keyspaces.DeleteKeyspace]
# snippet-end:[python.example_code.keyspaces.KeyspaceWrapper.class]
