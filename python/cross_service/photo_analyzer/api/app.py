# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) to create a RESTful web service that
lets clients do the following:

* Upload photos to an Amazon Simple Storage Service (Amazon S3) bucket.
* Use Amazon Rekognition to analyze individual photos and get a list of labels that
  identify items that are detected in the photo.
* Analyze all photos in the S3 bucket and use Amazon Simple Email Service (Amazon SES)
  to email a report.
"""

import logging
import boto3
from flask import Flask
from flask_restful import Api
from flask_cors import CORS
from analysis import Analysis
from photo_list import PhotoList
from photo import Photo
from report import Report

logger = logging.getLogger(__name__)


def create_app(test_config=None):
    """
    Creates a Flask-RESTful application that lets clients upload photos to an S3 bucket,
    use Amazon Rekognition to analyze the photos, and use SES to email reports.

    To use this application, you must first create an S3 bucket and specify its name
    in the accompanying `config.py` file for the BUCKET_NAME field.

    :param test_config: Configuration to use for testing.
    """
    app = Flask(__name__)
    if test_config is None:
        app.config.from_pyfile("config.py", silent=True)
    else:
        app.config.update(test_config)
    bucket_name = app.config.get('BUCKET_NAME')
    if bucket_name is None or bucket_name == 'NEED-BUCKET-NAME':
        raise RuntimeError(
            "You must configure this app with an S3 bucket that you own by "
            "entering the name of the bucket in the BUCKET_NAME field in config.py.")

    # Suppress CORS errors when working with React during development.
    # Remove this when you deploy your application!
    CORS(app)
    api = Api(app)

    bucket = boto3.resource('s3').Bucket(app.config.get('BUCKET_NAME'))
    rekognition_client = boto3.client('rekognition')
    ses_client = boto3.client('ses')

    api.add_resource(
        PhotoList, '/photos',
        resource_class_args=(bucket,))
    api.add_resource(
        Photo, '/photos/<string:photo_key>',
        resource_class_args=(bucket,))
    api.add_resource(
        Analysis, '/photos/<string:photo_key>/labels',
        resource_class_args=(bucket.name, rekognition_client))
    api.add_resource(
        Report, '/photos/report',
        resource_class_args=(bucket, rekognition_client, ses_client))

    return app


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    try:
        create_app().run(debug=True)  # Run in debug mode for better errors during development.
    except RuntimeError as error:
        logger.error(error)
