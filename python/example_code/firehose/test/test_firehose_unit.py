# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for Amazon Data Firehose wrapper using botocore Stubber.
"""

import os
import sys

import boto3
import pytest
from botocore.stub import Stubber

sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from firehose_wrapper import FirehoseWrapper


@pytest.fixture
def firehose_stubber():
    """Creates a FirehoseWrapper with a stubbed client."""
    client = boto3.client("firehose", region_name="us-east-1")
    wrapper = FirehoseWrapper(client)
    stubber = Stubber(client)
    stubber.activate()
    yield wrapper, stubber
    stubber.deactivate()


def test_create_delivery_stream(firehose_stubber):
    """Test creating a delivery stream."""
    wrapper, stubber = firehose_stubber
    stream_name = "test-stream"
    stream_arn = "arn:aws:firehose:us-east-1:123456789012:deliverystream/test-stream"

    stubber.add_response(
        "create_delivery_stream",
        {"DeliveryStreamARN": stream_arn},
        {
            "DeliveryStreamName": stream_name,
            "DeliveryStreamType": "DirectPut",
            "ExtendedS3DestinationConfiguration": {
                "BucketARN": "arn:aws:s3:::test-bucket",
                "RoleARN": "arn:aws:iam::123456789012:role/test-role",
                "BufferingHints": {
                    "IntervalInSeconds": 60,
                    "SizeInMBs": 1,
                },
            },
        },
    )

    result = wrapper.create_delivery_stream(
        stream_name=stream_name,
        bucket_arn="arn:aws:s3:::test-bucket",
        role_arn="arn:aws:iam::123456789012:role/test-role",
        interval_in_seconds=60,
        size_in_mbs=1,
    )

    assert result == stream_arn
    stubber.assert_no_pending_responses()


def test_describe_delivery_stream(firehose_stubber):
    """Test describing a delivery stream."""
    wrapper, stubber = firehose_stubber
    stream_name = "test-stream"

    stubber.add_response(
        "describe_delivery_stream",
        {
            "DeliveryStreamDescription": {
                "DeliveryStreamName": stream_name,
                "DeliveryStreamARN": "arn:aws:firehose:us-east-1:123456789012:deliverystream/test-stream",
                "DeliveryStreamStatus": "ACTIVE",
                "DeliveryStreamType": "DirectPut",
                "VersionId": "1",
                "CreateTimestamp": "2026-01-01T00:00:00Z",
                "Destinations": [],
                "HasMoreDestinations": False,
            }
        },
        {"DeliveryStreamName": stream_name},
    )

    result = wrapper.describe_delivery_stream(stream_name)

    assert result["DeliveryStreamStatus"] == "ACTIVE"
    assert result["DeliveryStreamName"] == stream_name
    stubber.assert_no_pending_responses()


def test_list_delivery_streams(firehose_stubber):
    """Test listing delivery streams."""
    wrapper, stubber = firehose_stubber

    stubber.add_response(
        "list_delivery_streams",
        {
            "DeliveryStreamNames": ["stream-1", "stream-2", "stream-3"],
            "HasMoreDeliveryStreams": False,
        },
        {"Limit": 20, "DeliveryStreamType": "DirectPut"},
    )

    result = wrapper.list_delivery_streams(stream_type="DirectPut", limit=20)

    assert result == ["stream-1", "stream-2", "stream-3"]
    stubber.assert_no_pending_responses()


def test_put_record(firehose_stubber):
    """Test putting a single record."""
    wrapper, stubber = firehose_stubber
    stream_name = "test-stream"
    record_data = '{"sensor": "test", "value": 42}\n'

    stubber.add_response(
        "put_record",
        {
            "RecordId": "record-id-12345",
            "Encrypted": True,
        },
        {
            "DeliveryStreamName": stream_name,
            "Record": {"Data": record_data.encode("utf-8")},
        },
    )

    result = wrapper.put_record(stream_name, record_data)

    assert result["RecordId"] == "record-id-12345"
    assert result["Encrypted"] is True
    stubber.assert_no_pending_responses()


def test_put_record_batch(firehose_stubber):
    """Test putting a batch of records."""
    wrapper, stubber = firehose_stubber
    stream_name = "test-stream"
    records = ['{"id": 1}\n', '{"id": 2}\n', '{"id": 3}\n']

    stubber.add_response(
        "put_record_batch",
        {
            "FailedPutCount": 0,
            "Encrypted": True,
            "RequestResponses": [
                {"RecordId": "id-1"},
                {"RecordId": "id-2"},
                {"RecordId": "id-3"},
            ],
        },
        {
            "DeliveryStreamName": stream_name,
            "Records": [{"Data": r.encode("utf-8")} for r in records],
        },
    )

    result = wrapper.put_record_batch(stream_name, records)

    assert result["FailedPutCount"] == 0
    assert result["SuccessCount"] == 3
    assert len(result["SuccessfulRecordIds"]) == 3
    stubber.assert_no_pending_responses()


def test_put_record_batch_partial_failure(firehose_stubber):
    """Test putting a batch with partial failures."""
    wrapper, stubber = firehose_stubber
    stream_name = "test-stream"
    records = ['{"id": 1}\n', '{"id": 2}\n']

    stubber.add_response(
        "put_record_batch",
        {
            "FailedPutCount": 1,
            "Encrypted": False,
            "RequestResponses": [
                {"RecordId": "id-1"},
                {
                    "ErrorCode": "ServiceUnavailableException",
                    "ErrorMessage": "Service temporarily unavailable",
                },
            ],
        },
        {
            "DeliveryStreamName": stream_name,
            "Records": [{"Data": r.encode("utf-8")} for r in records],
        },
    )

    result = wrapper.put_record_batch(stream_name, records)

    assert result["FailedPutCount"] == 1
    assert result["SuccessCount"] == 1
    assert len(result["FailedRecords"]) == 1
    assert result["FailedRecords"][0]["ErrorCode"] == "ServiceUnavailableException"
    stubber.assert_no_pending_responses()


def test_delete_delivery_stream(firehose_stubber):
    """Test deleting a delivery stream."""
    wrapper, stubber = firehose_stubber
    stream_name = "test-stream"

    stubber.add_response(
        "delete_delivery_stream",
        {},
        {
            "DeliveryStreamName": stream_name,
            "AllowForceDelete": True,
        },
    )

    wrapper.delete_delivery_stream(stream_name)
    stubber.assert_no_pending_responses()


def test_tag_delivery_stream(firehose_stubber):
    """Test tagging a delivery stream."""
    wrapper, stubber = firehose_stubber
    stream_name = "test-stream"
    tags = [
        {"Key": "Environment", "Value": "Test"},
        {"Key": "Project", "Value": "Unit"},
    ]

    stubber.add_response(
        "tag_delivery_stream",
        {},
        {
            "DeliveryStreamName": stream_name,
            "Tags": tags,
        },
    )

    wrapper.tag_delivery_stream(stream_name, tags)
    stubber.assert_no_pending_responses()


def test_list_tags_for_delivery_stream(firehose_stubber):
    """Test listing tags for a delivery stream."""
    wrapper, stubber = firehose_stubber
    stream_name = "test-stream"

    stubber.add_response(
        "list_tags_for_delivery_stream",
        {
            "Tags": [
                {"Key": "Environment", "Value": "Test"},
            ],
            "HasMoreTags": False,
        },
        {"DeliveryStreamName": stream_name},
    )

    result = wrapper.list_tags_for_delivery_stream(stream_name)

    assert len(result) == 1
    assert result[0]["Key"] == "Environment"
    stubber.assert_no_pending_responses()


def test_start_delivery_stream_encryption(firehose_stubber):
    """Test starting encryption on a delivery stream."""
    wrapper, stubber = firehose_stubber
    stream_name = "test-stream"

    stubber.add_response(
        "start_delivery_stream_encryption",
        {},
        {
            "DeliveryStreamName": stream_name,
            "DeliveryStreamEncryptionConfigurationInput": {"KeyType": "AWS_OWNED_CMK"},
        },
    )

    wrapper.start_delivery_stream_encryption(stream_name)
    stubber.assert_no_pending_responses()


def test_update_destination(firehose_stubber):
    """Test updating a delivery stream destination."""
    wrapper, stubber = firehose_stubber
    stream_name = "test-stream"

    # First call: describe to get version ID and destination ID
    stubber.add_response(
        "describe_delivery_stream",
        {
            "DeliveryStreamDescription": {
                "DeliveryStreamName": stream_name,
                "DeliveryStreamARN": "arn:aws:firehose:us-east-1:123456789012:deliverystream/test-stream",
                "DeliveryStreamStatus": "ACTIVE",
                "DeliveryStreamType": "DirectPut",
                "VersionId": "1",
                "CreateTimestamp": "2026-01-01T00:00:00Z",
                "Destinations": [
                    {"DestinationId": "dest-id-1"},
                ],
                "HasMoreDestinations": False,
            }
        },
        {"DeliveryStreamName": stream_name},
    )

    # Second call: update destination
    stubber.add_response(
        "update_destination",
        {},
        {
            "DeliveryStreamName": stream_name,
            "CurrentDeliveryStreamVersionId": "1",
            "DestinationId": "dest-id-1",
            "ExtendedS3DestinationUpdate": {
                "BufferingHints": {
                    "IntervalInSeconds": 120,
                    "SizeInMBs": 5,
                }
            },
        },
    )

    wrapper.update_destination(stream_name, interval_in_seconds=120, size_in_mbs=5)
    stubber.assert_no_pending_responses()
