# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for kinesis_wrapper.py.

These tests follow the standard Python example test pattern: they use the
``make_stubber`` fixture (from ``test_tools`` via conftest.py) together with
the shared ``KinesisStubber`` to intercept AWS calls. Each test is
parametrized to exercise both the success path (``error_code=None``) and the
error path (``error_code="TestException"``).
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from kinesis_wrapper import KinesisStreamWrapper

STREAM_NAME = "test-stream"
STREAM_ARN = f"arn:aws:kinesis:us-east-1:123456789012:stream/{STREAM_NAME}"
SHARD_ID = "shardId-000000000000"
SHARD_ITERATOR = "test-shard-iterator"


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_streams(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisStreamWrapper(kinesis_client)
    limit = 10
    stream_names = ["stream-1", "stream-2"]

    kinesis_stubber.stub_list_streams(limit, stream_names, error_code=error_code)

    if error_code is None:
        response = wrapper.list_streams(limit=limit)
        assert response["StreamNames"] == stream_names
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_streams(limit=limit)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_stream(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisStreamWrapper(kinesis_client)
    shard_count = 2

    kinesis_stubber.stub_create_stream(
        STREAM_NAME,
        shard_count=shard_count,
        stream_mode="PROVISIONED",
        error_code=error_code,
    )

    if error_code is None:
        wrapper.create_stream(STREAM_NAME, shard_count=shard_count)
        assert wrapper.name == STREAM_NAME
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_stream(STREAM_NAME, shard_count=shard_count)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_stream(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisStreamWrapper(kinesis_client)

    kinesis_stubber.stub_describe_stream(
        STREAM_NAME, STREAM_ARN, "ACTIVE", error_code=error_code
    )

    if error_code is None:
        details = wrapper.describe_stream(STREAM_NAME)
        assert details["StreamName"] == STREAM_NAME
        assert details["StreamStatus"] == "ACTIVE"
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_stream(STREAM_NAME)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_wait_for_stream_active(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisStreamWrapper(kinesis_client)

    kinesis_stubber.stub_describe_stream(
        STREAM_NAME, STREAM_ARN, "ACTIVE", error_code=error_code
    )

    if error_code is None:
        details = wrapper.wait_for_stream_active(STREAM_NAME, max_wait_seconds=5)
        assert details["StreamStatus"] == "ACTIVE"
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.wait_for_stream_active(STREAM_NAME, max_wait_seconds=5)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_put_record(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisStreamWrapper(kinesis_client)
    data = {"sensor_id": "sensor-1", "temperature": 22.5}
    partition_key = "sensor-1"

    kinesis_stubber.stub_put_record(
        STREAM_NAME, data, partition_key, error_code=error_code
    )

    if error_code is None:
        response = wrapper.put_record(STREAM_NAME, data, partition_key)
        assert "SequenceNumber" in response
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.put_record(STREAM_NAME, data, partition_key)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_put_records(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisStreamWrapper(kinesis_client)
    records = [
        {
            "Data": {"sensor_id": f"sensor-{i}", "value": i * 10},
            "PartitionKey": f"pk-{i}",
        }
        for i in range(5)
    ]

    kinesis_stubber.stub_put_records_batch(STREAM_NAME, records, error_code=error_code)

    if error_code is None:
        response = wrapper.put_records(STREAM_NAME, records)
        assert len(response["Records"]) == len(records)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.put_records(STREAM_NAME, records)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_shard_iterator(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisStreamWrapper(kinesis_client)

    kinesis_stubber.stub_get_shard_iterator(
        STREAM_NAME,
        SHARD_ID,
        SHARD_ITERATOR,
        iterator_type="TRIM_HORIZON",
        error_code=error_code,
    )

    if error_code is None:
        shard_iterator = wrapper.get_shard_iterator(
            STREAM_NAME, SHARD_ID, iterator_type="TRIM_HORIZON"
        )
        assert shard_iterator == SHARD_ITERATOR
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_shard_iterator(
                STREAM_NAME, SHARD_ID, iterator_type="TRIM_HORIZON"
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_records(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisStreamWrapper(kinesis_client)
    limit = 10
    records = ["record-1", "record-2"]

    kinesis_stubber.stub_get_records(
        SHARD_ITERATOR, limit, records, error_code=error_code
    )

    if error_code is None:
        response = wrapper.get_records(SHARD_ITERATOR, limit=limit)
        assert len(response["Records"]) == len(records)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_records(SHARD_ITERATOR, limit=limit)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_describe_stream_summary(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisStreamWrapper(kinesis_client)

    kinesis_stubber.stub_describe_stream_summary(
        STREAM_NAME, STREAM_ARN, "ACTIVE", 2, error_code=error_code
    )

    if error_code is None:
        summary = wrapper.describe_stream_summary(STREAM_NAME)
        assert summary["StreamName"] == STREAM_NAME
        assert summary["OpenShardCount"] == 2
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_stream_summary(STREAM_NAME)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_update_shard_count(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisStreamWrapper(kinesis_client)
    target = 4
    current = 2

    kinesis_stubber.stub_update_shard_count(
        STREAM_NAME, target, current, error_code=error_code
    )

    if error_code is None:
        response = wrapper.update_shard_count(STREAM_NAME, target_shard_count=target)
        assert response["CurrentShardCount"] == current
        assert response["TargetShardCount"] == target
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.update_shard_count(STREAM_NAME, target_shard_count=target)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_stream(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisStreamWrapper(kinesis_client)

    kinesis_stubber.stub_delete_stream(
        STREAM_NAME, enforce_consumer_deletion=True, error_code=error_code
    )

    if error_code is None:
        wrapper.delete_stream(STREAM_NAME)
        assert wrapper.name is None
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_stream(STREAM_NAME)
        assert exc_info.value.response["Error"]["Code"] == error_code
