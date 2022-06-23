# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for inference.py.
"""

import imghdr
from unittest.mock import patch, mock_open
import boto3
from botocore.exceptions import ClientError
import pytest

from inference import Inference


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_detect_anomalies(make_stubber, monkeypatch, error_code):
    lookoutvision_client = boto3.client('lookoutvision')
    lookoutvision_stubber = make_stubber(lookoutvision_client)
    project_name = 'test-project_name'
    model_version = 'test-model'
    photo = 'test.jpeg'
    content_type = f'image/jpeg'
    image_contents = b'test-contents'
    anomalous = True
    confidence = .5

    monkeypatch.setattr(imghdr, 'what', lambda f: 'jpeg')

    lookoutvision_stubber.stub_detect_anomalies(
        project_name, model_version, content_type, image_contents,
        anomalous, confidence, error_code=error_code)

    if error_code is None:
        with patch('builtins.open', mock_open(read_data=image_contents)) as mock_file:
            got_anom, got_con = Inference.detect_anomalies(
                lookoutvision_client, project_name, model_version, photo)
            mock_file.assert_called_with(photo, 'rb')
            assert got_anom == anomalous
            assert got_con == confidence
    else:
        with pytest.raises(ClientError) as exc_info:
            with patch('builtins.open', mock_open(read_data=image_contents)):
                Inference.detect_anomalies(
                    lookoutvision_client, project_name, model_version, photo)
            assert exc_info.value.response['Error']['Code'] == error_code


def test_download_from_s3(make_stubber, monkeypatch):
    s3_resource = boto3.resource('s3')
    photo = 's3://doc-example-bucket/test-photo.jpeg'
    file = 'test-photo.jpeg'

    monkeypatch.setattr(
        s3_resource.meta.client, 'download_file',
        lambda Filename, Bucket, Key, ExtraArgs, Callback, Config: None)

    got_file = Inference.download_from_s3(s3_resource, photo)
    assert got_file == file
