# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
from botocore.exceptions import ClientError
from flask_restful import Resource

logger = logging.getLogger(__name__)


class Analysis(Resource):
    """
    Encapsulates a resource that analyzes images in an Amazon Simple Storage Service
    (Amazon S3) bucket and returns labels that are detected within the images.
    """
    def __init__(self, photo_bucket_name, rekognition_client):
        """
        :param photo_bucket_name: The name of the bucket where your images are stored.
        :param rekognition_client: A Boto3 Amazon Rekognition client.
        """
        self.photo_bucket_name = photo_bucket_name
        self.rekognition_client = rekognition_client

    def get(self, photo_key):
        """
        Analyzes an image that is stored in an S3 bucket and returns a list of labels
        that identify items that were detected in the image.

        :param photo_key: The key of the photo object in S3.
        :return: The list of detected labels and an HTTP result code.
        """
        labels = []
        result = 200
        try:
            response = self.rekognition_client.detect_labels(
                Image={'S3Object': {'Bucket': self.photo_bucket_name, 'Name': photo_key}})
            labels = response.get('Labels', [])
            logger.info("Found %s labels in %s.", len(response['Labels']), photo_key)
        except ClientError as err:
            logger.info(
                "Couldn't detect labels in %s. Here's why: %s: %s", photo_key,
                err.response['Error']['Code'], err.response['Error']['Message'])
            result = 400
        return labels, result
