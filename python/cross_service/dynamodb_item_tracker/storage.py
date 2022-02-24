# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with DynamoDB to store work items
in a table.
"""

from datetime import datetime
from flask import current_app, g
import logging
from uuid import uuid4
import boto3
from boto3.dynamodb.conditions import Attr
from botocore.exceptions import ClientError


logger = logging.getLogger(__name__)


class StorageError(Exception):
    pass


class Storage:
    """
    Encapsulates data storage in a DynamoDB table.
    """
    def __init__(self, table, ddb_resource):
        """
        :param table: An existing DynamoDB table.
        :param ddb_resource: A Boto3 DynamoDB resource.
        """
        self.table = table
        self.ddb_resource = ddb_resource

    @classmethod
    def from_context(cls):
        """
        Creates a storage object based on context. The object is stored in Flask
        session globals and reused if it exists.
        """
        # pylint: disable=assigning-non-slot
        storage = getattr(g, 'storage', None)
        if storage is None:
            table_name = current_app.config['TABLE_NAME']
            ddb_resource = boto3.resource('dynamodb')
            table = ddb_resource.Table(table_name)
            logger.info("Table %s ready.", table.name)
            storage = cls(table, ddb_resource)
            g.storage = storage
        return storage

    def get_items(self, status_filter='All'):
        """
        Gets work items currently stored in the table.

        :param status_filter: When specified, only work items with this status are
                              returned.
        :return: A list of work items currently stored in the table.
        """
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
        """
        Gets an item from the table.

        :param item_id: The ID of the item to retrieve.
        :return: The requested item.
        """
        try:
            item = self.table.get_item(Key={'item_id': item_id}).get('Item', {})
        except ClientError as err:
            logger.exception(
                "Couldn't get item %s from table %s.", item_id, self.table.name)
            raise StorageError(err)
        else:
            return item

    def add_or_update_item(self, item):
        """
        Adds or updates an item in the table. When the item contains an item_id, it
        is updated. Otherwise, it is added.

        :param item: The item to add or update.
        """
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
        """
        Deletes an item from the table.

        :param item_id: The ID of the item to delete.
        """
        try:
            self.table.delete_item(Key={'item_id': item_id})
        except ClientError as err:
            logger.exception("Couldn't delete item %s from table %s.")
            raise StorageError(err)
