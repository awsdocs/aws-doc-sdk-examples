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
            response = Inference.detect_anomalies(
                lookoutvision_client, project_name, model_version, photo)
            mock_file.assert_called_with(photo, 'rb')
            assert response['IsAnomalous'] == anomalous
            assert response['Confidence']  == confidence
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


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_reject_on_classification(make_stubber, error_code):

    photo = 's3://doc-example-bucket/test-photo.jpeg'
    prediction = {
        "IsAnomalous": True,
        "Confidence": 0.9
    }

    confidence_limit = 0.5

    if error_code is None:
        Inference.reject_on_classification(photo, prediction, confidence_limit)
    else:
        prediction = {
            "IsAnomalousX": True,
            "Confidence": 0.9
        }
        with pytest.raises(KeyError) as exc_info:
            Inference.reject_on_classification(
                photo, prediction, confidence_limit)
        assert exc_info.typename == "KeyError"


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_reject_on_anomaly_types(make_stubber, error_code):

    photo = 's3://doc-example-bucket/test-photo.jpeg'
    prediction = {
        "IsAnomalous": True,
        "Confidence": 0.9,
        "Anomalies": [
            {"Name": "broken"},
            {"Name": "cracked"}
        ]
    }

    confidence_limit = 0.5
    anomaly_types_limit = 1

    if error_code is None:
        Inference.reject_on_anomaly_types(
            photo, prediction, confidence_limit, anomaly_types_limit)
    else:
        prediction = {
            "IsAnomalous": True,
            "Confidence": 0.9,
            "AnomaliesX": [
                {"Name": "broken"},
                {"Name": "cracked"}
            ]
        }
        with pytest.raises(KeyError) as exc_info:
            Inference.reject_on_anomaly_types(
                photo, prediction, confidence_limit, anomaly_types_limit)
        assert exc_info.typename == "KeyError"


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_reject_on_coverage(make_stubber, error_code):

    photo = 's3://doc-example-bucket/test-photo.jpeg'
    prediction = {
        "IsAnomalous": True,
        "Confidence": 0.9,
        "Anomalies": [
            {
                "Name": "broken",
                "PixelAnomaly": {"TotalPercentageArea": 0.10}
            },
            {
                "Name": "broken",
                "PixelAnomaly": {"TotalPercentageArea": 0.50}
            }
        ]
    }

    confidence_limit = 0.5
    coverage_limit = 1
    anomaly_label = "broken"

    if error_code is None:
        Inference.reject_on_coverage(
            photo, prediction, confidence_limit, anomaly_label, coverage_limit)
    else:
        prediction = {
            "IsAnomalous": True,
            "Confidence": 0.9,
            "Anomalies": [
                {
                    "Name": "broken",
                    "PixelAnomalyX": {"TotalPercentageArea": 0.10}
                },
                {
                    "Name": "broken",
                    "PixelAnomalyX": {"TotalPercentageArea": 0.50}
                }
            ]
        }
        with pytest.raises(KeyError) as exc_info:
            Inference.reject_on_coverage(
                photo, prediction, confidence_limit, anomaly_label, coverage_limit)
        assert exc_info.typename == "KeyError"

@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_reject_on_classification(make_stubber, error_code):

    photo = 's3://doc-example-bucket/test-photo.jpeg'
    prediction = {
        "IsAnomalous": True,
        "Confidence": 0.9
    }

    confidence_limit = 0.5

    if error_code is None:
        Inference.reject_on_classification(photo, prediction, confidence_limit)
    else:
        prediction = {
            "IsAnomalousX": True,
            "Confidence": 0.9
        }
        with pytest.raises(KeyError) as exc_info:
            Inference.reject_on_classification(
                photo, prediction, confidence_limit)
        assert exc_info.typename == "KeyError"


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_reject_on_anomaly_types(make_stubber, error_code):

    photo = 's3://doc-example-bucket/test-photo.jpeg'
    prediction = {
        "IsAnomalous": True,
        "Confidence": 0.9,
        "Anomalies": [
            {"Name": "broken"},
            {"Name": "cracked"}
        ]
    }

    confidence_limit = 0.5
    anomaly_types_limit = 1

    if error_code is None:
        Inference.reject_on_anomaly_types(
            photo, prediction, confidence_limit, anomaly_types_limit)
    else:
        prediction = {
            "IsAnomalous": True,
            "Confidence": 0.9,
            "AnomaliesX": [
                {"Name": "broken"},
                {"Name": "cracked"}
            ]
        }
        with pytest.raises(KeyError) as exc_info:
            Inference.reject_on_anomaly_types(
                photo, prediction, confidence_limit, anomaly_types_limit)
        assert exc_info.typename == "KeyError"


@pytest.mark.parametrize('error_code', [None, 'TestException'])
def test_reject_on_coverage(make_stubber, error_code):

    photo = 's3://doc-example-bucket/test-photo.jpeg'
    prediction = {
        "IsAnomalous": True,
        "Confidence": 0.9,
        "Anomalies": [
            {
                "Name": "broken",
                "PixelAnomaly": {"TotalPercentageArea": 0.10}
            },
            {
                "Name": "broken",
                "PixelAnomaly": {"TotalPercentageArea": 0.50}
            }
        ]
    }

    confidence_limit = 0.5
    coverage_limit = 1
    anomaly_label = "broken"

    if error_code is None:
        Inference.reject_on_coverage(
            photo, prediction, confidence_limit, anomaly_label, coverage_limit)
    else:
        prediction = {
            "IsAnomalous": True,
            "Confidence": 0.9,
            "Anomalies": [
                {
                    "Name": "broken",
                    "PixelAnomalyX": {"TotalPercentageArea": 0.10}
                },
                {
                    "Name": "broken",
                    "PixelAnomalyX": {"TotalPercentageArea": 0.50}
                }
            ]
        }
        with pytest.raises(KeyError) as exc_info:
            Inference.reject_on_coverage(
                photo, prediction, confidence_limit, anomaly_label, coverage_limit)
        assert exc_info.typename == "KeyError"
