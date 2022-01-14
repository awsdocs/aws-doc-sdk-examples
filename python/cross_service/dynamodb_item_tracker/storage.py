# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from datetime import datetime
import logging
import os
from uuid import uuid4
import boto3
from boto3.dynamodb.conditions import Attr
from botocore.exceptions import ClientError


logger = logging.getLogger(__name__)


class StorageError(Exception):
    pass


class Storage:
    def __init__(self, table, ddb_resource):
        self.table = table
        self.ddb_resource = ddb_resource

    @classmethod
    def from_env(cls):
        """
        Creates a storage object based on environment variables.
        """
        table_name = os.environ.get('TABLE_NAME', '')
        ddb_resource = boto3.resource('dynamodb')
        table = ddb_resource.Table(table_name)
        logger.info("Table %s ready.", table.name)
        return cls(table, ddb_resource)

    def get_items(self, status_filter='All'):
        try:
            if status_filter == 'All':
                work_items = self.table.scan().get('Items', [])
            else:
                work_items = self.table.scan(
                    FilterExpression=Attr('status').eq(status_filter)).get('Items', [])
        except ClientError as err:
            logger.exception(
                "Couldn't get items from table %s with status %s.", self.table.name,
                status_filter)
            raise StorageError(err)
        else:
            return work_items

    def get_item(self, item_id):
        try:
            item = self.table.get_item(Key={'item_id': item_id}).get('Item', {})
        except ClientError as err:
            logger.exception(
                "Couldn't get item %s from table %s.", item_id, self.table.name)
            raise StorageError(err)
        else:
            return item

    def add_or_update_item(self, item):
        try:
            if item['item_id'] is None:
                item['item_id'] = str(uuid4())
                item['created_date'] = str(datetime.now())
                self.table.put_item(Item=item)
            else:
                self.table.update_item(
                    Key={'item_id': item['item_id']},
                    AttributeUpdates={
                        'name': {
                            'Value': item['name'],
                            'Action': 'PUT'
                        },
                        'description': {
                            'Value': item['description'],
                            'Action': 'PUT'
                        },
                        'status': {
                            'Value': item['status'],
                            'Action': 'PUT'
                        }})
        except ClientError as err:
            logger.exception("Couldn't add or update item %s in table %s.")
            raise StorageError(err)

    def delete_item(self, item_id):
        try:
            self.table.delete_item(Key={'item_id': item_id})
        except ClientError as err:
            logger.exception("Couldn't delete item %s from table %s.")
            raise StorageError(err)
