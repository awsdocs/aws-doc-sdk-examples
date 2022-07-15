# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to create a RESTful web service that
stores work items in an Amazon Aurora Serverless database and uses Amazon Simple
Email Service (Amazon SES) to let client applications do the following:

* Get a list of active or archived work items.
* Add new work items to the database.
* Archive a currently active work item.
* Send a report of work items to an email recipient.

This web service is intended to be used in conjunction with the item tracker React
client found in the resources/clients/react/item-tracker folder of this repository.
For more information on how to set up resources, run the web service, and run the
React client, see the accompanying README.
"""

import logging
import boto3
from flask import Flask
from flask_restful import Api
from flask_cors import CORS
from storage import Storage
from item_list import ItemList
from report import Report

logger = logging.getLogger(__name__)


def create_app(test_config=None):
    """
    Creates a Flask-RESTful application that lets clients manage a list of work items.

    To use this application, you must first specify the following in the accompanying
    config.py file:
    * The Amazon Resource Name (ARN) of an Amazon Aurora DB cluster.
    * The name of a database and table where work items are stored.
    * The ARN of an AWS Secrets Manager secret that contains credentials for the
      database.
    * To send an email report, you must also specify the email address from which
      emails are sent.

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
    api = Api(app)

    rdsdata_client = boto3.client('rds-data')
    ses_client = boto3.client('ses')
    storage = Storage(cluster_arn, secret_arn, database, table_name, rdsdata_client)

    api.add_resource(
        ItemList, '/items', '/items/', '/items/<string:item_state>', '/items/<int:work_item_id>',
        resource_class_args=(storage,))
    api.add_resource(
        Report, '/report',
        resource_class_args=(storage, sender_email, ses_client))

    return app


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    try:
        create_app().run(debug=True)  # Run in debug mode for more descriptive errors during development.
    except RuntimeError as error:
        logger.error(error)
