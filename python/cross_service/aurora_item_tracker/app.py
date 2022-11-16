# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Shows how to use the AWS SDK for Python (Boto3) to create a REST web service that
stores work items in an Amazon Aurora Serverless database and uses Amazon Simple
Email Service (Amazon SES) to let client applications do the following:

* Get a list of active or archived work items.
* Add new work items to the database.
* Archive a currently active work item.
* Send a report of work items to an email recipient.

This web service is intended to be used in conjunction with the Elwing React
client found in the resources/clients/react/elwing folder of this repository.
For more information on how to set up resources, run the web service, and run the
React client, see the accompanying README.
"""

import boto3
from flask import Flask
from flask_cors import CORS

from item_list import ItemList
from report import Report
from storage import Storage


def create_app(test_config=None):
    """
    Creates the Flask service, which responds to HTTP requests through its routes.

    To use this application, you must first specify the following in the accompanying
    config.py file:

    * CLUSTER_ARN The Amazon Resource Name (ARN) of an Amazon Aurora DB cluster.
    * SECRET_ARN The ARN of an AWS Secrets Manager secret that contains credentials for the
      database.
    * DATABASE The name of a database where work items are stored.
    * TABLE_NAME The name of the table in the database where work items are stored.
    * SENDER_EMAIL To send an email report, you must also specify the email address from which
      emails are sent.
    * SECRET_KEY The secret key Flask uses for sessions. Change this temporary value
      to a secret value for production.

    :param test_config: Configuration to use for testing.
    """
    app = Flask(__name__)
    if test_config is None:
        app.config.from_pyfile("config.py", silent=True)
    else:
        app.config.update(test_config)
    cluster_arn = app.config.get('CLUSTER_ARN')
    secret_arn = app.config.get('SECRET_ARN')
    database = app.config.get('DATABASE')
    table_name = app.config.get('TABLE_NAME')
    sender_email = app.config.get('SENDER_EMAIL')
    if (cluster_arn is None or cluster_arn == 'NEED-CLUSTER-ARN'
            or secret_arn is None or secret_arn == 'NEED-SECRET-ARN'
            or database is None or database == 'NEED-DATABASE'
            or table_name is None or table_name == 'NEED-TABLE-NAME'):
        raise RuntimeError(
            "To run this app, you must first enter configuration information in config.py.")

    # Suppress CORS errors when working with React during development.
    # Important: Remove this when you deploy your application.
    CORS(app)

    if app.config.get('TESTING'):
        rdsdata_client = app.config.get('RDSDATA_CLIENT')
        ses_client = app.config.get('SES_CLIENT')
    else:
        rdsdata_client = boto3.client('rds-data')
        ses_client = boto3.client('ses')

    storage = Storage(cluster_arn, secret_arn, database, table_name, rdsdata_client)

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
