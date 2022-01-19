# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from flask import g
import logging
import boto3
from boto3.dynamodb.conditions import Attr
from botocore.exceptions import ClientError


logger = logging.getLogger(__name__)


class ReportError(Exception):
    pass


class Report:
    def __init__(self, ses_client):
        self.ses_client = ses_client

    @classmethod
    def from_context(cls):
        """
        Creates a report object based on context.
        """
        report = getattr(g, 'report', None)
        if report is None:
            report = cls(boto3.client('ses'))
            g.report = report
        return report

    def send(self, sender, recipient, subject, text_message, html_message):
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
