# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the Amazon Relational Database Service (Amazon RDS) Data Service to
interact with an Amazon Aurora database.
"""

import logging
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class DataServiceNotReadyException(Exception):
    pass


class Storage:
    """
    Wraps calls to the Amazon RDS Data Service.
    """
    def __init__(self, cluster, secret, db_name, table_name, rdsdata_client):
        """
        :param cluster: The Amazon Resource Name (ARN) of an Amazon Aurora cluster that
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
        Runs a SQL statement and associated parameters using Amazon RDS Data Service.

        :param sql: The SQL statement to run.
        :param sql_params: The parameters associated with the SQL statement.
        :transaction_id: The ID of a previously created transaction.
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
                    'pause mode after five minutes of inactivity. Wait a minute for '
                    'your cluster to resume and try your request again.') from error
            logger.exception("Run statement on %s failed.", self._db_name)
            raise
        else:
            return result

    def get_work_items(self, item_state):
        """
        Gets work items from the database.

        :param item_state: Specifies whether to retrieve active, archived, or all items.
        :return: The list of retrieved work items.
        """
        sql_select = "SELECT work_item_id, created_date, username, description, guide, status, archive"
        sql_where = ''
        sql_params = None
        if item_state is not None and item_state != 'all':
            sql_where = "WHERE archive=:archive"
            sql_params = [{'name': 'archive', 'value': {'booleanValue': item_state == 'archive'}}]
        sql = f"{sql_select} FROM {self._table_name} {sql_where}"
        print(sql)
        results = self._run_statement(sql, sql_params=sql_params)
        output = [{
            'id': record[0]['longValue'],
            'created_date': record[1]['stringValue'],
            'name': record[2]['stringValue'],
            'description': record[3]['stringValue'],
            'guide': record[4]['stringValue'],
            'status': record[5]['stringValue'],
            'state': 'archive' if record[6]['booleanValue'] else 'active'
        } for record in results['records']]
        return output

    def add_work_item(self, work_item):
        """
        Adds a work item to the database.

        :param work_item: The work item to add to the database. Because the ID,
                          created date, and archive fields are auto-generated,
                          you don't need to specify them when creating a new item.
        :return: The generated ID of the new work item.
        """
        sql = (f"INSERT INTO {self._table_name} (username, description, guide, status) " 
               f" VALUES (:username, :description, :guide, :status)")
        sql_params = [
            {'name': 'username', 'value': {'stringValue': work_item['name']}},
            {'name': 'description', 'value': {'stringValue': work_item['description']}},
            {'name': 'guide', 'value': {'stringValue': work_item['guide']}},
            {'name': 'status', 'value': {'stringValue': work_item['status']}}
        ]
        results = self._run_statement(sql, sql_params=sql_params)
        work_item_id = results['generatedFields'][0]['longValue']
        return work_item_id

    def archive_item(self, work_item_id):
        """
        Archives a work item.

        :param work_item_id: The ID of the work item to archive.
        """
        sql = f"UPDATE {self._table_name} SET archive=:archive WHERE work_item_id=:work_item_id"
        sql_params = [
            {'name': 'archive', 'value': {'booleanValue': True}},
            {'name': 'work_item_id', 'value': {'longValue': work_item_id}}
        ]
        self._run_statement(sql, sql_params=sql_params)
