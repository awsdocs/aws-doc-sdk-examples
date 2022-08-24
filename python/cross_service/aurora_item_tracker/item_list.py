# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to read, write, and update work items that are stored in an Amazon Aurora
database.
"""

import logging
from botocore.exceptions import ClientError
from flask_restful import Resource, reqparse
from storage import DataServiceNotReadyException

logger = logging.getLogger(__name__)


class ItemList(Resource):
    """
    Encapsulates a resource that represents a list of work items that are stored in
    an Amazon Aurora database.
    """
    def __init__(self, storage):
        """
        :param storage: An object that manages moving data in and out of the underlying
                        database.
        """
        self.storage = storage

    def get(self, item_state='active'):
        """
        Gets a list of work items.

        :param item_state: The state of items to retrieve, such as active or archived.
        :return: A list of work items and an HTTP result code.
        """
        work_items = []
        result = 200
        try:
            print(f"item_state: {item_state}")
            work_items = self.storage.get_work_items(item_state)
        except DataServiceNotReadyException:
            result = 408
        except ClientError as err:
            logger.error(
                "Couldn't get work items. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            result = 400
        return work_items, result

    def post(self):
        """
        Adds a new, active work item to the database.

        JSON request parameters:
            name: The name of the user assigned to the work item.
            guide: The name of the guide in which work is being done.
            description: A description of the work item.
            status: The current status of the work item.

        :return: The auto-generated ID of the newly added work item, and an HTTP result
                 code.
        """
        item_id = None
        result = 200
        parser = reqparse.RequestParser()
        parser.add_argument('name', location='json')
        parser.add_argument('guide', location='json')
        parser.add_argument('description', location='json')
        parser.add_argument('status', location='json')
        work_item = parser.parse_args()
        try:
            item_id = self.storage.add_work_item(work_item)
        except ClientError as err:
            logger.error(
                "Couldn't add a work item. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            result = 400
        return item_id, result

    def put(self, work_item_id):
        """
        Archives a currently active work item.

        :param work_item_id: The ID of the work item to archive.
        :return: An HTTP result code.
        """
        result = 200
        try:
            self.storage.archive_item(work_item_id)
        except ClientError as err:
            logger.error(
                "Couldn't archive work item %s. Here's why: %s: %s", work_item_id,
                err.response['Error']['Code'], err.response['Error']['Message'])
            result = 400
        return None, result
