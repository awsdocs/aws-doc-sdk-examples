# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for photo.py.
"""

from unittest.mock import MagicMock
import boto3
import pytest

from photo import Photo


def test_get_labels(monkeypatch):
    s3_resource = boto3.resource('s3')
    bucket = s3_resource.Bucket('test-bucket')
    photo = Photo(bucket)
    photo_key = 'test-photo_key'
    presigned_url = 'http://example.com/test-photo_key'

    def verify_presigned_params(ClientMethod, Params):
        assert ClientMethod == 'get_object'
        assert Params['Bucket'] == bucket.name
        assert Params['Key'] == photo_key
        return presigned_url

    monkeypatch.setattr(
        bucket.meta.client, 'generate_presigned_url', verify_presigned_params)

    got_url, result = photo.get(photo_key)
    assert got_url == {'name': photo_key, 'url': presigned_url}
    assert result == 200
