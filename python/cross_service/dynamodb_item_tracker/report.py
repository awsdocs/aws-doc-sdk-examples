# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon SES to send an email
report.
"""

from flask import g
import logging
import boto3
from botocore.exceptions import ClientError


logger = logging.getLogger(__name__)


class ReportError(Exception):
    pass


class Report:
    """
    Encapsulates a report that can be sent by using Amazon SES.
    """
    def __init__(self, ses_client):
        """
        :param ses_client: A Boto3 Amazon SES client.
        """
        self.ses_client = ses_client

    @classmethod
    def from_context(cls):
        """
        Creates a report object based on context. The object is stored in Flask
        session globals and reused if it exists.
        """
        # pylint: disable=assigning-non-slot
        report = getattr(g, 'report', None)
        if report is None:
            report = cls(boto3.client('ses'))
            g.report = report
        return report

    def send(self, sender, recipient, subject, text_message, html_message):
        """
        Sends an email.

        :param sender: The sender's email address.
        :param recipient: The recipient's email address.
        :param subject: The subject of the email.
        :param text_message: The text version of the email. This is rendered when the
                             recipient's email client cannot render HTML.
        :param html_message: The HTML version of the email. This is rendered when the
                             recipient's email client can render HTML.
        """
        try:
            self.ses_client.send_email(
                Source=sender,
                Destination={'ToAddresses': [recipient]},
                Message={
                    'Subject': {'Data': subject},
                    'Body': {
                        'Text': {'Data': text_message},
                        'Html': {'Data': html_message}}})
        except ClientError as err:
            logger.exception("Couldn't send email from %s to %s.", sender, recipient)
            raise ReportError(err)
