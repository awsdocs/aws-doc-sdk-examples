# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the Amazon Relational Database Service (Amazon RDS) Data Service to
interact with an Amazon Aurora Serverless database.
"""

import logging
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class DataServiceNotReadyException(Exception):
    pass


class StorageError(Exception):
    pass


class Storage:
    """
    Wraps calls to the Amazon RDS Data Service.
    """
    def __init__(self, cluster, secret, db_name, table_name, rdsdata_client):
        """
        :param cluster: The Amazon Resource Name (ARN) of an Aurora DB cluster that
                        contains the work item database.
        :param secret: The ARN of an AWS Secrets Manager secret that contains
                       credentials used to connect to the database.
        :param db_name: The name of the work item database.
        :param table_name: The name of the work item table in the database.
        :param rdsdata_client: A Boto3 Amazon RDS Data Service client.
        """
        self._cluster = cluster
        self._secret = secret
        self._db_name = db_name
        self._table_name = table_name
        self._rdsdata_client = rdsdata_client

    def _run_statement(self, sql, sql_params=None):
        """
        Runs a SQL statement and associated parameters using the Amazon RDS Data Service.

        :param sql: The SQL statement to run.
        :param sql_params: The parameters associated with the SQL statement.
        :return: The result of running the SQL statement.
        """
        try:
            run_args = {
                'database': self._db_name,
                'resourceArn': self._cluster,
                'secretArn': self._secret,
                'sql': sql
            }
            if sql_params is not None:
                run_args['parameters'] = sql_params
            result = self._rdsdata_client.execute_statement(**run_args)
            logger.info("Ran statement on %s.", self._db_name)
        except ClientError as error:
            if (error.response['Error']['Code'] == 'BadRequestException'
                    and 'Communications link failure'
                    in error.response['Error']['Message']):
                raise DataServiceNotReadyException(
                    'The Aurora Data Service is not ready, probably because it entered '
                    'pause mode after a period of inactivity. Wait a minute for '
                    'your cluster to resume and try your request again.') from error
            else:
                logger.exception("Run statement on %s failed.", self._db_name)
                raise StorageError(error)
        else:
            return result

    def get_work_items(self, archived=None):
        """
        Gets work items from the database.

        :param archived: When specified, only archived or non-archived work items are
                         returned. Otherwise, all work items are returned.
        :return: The list of retrieved work items.
        """
        sql_select = "SELECT iditem, description, guide, status, username, archived"
        sql_where = ''
        sql_params = None
        if archived is not None:
            sql_where = "WHERE archived=:archived"
            sql_params = [{'name': 'archived', 'value': {'booleanValue': archived}}]
        sql = f"{sql_select} FROM {self._table_name} {sql_where}"
        print(sql)
        results = self._run_statement(sql, sql_params=sql_params)
        output = [{
            'iditem': record[0]['longValue'],
            'description': record[1]['stringValue'],
            'guide': record[2]['stringValue'],
            'status': record[3]['stringValue'],
            'username': record[4]['stringValue'],
            'archived': record[5]['booleanValue']
        } for record in results['records']]
        return output

    def add_work_item(self, work_item):
        """
        Adds a work item to the database.

        :param work_item: The work item to add to the database. Because the ID
                          and archive fields are auto-generated,
                          you don't need to specify them when creating a new item.
        :return: The generated ID of the new work item.
        """
        sql = (f"INSERT INTO {self._table_name} (description, guide, status, username) " 
               f" VALUES (:description, :guide, :status, :username)")
        sql_params = [
            {'name': 'description', 'value': {'stringValue': work_item['description']}},
            {'name': 'guide', 'value': {'stringValue': work_item['guide']}},
            {'name': 'status', 'value': {'stringValue': work_item['status']}},
            {'name': 'username', 'value': {'stringValue': work_item['username']}},
        ]
        results = self._run_statement(sql, sql_params=sql_params)
        work_item_id = results['generatedFields'][0]['longValue']
        return work_item_id

    def archive_work_item(self, iditem):
        """
        Archives a work item.

        :param iditem: The ID of the work item to archive.
        """
        sql = f"UPDATE {self._table_name} SET archived=:archived WHERE iditem=:iditem"
        sql_params = [
            {'name': 'archived', 'value': {'booleanValue': True}},
            {'name': 'iditem', 'value': {'longValue': int(iditem)}}
        ]
        self._run_statement(sql, sql_params=sql_params)
