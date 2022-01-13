# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from datetime import datetime
import logging
import os
from uuid import uuid4
import boto3
from botocore.exceptions import ClientError


logger = logging.getLogger(__name__)


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

    def get_items(self):
        return self.table.scan().get('Items', [])

    def get_item(self, item_id):
        return self.table.get_item(Key={'item_id': item_id}).get('Item', {})

    def add_or_update_item(self, item):
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

    def delete_item(self, item_id):
        self.table.delete_item(Key={'item_id': item_id})
