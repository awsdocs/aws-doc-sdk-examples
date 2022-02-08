# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with the Flask web framework to create
a web application that manages work items in a DynamoDB table and sends
email reports by using Amazon SES.
"""

from datetime import datetime
import logging

import boto3
from flask import Flask, render_template, redirect, url_for, request, flash

from report import Report, ReportError
from storage import Storage, StorageError

logger = logging.getLogger(__name__)


def create_app(test_config=None):
    """
    Creates the Flask app, which responds to HTTP requests through its routes.

    The app is configured by settings in an optional config.py file. Config options are:

    * SECRET_KEY The secret key Flask uses for sessions.
    * TABLE_NAME The name of an existing DynamoDB table that stores work items.
    * ITEM_TRACKER_PROFILE The optional name of an IAM role that grants restricted
                           permissions used by the app. When this option is set, Boto3
                           assumes the specified role and the app is authorized to make
                           only requests allowed by the role. Otherwise, the app runs
                           with the permissions of the default user.
    """
    app = Flask(__name__)
    app.config.from_mapping(
        SECRET_KEY='change-for-production!',
        TABLE_NAME='doc-example-work-item-tracker',
        ITEM_TRACKER_PROFILE=None)
    if test_config is None:
        app.config.from_pyfile("config.py", silent=True)
    else:
        app.config.update(test_config)
    restricted_profile = app.config.get('ITEM_TRACKER_PROFILE')
    if restricted_profile is not None:
        logger.info("Using credentials from restricted profile %s.", restricted_profile)
        boto3.setup_default_session(profile_name=restricted_profile)
    else:
        logger.info("Using default credentials.")

    def add_formatted_date(work_item):
        """
        Adds a formatted date to a work item.

        :param work_item: The work item to add the date to.
        """
        work_item['formatted_date'] = datetime.fromisoformat(
            work_item['created_date']).strftime('%b %d %Y')

    def get_work_items(storage, status_filter):
        """
        Gets work items from storage.

        :param storage: A Storage object that can get items from the DynamoDB table.
        :param status_filter: When specified, only items with this status are returned.
        :return: The requested work items and any error that occurred.
        """
        work_items = []
        error = None
        try:
            work_items = storage.get_items(status_filter)
        except StorageError as err:
            error = str(err)
        for work_item in work_items:
            add_formatted_date(work_item)
        return work_items, error

    @app.route('/')
    @app.route('/items')
    def items():
        """
        A route that gets work items currently stored in the table.

        :return: A rendered page that displays the current table of work items. When
                 `status_filter` is specified in the query parameters, the
                 returned list of items contains only items with the specified status.
        """
        storage = Storage.from_context()
        logger.info("Getting items for table %s.", storage.table.name)
        status_filter = request.args.get('status_filter', 'All')
        work_items, error = get_work_items(storage, status_filter)
        return render_template(
            'items.html', table=storage.table.name, items=work_items,
            status_filter=status_filter, error=error)

    @app.route('/item/', methods=['GET', 'POST'])
    @app.route('/item/<item_id>', methods=['GET', 'POST'])
    def item(item_id=None):
        """
        A route that handles an individual item.

        GET <item_id>  When item_id is not specified, renders an empty form that can
                       be used to add a work item.
                       When item_id is specified, renders a form that contains the
                       specified work item's data. The form can be used to edit the
                       work item.
        POST <item_id> When item_id is not specified, adds a work item to the table.
                       When item_id is specified, updates the work item.
                       After a POST, redirects to the /items/ route.
        """
        storage = Storage.from_context()
        if request.method == 'GET':
            work_item = {'item_id': item_id}
            error = None
            if item_id is not None:
                try:
                    work_item = storage.get_item(item_id)
                    add_formatted_date(work_item)
                except StorageError as err:
                    error = str(err)
            return render_template('item.html', **work_item, action='get', error=error)
        elif request.method == 'POST':
            work_item = {
                'item_id': item_id,
                'name': request.form.get('name'),
                'description': request.form.get('description'),
                'status': request.form.get('status')}
            try:
                storage.add_or_update_item(work_item)
            except StorageError as err:
                return render_template(
                    'item.html', **work_item, action='add or update', error=err)
            else:
                return redirect(url_for('items'))

    @app.route('/items/delete/<item_id>')
    def delete_item(item_id):
        """
        A route that deletes a work item from the table.

        :param item_id: The ID of the work item to delete.
        :return: Redirects to the /items/ route.
        """
        storage = Storage.from_context()
        try:
            storage.delete_item(item_id)
        except StorageError as err:
            flash(f"Couldn't delete item '{item_id}'.")
            flash(str(err))
        return redirect(url_for('items'))

    @app.route('/report/<status_filter>', methods=['GET', 'POST'])
    def report(status_filter):
        """
        A route that handles sending email reports.

        GET  Renders a form that can be used to send an email report of work items.
             Before the form is rendered, the current list of work items is retrieved
             from the table.
        POST Sends an email report to the email recipient specified in the form. Both
             the sender and recipient email addresses must be verified with Amazon SES.
             Redirects to the /items/ route.

        :param status_filter: When specified, only work items with the specified status
                              are included in the report.
        """
        storage = Storage.from_context()
        work_items, error = get_work_items(storage, status_filter)
        if request.method == 'GET':
            return render_template(
                'report.html', status_filter=status_filter, table=storage.table.name,
                items=work_items, error=error)
        elif request.method == 'POST':
            reporter = Report.from_context()
            message = request.form.get('message')
            text_report = render_template(
                'email.txt', message=message, items=work_items)
            html_report = render_template(
                'email.html', message=message, items=work_items)
            try:
                reporter.send(
                    request.form.get('sender'),
                    request.form.get('recipient'),
                    f"Report for items with status '{status_filter}'",
                    text_report,
                    html_report)
            except ReportError as err:
                error = str(err)
            if not error:
                flash(f"Report sent.")
            if error:
                flash("Report not sent!")
                flash(str(error))
            return redirect(url_for('items'))

    return app


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    create_app().run(debug=True)
