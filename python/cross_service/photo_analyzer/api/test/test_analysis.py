# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for analysis.py.
"""

from unittest.mock import MagicMock
import boto3
import pytest

from analysis import Analysis


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_get_photo_analysis(make_stubber, error_code):
    rekognition_client = boto3.client('rekognition')
    rekognition_stubber = make_stubber(rekognition_client)
    bucket_name = 'test-bucket-name'
    analysis = Analysis(bucket_name, rekognition_client)
    photo_key = 'test-photo'
    labels = []
    for index in range(3):
        mock_label = MagicMock(confidence=index, instances=[], parents=[])
        mock_label.name = f'label-{index}1'
        labels.append(mock_label)

    rekognition_stubber.stub_detect_labels(
        {'S3Object': {'Bucket': bucket_name, 'Name': photo_key}}, None, labels,
        error_code=error_code)

    got_labels, got_result = analysis.get(photo_key)
    if error_code is None:
        assert ([got_label['Name'] for got_label in got_labels] ==
                [label.name for label in labels])
        assert ([got_label['Confidence'] for got_label in got_labels] ==
                [label.confidence for label in labels])
        assert got_result == 200
    else:
        assert got_labels == []
        assert got_result == 400
