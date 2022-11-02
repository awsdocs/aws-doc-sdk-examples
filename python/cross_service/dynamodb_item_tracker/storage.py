# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon DynamoDB to store work items
in a table.
"""

import logging
from uuid import uuid4
from boto3.dynamodb.conditions import Attr
from botocore.exceptions import ClientError


logger = logging.getLogger(__name__)


class StorageError(Exception):
    pass


class Storage:
    """
    Encapsulates work item data in a DynamoDB table.
    """
    def __init__(self, table):
        """
        :param table: A Boto3 DynamoDB Table object that represents an existing DynamoDB
                      table. This object is a high-level object that wraps low-level
                      DynamoDB service actions.
        """
        self.table = table

    def get_work_items(self, archived=None):
        """
        Gets work items currently stored in the table.

        :param archived: When specified, only archived or non-archived work items are
                         returned. Otherwise, all work items are returned.
        :return: A list of work items currently stored in the table.
        """
        try:
            if archived is None:
                work_items = self.table.scan().get('Items', [])
            else:
                work_items = self.table.scan(
                    FilterExpression=Attr('archived').eq(archived)).get('Items', [])
        except ClientError as err:
            logger.exception(
                "Couldn't get items from table %s with archived %s.", self.table.name,
                archived)
            raise StorageError(err)
        else:
            return work_items

    def get_work_item(self, iditem):
        """
        Gets a single work item from the table.

        :param iditem: The ID of the item to retrieve.
        :return: The requested item.
        """
        try:
            item = self.table.get_item(Key={'iditem': iditem}).get('Item', {})
        except ClientError as err:
            logger.exception(
                "Couldn't get item %s from table %s.", iditem, self.table.name)
            raise StorageError(err)
        else:
            return item

    def add_or_update_work_item(self, item):
        """
        Adds or updates an item in the table. When the item contains an iditem field, it
        is updated. Otherwise, it is added. When an item is added, a UUID is generated
        as its ID.

        :param item: The item to add or update.
        :return: The ID of the item.
        """
        try:
            if item.get('iditem') is None:
                item['iditem'] = str(uuid4())
                self.table.put_item(Item=item)
            else:
                attrs = {
                    key: {'Value': val, 'Action': 'PUT'}
                    for key, val in item.items() if key != 'iditem'
                }
                self.table.update_item(
                    Key={'iditem': item['iditem']},
                    AttributeUpdates=attrs)
        except ClientError as err:
            logger.exception("Couldn't add or update item %s in table %s.")
            raise StorageError(err)
        return item['iditem']
