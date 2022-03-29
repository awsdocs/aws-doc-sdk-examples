# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
from botocore.exceptions import ClientError
from flask import render_template
from flask_restful import Resource, reqparse

logger = logging.getLogger(__name__)


class Report(Resource):
    """
    Encapsulates a report resource that can get reports of all images in your
    Amazon Simple Storage Service (Amazon S3) bucket and send emails about them.
    """
    def __init__(self, photo_bucket, rekognition_client, ses_client):
        """
        :param photo_bucket: The S3 bucket where your photos are stored.
        :param rekognition_client: A Boto3 Amazon Rekognition client.
        :param ses_client: A Boto3 Amazon Simple Email Service (Amazon SES) client.
        """
        self.photo_bucket = photo_bucket
        self.rekognition_client = rekognition_client
        self.ses_client = ses_client

    def get(self):
        """
        Uses Amazon Rekognition to analyze all images in your S3 bucket and returns a
        report as a list of comma-separated value (CSV) records.

        :return: The CSV report and an HTTP code.
        """
        result = 200
        report_csv = ['Photo,Label,Confidence']
        try:
            for photo in self.photo_bucket.objects.all():
                try:
                    response = self.rekognition_client.detect_labels(
                        Image={
                            'S3Object': {
                                'Bucket': self.photo_bucket.name, 'Name': photo.key}})
                    logger.info("Found %s labels in %s.", len(response['Labels']), photo.key)
                    for label in response.get('Labels', []):
                        report_csv.append(
                            ','.join((photo.key, label['Name'], str(label['Confidence']))))
                except ClientError as err:
                    logger.warning(
                        "Couldn't detect labels in %s. Here's why: %s: %s", photo.key,
                        err.response['Error']['Code'], err.response['Error']['Message'])
        except ClientError as err:
            logger.error(
                "Couldn't list photos in bucket '%s'. Here's why: %s: %s",
                self.photo_bucket.name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            result = 400
        return report_csv, result

    def post(self):
        """
        Sends an email of a previously created report. Two versions of the email are
        included:

        * An HTML version that formats the CSV report as an HTML table by using Flask's
          template rendering feature. Email clients that can render HTML receive this
          version.
        * A text version that includes the report in its original CSV format. Email
          clients that cannot render HTML receive this version.

        When your SES account is in the sandbox, both the sender and recipient email
        addresses must be registered with SES.

        JSON request parameters:
            sender: The sender's email address.
            recipient: The recipient's email address.
            subject: The subject of the email.
            message: The body of the email message.
            analysis_labels: A previously generated report of image labels formatted
                             as CSV records.
        """
        result = 200
        parser = reqparse.RequestParser()
        parser.add_argument('sender', location='json')
        parser.add_argument('recipient', location='json')
        parser.add_argument('subject', location='json')
        parser.add_argument('message', location='json')
        parser.add_argument('analysis_labels', type=list, location='json')
        args = parser.parse_args()
        html_report = render_template(
            'report.html', message=args['message'],
            headers=args['analysis_labels'][0].split(','),
            labels=[label.split(',') for label in args['analysis_labels'][1:]]
        )
        text_labels = '\n'.join(args['analysis_labels'])
        try:
            pass
            self.ses_client.send_email(
                Source=args['sender'],
                Destination={'ToAddresses': [args['recipient']]},
                Message={
                    'Subject': {'Data': args['subject']},
                    'Body': {
                        'Html': {'Data': html_report},
                        'Text': {
                            'Data': f"{args['message']}\n\n{text_labels}"}}})
        except ClientError as err:
            logger.exception(
                "Couldn't send email. Here's why: %s: %s",
                err.response['Error']['Code'], err.response['Error']['Message'])
            result = 400
        return None, result
