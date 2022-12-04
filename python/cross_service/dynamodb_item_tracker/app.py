# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Shows how to use the AWS SDK for Python (Boto3) to create a REST web service that
stores work items in an Amazon DynamoDB table and uses Amazon Simple
Email Service (Amazon SES) to let client applications do the following:

* Get a list of active or archived work items.
* Add new work items to the table.
* Archive a currently active work item.
* Send a report of work items to an email recipient.

This web service is intended to be used in conjunction with the Elwing React
client found in the resources/clients/react/elwing folder of this repository.
For more information on how to set up resources, run the web service, and run the
React client, see the accompanying README.
"""

import logging

import boto3
from flask import Flask
from flask_cors import CORS

from item_list import ItemList
from report import Report
from storage import Storage, StorageError

logger = logging.getLogger(__name__)


def create_app(test_config=None):
    """
    Creates the Flask service, which responds to HTTP requests through its routes.

    To use this application, you must first specify the following in the accompanying
    config.py file:

    * TABLE_NAME The name of an existing DynamoDB table that stores work items.
    * SENDER_EMAIL The email address from which report emails are sent.
    * SECRET_KEY The secret key Flask uses for sessions. Change this temporary value
      to a secret value for production.

    :param test_config: Configuration to use for testing.
    """
    app = Flask(__name__)
    if test_config is None:
        app.config.from_pyfile("config.py", silent=True)
    else:
        app.config.update(test_config)
    table_name = app.config.get('TABLE_NAME')
    sender_email = app.config.get('SENDER_EMAIL')
    if table_name is None or table_name == 'NEED-TABLE-NAME':
        raise RuntimeError(
            "To run this app, you must first enter configuration information in config.py.")

    # Suppress CORS errors when working with React during development.
    # Important: Remove this when you deploy your application.
    CORS(app)

    if app.config.get('TESTING'):
        dynamodb_resource = app.config.get('DYNAMODB_RESOURCE')
        ses_client = app.config.get('SES_CLIENT')
    else:
        dynamodb_resource = boto3.resource('dynamodb')
        ses_client = boto3.client('ses')
    table = dynamodb_resource.Table(app.config['TABLE_NAME'])
    storage = Storage(table)

    item_list_view = ItemList.as_view('item_list_api', storage)
    report_view = Report.as_view('report_api', storage, sender_email, ses_client)
    app.add_url_rule(
        '/api/items', defaults={'iditem': None}, view_func=item_list_view, methods=['GET'],
        strict_slashes=False)
    app.add_url_rule(
        '/api/items', view_func=item_list_view, methods=['POST'], strict_slashes=False)
    app.add_url_rule(
        '/api/items/<string:iditem>', view_func=item_list_view, methods=['GET', 'PUT'])
    app.add_url_rule(
        '/api/items/<string:iditem>:<string:action>', view_func=item_list_view, methods=['PUT'])
    app.add_url_rule(
        '/api/items:report', view_func=report_view, methods=['POST'])

    return app
