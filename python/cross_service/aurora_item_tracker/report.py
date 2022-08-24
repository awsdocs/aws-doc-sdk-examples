# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Shows how to get a list of work items from a storage object, render it in both
HTML and text formats, and use Amazon Simple Email Service (Amazon SES) to send it as
an email report.
"""

import logging
from botocore.exceptions import ClientError
from flask import render_template
from flask_restful import Resource, reqparse

logger = logging.getLogger(__name__)


class Report(Resource):
    """
    Encapsulates a report resource that can get reports of work items from an
    Amazon Aurora database and use Amazon SES to send emails about them.
    """
    def __init__(self, storage, email_sender, ses_client):
        """
        :param storage: An object that manages moving data in and out of the underlying
                        database.
        :param email_sender: The email address from which the email report is sent.
        :param ses_client: A Boto3 Amazon SES client.
        """
        self.storage = storage
        self.email_sender = email_sender
        self.ses_client = ses_client

    def post(self):
        """
        Gets a list of work items from the database, makes a report of them, and
        sends an email. Two versions of the email are included:

        * An HTML version that formats the report as an HTML table by using Flask's
          template rendering feature. Email clients that can render HTML receive this
          version.
        * A text version that includes the report as a list of Python dicts. Email
          clients that cannot render HTML receive this version.

        When your Amazon SES account is in the sandbox, both the sender and recipient
        email addresses must be registered with Amazon SES.

        JSON request parameters:
            email: The recipient's email address.
            status: The status of work items that are included in the report.

        :return: An HTTP result code.
        """
        result = 200
        parser = reqparse.RequestParser()
        parser.add_argument('email', location='json')
        parser.add_argument('status', location='json')
        args = parser.parse_args()
        work_items = self.storage.get_work_items(args['status'])
        html_report = render_template(
            'report.html', status=args['status'], work_items=work_items)
        text_report = "\n".join(str(i) for i in work_items)
        try:
            self.ses_client.send_email(
                Source=self.email_sender,
                Destination={'ToAddresses': [args['email']]},
                Message={
                    'Subject': {'Data': f"Work items: {args['status']}"},
                    'Body': {
                        'Html': {'Data': html_report},
                        'Text': {'Data': text_report}}})
        except ClientError as err:
            logger.exception(
                "Couldn't send email. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            result = 400
        return None, result
