# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the Amazon Kinesis Data Streams basics wrapper (kinesis_wrapper.py).

Uses the shared ``make_stubber`` fixture and ``KinesisStubber`` so the tests run
offline without AWS credentials, following the standard pattern used throughout
``python/example_code``.
"""

import boto3
import pytest
from botocore.exceptions import ClientError

from kinesis_wrapper import KinesisWrapper

STREAM_NAME = "test-stream"
STREAM_ARN = "arn:aws:kinesis:us-east-1:123456789012:stream/test-stream"
SHARD_ID = "shardId-000000000000"
CONSUMER_NAME = "test-consumer"
CONSUMER_ARN = (
    "arn:aws:kinesis:us-east-1:123456789012:stream/test-stream/"
    "consumer/test-consumer:1234567890"
)


@pytest.mark.parametrize("error_code", [None, "LimitExceededException"])
def test_list_streams(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)
    stream_names = [f"{STREAM_NAME}-{index}" for index in range(3)]

    kinesis_stubber.stub_list_streams(stream_names, error_code=error_code)

    if error_code is None:
        got_streams = wrapper.list_streams()
        assert [s["StreamName"] for s in got_streams] == stream_names
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_streams()
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_describe_stream_summary(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)

    kinesis_stubber.stub_describe_stream_summary(
        STREAM_NAME, STREAM_ARN, error_code=error_code
    )

    if error_code is None:
        summary = wrapper.describe_stream_summary(STREAM_NAME)
        assert summary["StreamName"] == STREAM_NAME
        assert summary["StreamARN"] == STREAM_ARN
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.describe_stream_summary(STREAM_NAME)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_list_shards(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)
    shard_ids = [SHARD_ID, "shardId-000000000001"]

    kinesis_stubber.stub_list_shards(STREAM_NAME, shard_ids, error_code=error_code)

    if error_code is None:
        shards = wrapper.list_shards(STREAM_NAME)
        assert [s["ShardId"] for s in shards] == shard_ids
    else:
        # max_wait=0 disables retries so the error surfaces immediately.
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_shards(STREAM_NAME, max_wait=0, poll_interval=0)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize(
    "error_code", ["ResourceInUseException", "ResourceNotFoundException"]
)
def test_list_shards_retries_after_create(make_stubber, error_code):
    """
    A freshly created stream can report ACTIVE before its shards are listable,
    so ListShards may transiently raise ResourceInUseException or
    ResourceNotFoundException. The wrapper should retry and succeed once the
    shards become available.
    """
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)
    shard_ids = [SHARD_ID]

    # First call fails with a transient error, second call succeeds.
    kinesis_stubber.stub_list_shards(STREAM_NAME, shard_ids, error_code=error_code)
    kinesis_stubber.stub_list_shards(STREAM_NAME, shard_ids)

    # poll_interval=0 keeps the test fast (no real sleep).
    shards = wrapper.list_shards(STREAM_NAME, max_wait=10, poll_interval=0)
    assert [s["ShardId"] for s in shards] == shard_ids


def test_list_shards_gives_up_after_max_wait(make_stubber):
    """
    If ResourceInUseException persists past max_wait, the error should surface.
    """
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)

    # Only one stubbed response; max_wait=0 means no retry is attempted.
    kinesis_stubber.stub_list_shards(
        STREAM_NAME, [SHARD_ID], error_code="ResourceInUseException"
    )

    with pytest.raises(ClientError) as exc_info:
        wrapper.list_shards(STREAM_NAME, max_wait=0, poll_interval=0)
    assert exc_info.value.response["Error"]["Code"] == "ResourceInUseException"


@pytest.mark.parametrize("error_code", [None, "InvalidArgumentException"])
def test_put_records(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)
    records = [
        {"Data": b"record-1", "PartitionKey": "key-1"},
        {"Data": b"record-2", "PartitionKey": "key-2"},
    ]

    kinesis_stubber.stub_put_records_batch(STREAM_NAME, records, error_code=error_code)

    if error_code is None:
        response = wrapper.put_records(STREAM_NAME, records)
        assert len(response["Records"]) == len(records)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.put_records(STREAM_NAME, records)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_get_shard_iterator(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)
    shard_iter = "test-iterator"

    kinesis_stubber.stub_get_shard_iterator(
        STREAM_NAME, SHARD_ID, shard_iter, error_code=error_code
    )

    if error_code is None:
        got_iter = wrapper.get_shard_iterator(STREAM_NAME, SHARD_ID, "LATEST")
        assert got_iter == shard_iter
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_shard_iterator(STREAM_NAME, SHARD_ID, "LATEST")
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_add_tags_to_stream(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)
    tags = {"Project": "kinesis-demo", "Environment": "development"}

    kinesis_stubber.stub_add_tags_to_stream(STREAM_NAME, tags, error_code=error_code)

    if error_code is None:
        wrapper.add_tags_to_stream(STREAM_NAME, tags)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.add_tags_to_stream(STREAM_NAME, tags)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_list_tags_for_stream(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)
    tags = {"Project": "kinesis-demo", "Environment": "development"}

    kinesis_stubber.stub_list_tags_for_stream(STREAM_NAME, tags, error_code=error_code)

    if error_code is None:
        tag_list, has_more = wrapper.list_tags_for_stream(STREAM_NAME)
        assert len(tag_list) == len(tags)
        assert has_more is False
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_tags_for_stream(STREAM_NAME)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_remove_tags_from_stream(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)
    tag_keys = ["CostCenter"]

    kinesis_stubber.stub_remove_tags_from_stream(
        STREAM_NAME, tag_keys, error_code=error_code
    )

    if error_code is None:
        wrapper.remove_tags_from_stream(STREAM_NAME, tag_keys)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.remove_tags_from_stream(STREAM_NAME, tag_keys)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceInUseException"])
def test_register_stream_consumer(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)

    kinesis_stubber.stub_register_stream_consumer(
        STREAM_ARN, CONSUMER_NAME, CONSUMER_ARN, error_code=error_code
    )

    if error_code is None:
        consumer = wrapper.register_stream_consumer(STREAM_ARN, CONSUMER_NAME)
        assert consumer["ConsumerName"] == CONSUMER_NAME
        assert consumer["ConsumerARN"] == CONSUMER_ARN
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.register_stream_consumer(STREAM_ARN, CONSUMER_NAME)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "ResourceNotFoundException"])
def test_deregister_stream_consumer(make_stubber, error_code):
    kinesis_client = boto3.client("kinesis")
    kinesis_stubber = make_stubber(kinesis_client)
    wrapper = KinesisWrapper(kinesis_client)

    kinesis_stubber.stub_deregister_stream_consumer(CONSUMER_ARN, error_code=error_code)

    if error_code is None:
        wrapper.deregister_stream_consumer(CONSUMER_ARN)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.deregister_stream_consumer(CONSUMER_ARN)
        assert exc_info.value.response["Error"]["Code"] == error_code
