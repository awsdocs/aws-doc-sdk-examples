# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Shows how to get a list of work items from a storage object, render it in both
# HTML and text formats, and use Amazon Simple Email Service (Amazon SES) to send it as
# an email report.
#
# When the list of items is longer than a specified threshold, it is included as a CSV
# attachment to the email instead of in the body of the email itself.

# import csv
# from datetime import datetime
# from email.mime.multipart import MIMEMultipart
# from email.mime.text import MIMEText
# from email.mime.application import MIMEApplication
require 'logger'
# from io import StringIO
# from botocore.exceptions import ClientError
# from flask import jsonify, render_template
# from flask.views import MethodView
# from webargs import fields
# from webargs.flaskparser import use_kwargs

require 'db_wrapper'

logger = Logger.new(STDOUT)

# Encapsulates a report resource that gets work items from an
# Amazon Aurora Serverless database and uses Amazon SES to send emails about them.
class Report
    # @param storage: An object that manages moving data in and out of the underlying database.
    # @param email_sender: The email address from which the email report is sent.
    # @param ses_client: A Amazon SES client.
    def initialize(storage, email_sender, ses_client)
        @rds_wrapper = storage
        @email_sender = email_sender
        @ses_client = ses_client

    # Formats the report as a MIME message. When the the email contains an attachment,
    # it must be sent in MIME format.
    def format_mime_message(recipient, text, html, attachment, charset='utf-8')
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

    # Renders work items to CSV format, with the field names as a header row.
    #
    # @param work_items: The work items to include in the CSV output.
    # @return: Work items rendered to a string in CSV format.
    def render_csv(work_items)
        with StringIO() as csv_buffer:
            writer = csv.DictWriter(
                csv_buffer, ['description', 'guide', 'status', 'username', 'archived'],
                extrasaction='ignore')
            writer.writeheader()
            writer.writerows(work_items)
            csv_items = csv_buffer.getvalue()
        return csv_items

    # @use_kwargs({'email': fields.Str(required=True)})
    # Gets a list of work items from storage, makes a report of them, and
    # sends an email. The email is sent in both HTML and text format.
    #
    # When ten or fewer items are in the report, the items are included in the body
    # of the email. Otherwise, the items are included as an attachment in CSV format.
    #
    # When your Amazon SES account is in the sandbox, both the sender and recipient
    # email addresses must be registered with Amazon SES.
    #
    # @param email: The recipient's email address.
    # @return: An error message and an HTTP result code.
    def post(email)
        begin
            response = None
            result = 200
            work_items = self.storage.get_work_items(archived=False)
            snap_time = datetime.now()
            logger.info(f"Sending report of %s items to %s.", len(work_items), email)
            html_report = render_template(
                'report.html', work_items=work_items, item_count=len(work_items), snap_time=snap_time)
            csv_items = self._render_csv(work_items)
            text_report = render_template(
                'report.txt', work_items=csv_items, item_count=len(work_items), snap_time=snap_time)
            if len(work_items) > 10
                mime_msg = self._format_mime_message(email, text_report, html_report, csv_items)
                response = self.ses_client.send_raw_email(
                    Source=self.email_sender,
                    Destinations=[email],
                    RawMessage={'Data': mime_msg.as_string()})
            else
                ses_client.send_email(
                    Source=self.email_sender,
                    Destination={'ToAddresses': [email]},
                    Message={
                        'Subject': {'Data': f"Work items"},
                        'Body': {
                            'Html': {'Data': html_report},
                            'Text': {'Data': text_report}}})
        rescue RDSClientError as err:
            logger.exception(
                "Couldn't get work items from storage. Here's why: %s", err)
            response = "A storage error occurred."
            result = 500
        rescue ClientError as err:
            logger.exception(
                "Couldn't send email. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            response = "An email error occurred."
            result = 500
        ensure
            return jsonify(response), result
        end