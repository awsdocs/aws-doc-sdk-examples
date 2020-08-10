# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to write and retrieve Amazon DynamoDB
data as part of a REST API. This file is uploaded to AWS Lambda as part of the
serverless deployment package created by AWS Chalice.
"""

import datetime
import os
import random
import boto3
from boto3.dynamodb.conditions import Key


class Storage:
    """
    Handles basic storage functions, backed by an Amazon DynamoDB table.
    """
    STATES = {
        'Alabama', 'Alaska', 'Arizona', 'Arkansas', 'California', 'Colorado',
        'Connecticut', 'Delaware', 'Florida', 'Georgia', 'Hawaii', 'Idaho',
        'Illinois', 'Indiana', 'Iowa', 'Kansas', 'Kentucky', 'Louisiana', 'Maine',
        'Maryland', 'Massachusetts', 'Michigan', 'Minnesota', 'Mississippi',
        'Missouri', 'Montana', 'Nebraska', 'Nevada', 'New Hampshire', 'New Jersey',
        'New Mexico', 'New York', 'North Carolina', 'North Dakota', 'Ohio',
        'Oklahoma', 'Oregon', 'Pennsylvania', 'Rhode Island', 'South Carolina',
        'South Dakota', 'Tennessee', 'Texas', 'Utah', 'Vermont', 'Virginia',
        'Washington', 'West Virginia', 'Wisconsin', 'Wyoming'
    }

    def __init__(self, table):
        self._table = table

    @classmethod
    def from_env(cls):
        """
        Creates a Storage object that contains a table identified by the TABLE_NAME
        environment variable.

        :return: The newly created Storage object.
        """
        table_name = os.environ.get('TABLE_NAME', '')
        table = boto3.resource('dynamodb').Table(table_name)
        return cls(table)

    @staticmethod
    def _generate_random_data(state):
        """
        Generates some random data for the demo.

        :param state: The state for which to create the data.
        :return: The newly created data.
        """
        return {
            'state': state,
            'date': datetime.date.today().isoformat(),
            'cases': random.randint(1, 1000),
            'deaths': random.randint(1, 100)
        }

    def get_state_data(self, state):
        """
        Gets the data records for the specified state. If there are no records,
        a new one is generated with random values for today's date and stored in
        the table before it is returned.

        :param state: The state to retrieve.
        :return: The retrieved data.
        """
        response = self._table.query(
            KeyConditionExpression=Key('state').eq(state)
        )
        items = response.get('Items', [])
        if len(items) == 0:
            items.append(self._generate_random_data(state))
            self._table.put_item(Item=items[0])
        return items

    def put_state_data(self, state, state_data):
        """
        Puts data for a state into the table.

        :param state: The state for which to store the data.
        :param state_data: The data record to store.
        """
        self._table.put_item(Item=state_data)

    def delete_state_data(self, state):
        """
        Deletes all records for a state from the table.

        :param state: The state to delete.
        """
        response = self._table.query(
            KeyConditionExpression=Key('state').eq(state)
        )
        items = response.get('Items', [])
        with self._table.batch_writer() as batch:
            for item in items:
                batch.delete_item(Key={'state': item['state'], 'date': item['date']})

    def post_state_data(self, state, state_data):
        """
        Puts data for a state into the table.

        :param state: The state for which to store the data.
        :param state_data: The data record to store.
        """
        self._table.put_item(Item=state_data)

    def get_state_date_data(self, state, date):
        """
        Gets a single record for the specified state and date.

        :param state: The state of the record to retrieve.
        :param date: The date of the record to retrieve.
        :return: The retrieved record, or None if no record exists.
        """
        response = self._table.get_item(Key={'state': state, 'date': date})
        item = response.get('Item', None)
        return item

    def delete_state_date_data(self, state, date):
        """
        Deletes the record for the specified state and date.

        :param state: The state of the record to remove.
        :param date: The date of the record to remove.
        """
        self._table.delete_item(Key={'state': state, 'date': date})
