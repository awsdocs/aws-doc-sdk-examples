# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
from boto3.s3.transfer import S3UploadFailedError
from botocore.exceptions import ClientError
from flask_restful import Resource, reqparse
import werkzeug.datastructures

logger = logging.getLogger(__name__)


class PhotoList(Resource):
    """
    Encapsulates a photo list resource that represents a list of image objects that
    are stored in an Amazon Simple Storage Service (Amazon S3) bucket.
    """
    photo_types = ('.jpg', '.png')

    def __init__(self, photo_bucket):
        """
        :param photo_bucket: The S3 bucket where your photos are stored.
        """
        self.photo_bucket = photo_bucket

    def get(self):
        """
        Gets a list of photos that are stored in your S3 bucket. Only images with a
        .jpg or .png extension are returned, because these are the image types that
        Amazon Rekognition can analyze.

        :return: The list of photos and an HTTP code.
        """
        photos = []
        result = 200
        try:
            for obj in self.photo_bucket.objects.all():
                if obj.key.lower().endswith(PhotoList.photo_types):
                    photos.append({'name': obj.key, 'size': obj.size})
        except ClientError as err:
            logger.error(
                "Couldn't get photos from bucket %s. Here's why: %s: %s", self.photo_bucket.name,
                err.response['Error']['Code'], err.response['Error']['Message'])
            if err.response['Error']['Code'] == 'AccessDenied':
                result = 403
            else:
                result = 400
        return photos, result

    def post(self):
        """
        Uploads an image to your S3 bucket. The image must be sent as form data under
        an `image_file` key.

        :return: None and an HTTP code.
        """
        result = 200
        parse = reqparse.RequestParser()
        parse.add_argument('image_file', type=werkzeug.datastructures.FileStorage, location='files')
        args = parse.parse_args()
        image_file = args['image_file']
        logger.info("Got file to upload: %s", image_file.filename)
        try:
            self.photo_bucket.upload_fileobj(image_file, image_file.filename)
        except ClientError as err:
            logger.error(
                "Couldn't upload file %s. Here's why: %s: %s", image_file.filename,
                err.response['Error']['Code'], err.response['Error']['Message'])
            if err.response['Error']['Code'] == 'AccessDenied':
                result = 403
            else:
                result = 404
        except S3UploadFailedError as err:
            logger.error(
                "Couldn't upload file %s. Here's why: %s", image_file.filename, err)
            result = 400
        return None, result
