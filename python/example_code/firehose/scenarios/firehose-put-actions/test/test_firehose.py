# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import sys
from unittest import mock

import boto3
import pytest
from botocore.exceptions import NoCredentialsError
from moto import mock_cloudwatch, mock_firehose

sys.path.append("../../firehose-put-actions")
from firehose import FirehoseClient, load_sample_data


# Sample configuration mock
class MockConfig:
    def __init__(self):
        self.delivery_stream_name = "test_stream"
        self.region = "us-west-2"
        self.sample_data_file = "mock_sample_data.json"


@pytest.fixture
def mock_config():
    return MockConfig()


@pytest.fixture
def firehose_client(mock_config):
    with mock_firehose(), mock_cloudwatch():
        # Set up Firehose and CloudWatch mocks
        client = boto3.client("firehose", region_name=mock_config.region)
        client.create_delivery_stream(
            DeliveryStreamName=mock_config.delivery_stream_name,
            S3DestinationConfiguration={
                "RoleARN": "arn:aws:iam::000000000000:role/FirehoseRole",
                "BucketARN": "arn:aws:s3:::mybucket",
                "Prefix": "myfolder/",
                "BufferingHints": {"SizeInMBs": 1, "IntervalInSeconds": 60},
            },
        )
        yield FirehoseClient(mock_config)


@pytest.fixture
def mock_sample_data():
    return {"key": "value"}


@mock.patch("builtins.open", new_callable=mock.mock_open, read_data='{"key": "value"}')
def test_load_sample_data(mock_open):
    loaded_data = load_sample_data("mock_sample_data.json")
    assert loaded_data == {"key": "value"}


def test_put_record(firehose_client):
    record = {"key": "value"}
    try:
        firehose_client.put_record(record)
    except RuntimeError as e:
        assert "Firehose PutRecord(Batch to S3 destination failed" in str(e)


def test_put_record_batch(firehose_client):
    records = [{"key": "value"} for _ in range(5)]
    try:
        firehose_client.put_record_batch(records)
    except RuntimeError as e:
        assert "Firehose PutRecord(Batch to S3 destination failed" in str(e)


def test_monitor_metrics(firehose_client):
    firehose_client.monitor_metrics()


def test_no_credentials_error(mock_config):
    with mock.patch("boto3.client") as mock_boto_client:
        mock_boto_client.side_effect = NoCredentialsError
        with pytest.raises(NoCredentialsError):
            FirehoseClient(mock_config)
