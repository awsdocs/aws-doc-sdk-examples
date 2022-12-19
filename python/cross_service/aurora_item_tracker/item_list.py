# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to read, write, and update work items that are stored in an Amazon Aurora
Serverless database.
"""

import logging
from flask import jsonify
from flask.views import MethodView
from marshmallow import Schema
from webargs import fields
from webargs.flaskparser import use_args, use_kwargs
from storage import DataServiceNotReadyException, StorageError

logger = logging.getLogger(__name__)


class WorkItemSchema(Schema):
    """
    A schema for validating work item data and transforming field names between
    external and internal names.
    """
    iditem = fields.Str(data_key='id')      # The ID of the item.
    description = fields.Str()              # The item's description.
    guide = fields.Str()                    # The SDK guide the item is associated with.
    status = fields.Str()                   # The current status of the item.
    username = fields.Str(data_key='name')  # The user assigned to the item.
    archived = fields.Bool()                # Whether the item is active or archived.


class ItemList(MethodView):
    """
    Encapsulates a REST resource that represents a list of work items.

    This class uses the webargs package together with a marshmallow schema to manage
    incoming data validation and field transformation.
    """
    def __init__(self, storage):
        """
        :param storage: An object that manages moving data in and out of the underlying
                        database.
        """
        self.storage = storage

    @use_kwargs(WorkItemSchema, location='query')
    def get(self, iditem, archived=None):
        """
        Gets a list of work items or a single work item.

        :param iditem: When specified, the ID of a single item to retrieve.
        :param archived: When specified, either archived or non-archived items are
                         returned. Otherwise, all items are returned.
        :return: A list of work items and an HTTP result code.
        """
        result = 200
        try:
            if iditem is None:
                print(f"archived: {archived}")
                work_items = self.storage.get_work_items(archived)
            else:
                work_items = [self.storage.get_work_item(iditem)]
            schema = WorkItemSchema(many=True)
            response = schema.dump(work_items)
        except DataServiceNotReadyException as err:
            logger.error(err)
            response = jsonify("Data service not ready. Wait a minute and try again.")
            result = 503
        except StorageError as err:
            logger.error("Storage error when trying to get work items: %s", err)
            response = jsonify("A storage error occurred.")
            result = 500
        return response, result

    @use_args(WorkItemSchema)
    def post(self, args):
        """
        Adds a work item to the database.

        :param args: The request body data, validated and transformed by the work item schema.
        :return: The generated ID of the newly added work item, and an HTTP result code.
        """
        result = 200
        print(f"work_item: {args}")
        try:
            response = self.storage.add_work_item(args)
        except DataServiceNotReadyException as err:
            logger.error(err)
            response = "Data service not ready. Wait a minute and try again."
            result = 503
        except StorageError as err:
            logger.error("Storage error when trying to add a work item: %s", err)
            response = "A storage error occurred."
            result = 500
        return jsonify(response), result

    @use_kwargs(WorkItemSchema, location='query')
    def put(self, iditem, action=None):
        """
        Archives a work item.

        :param args: The request body data, validated and transformed by the work item schema.
        :param iditem: The ID of the work item to update.
        :param action: Specifies additional actions. The only additional action
                       is 'archive', which sets the 'archived' field of the item to True.
        :return: The ID of the archived item and an HTTP result code.
        """
        result = 200
        print(f"iditem: {iditem}, action: {action}")
        try:
            if action == 'archive':
                response = self.storage.archive_work_item(iditem)
            else:
                result = 400
                response = f"Unrecognized action {action}. The only allowed action is 'archive'."
        except DataServiceNotReadyException as err:
            logger.error(err)
            response = "Data service not ready. Wait a minute and try again."
            result = 503
        except StorageError as err:
            logger.error("Storage error when trying to add a work item: %s", err)
            response = "A storage error occurred."
            result = 500
        return jsonify(response), result
