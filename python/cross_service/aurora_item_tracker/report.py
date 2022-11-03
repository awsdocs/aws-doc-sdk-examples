# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Shows how to get a list of work items from a storage object, render it in both
HTML and text formats, and use Amazon Simple Email Service (Amazon SES) to send it as
an email report.

When the list of items is longer than a specified threshold, it is included as a CSV
attachment to the email instead of in the body of the email itself.
"""

from datetime import datetime
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.application import MIMEApplication
import logging
from botocore.exceptions import ClientError
from flask import jsonify, render_template
from flask.views import MethodView
from webargs import fields
from webargs.flaskparser import use_kwargs

from storage import StorageError

logger = logging.getLogger(__name__)


class Report(MethodView):
    """
    Encapsulates a report resource that gets work items from an
    Amazon Aurora Serverless database and uses Amazon SES to send emails about them.
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

    def _format_mime_message(self, recipient, text, html, attachment, charset='utf-8'):
        """
        Formats the report as a MIME message. When the the email contains an attachment,
        it must be sent in MIME format.
        """
        msg = MIMEMultipart('mixed')
        msg['Subject'] = "Work items"
        msg['From'] = self.email_sender
        msg['To'] = recipient
        msg_body = MIMEMultipart('alternative')

        textpart = MIMEText(text.encode(charset), 'plain', charset)
        htmlpart = MIMEText(html.encode(charset), 'html', charset)
        msg_body.attach(textpart)
        msg_body.attach(htmlpart)

        att = MIMEApplication(attachment.encode(charset))
        att.add_header('Content-Disposition', 'attachment', filename='work_items.csv')
        msg.attach(msg_body)
        msg.attach(att)
        return msg

    @use_kwargs({'email': fields.Str(required=True)})
    def post(self, email):
        """
        Gets a list of work items from storage, makes a report of them, and
        sends an email. The email is sent in both HTMl and text format.

        When ten or fewer items are in the report, the items are included in the body
        of the email. Otherwise, the items are included as an attachment in CSV format.

        When your Amazon SES account is in the sandbox, both the sender and recipient
        email addresses must be registered with Amazon SES.

        :param email: The recipient's email address.
        :return: An error message and an HTTP result code.
        """
        response = None
        result = 200
        try:
            work_items = self.storage.get_work_items(archived=False)
            snap_time = datetime.now()
            print(f"Sending report of {len(work_items)} items to {email}.")
            html_report = render_template(
                'report.html', work_items=work_items, item_count=len(work_items), snap_time=snap_time)
            text_report = render_template(
                'report.txt', work_items=work_items, item_count=len(work_items), snap_time=snap_time)
            if len(work_items) > 10:
                item_csv = render_template('work_items.csv', work_items=work_items)
                mime_msg = self._format_mime_message(email, text_report, html_report, item_csv)
                response = self.ses_client.send_raw_email(
                    Source=self.email_sender,
                    Destinations=[email],
                    RawMessage={'Data': mime_msg.as_string()})
            else:
                self.ses_client.send_email(
                    Source=self.email_sender,
                    Destination={'ToAddresses': [email]},
                    Message={
                        'Subject': {'Data': f"Work items"},
                        'Body': {
                            'Html': {'Data': html_report},
                            'Text': {'Data': text_report}}})
        except StorageError as err:
            logger.exception(
                "Couldn't get work items from storage. Here's why: %s", err)
            response = "A storage error occurred."
            result = 500
        except ClientError as err:
            logger.exception(
                "Couldn't send email. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            response = "An email error occurred."
            result = 500
        return jsonify(response), result
