# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for photo_list.py.
"""

from unittest.mock import MagicMock
import boto3
from boto3.s3.transfer import S3UploadFailedError
from botocore.exceptions import ClientError
import pytest

from photo_list import PhotoList, reqparse


@pytest.mark.parametrize('error_code', [None, 'TestException', 'AccessDenied'])
def test_get_photo_list(make_stubber, error_code):
    s3_resource = boto3.resource('s3')
    s3_stubber = make_stubber(s3_resource.meta.client)
    bucket = s3_resource.Bucket('test-bucket')
    photo_list = PhotoList(bucket)
    photos = ['photo.jpg', 'photo.PNG']
    all_keys = photos + ['photo.txt', 'photo.pdf']

    s3_stubber.stub_list_objects(bucket.name, all_keys, error_code=error_code)

    got_photos, result = photo_list.get()
    if error_code is None:
        assert [got_photo['name'] for got_photo in got_photos] == photos
        assert result == 200
    elif error_code == 'AccessDenied':
        assert result == 403
    else:
        assert result == 400


@pytest.mark.parametrize(
    'error_code', [None, 'TestException', 'AccessDenied', 'S3UploadFailedError'])
def test_post_photo(make_stubber, monkeypatch, error_code):
    s3_resource = boto3.resource('s3')
    make_stubber(s3_resource.meta.client)
    bucket = s3_resource.Bucket('test-bucket')
    photo_list = PhotoList(bucket)
    image_file = MagicMock(filename='test-image_file')

    mock_parser = MagicMock(
        name='mock_parser', return_value=MagicMock(
            parse_args=MagicMock(return_value={'image_file': image_file})))
    monkeypatch.setattr(reqparse, 'RequestParser', mock_parser)

    def mock_upload(file_arg, filename_arg):
        assert file_arg == image_file
        assert filename_arg == image_file.filename
        if error_code is not None:
            if error_code == 'S3UploadFailedError':
                raise S3UploadFailedError
            else:
                raise ClientError(
                    {'Error': {'Code': error_code, 'Message': 'test error'}}, 'test-op')

    monkeypatch.setattr(bucket, 'upload_fileobj', mock_upload)

    _, result = photo_list.post()
    if error_code is None:
        assert result == 200
    elif error_code == 'AccessDenied':
        assert result == 403
    elif error_code == 'S3UploadFailedError':
        assert result == 400
    else:
        assert result == 404
