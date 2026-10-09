# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for Amazon Kinesis Data Streams wrapper and scenario.

Uses botocore.stub.Stubber — runs offline without AWS credentials.
"""

import json
from datetime import datetime, timezone

import boto3
import pytest
from botocore.stub import Stubber

from kinesis_wrapper import KinesisWrapper


# ---------------------------------------------------------------------------
# Shared constants
# ---------------------------------------------------------------------------
STREAM_NAME = "test-stream"
STREAM_ARN = "arn:aws:kinesis:us-east-1:123456789012:stream/test-stream"
SHARD_ID = "shardId-000000000000"
CONSUMER_NAME = "test-consumer"
CONSUMER_ARN = (
    "arn:aws:kinesis:us-east-1:123456789012:stream/test-stream/"
    "consumer/test-consumer:1234567890"
)
SEQ_NUMBER = "49640108810694405084505378892770476580360842874393600002"
SHARD_ITERATOR = "AAAAAAAAAAHSywljv0zEgPX4NyKdZ5wryMzP9yALs8NeKbUjp1IxtBXvT"
CREATION_TIMESTAMP = datetime(2024, 1, 15, 10, 0, 0, tzinfo=timezone.utc)


# ---------------------------------------------------------------------------
# Fixture: wrapper + stubber
# ---------------------------------------------------------------------------
@pytest.fixture
def kinesis_stubber():
    """Creates a KinesisWrapper with a Stubber for unit testing."""
    client = boto3.client("kinesis", region_name="us-east-1")
    wrapper = KinesisWrapper(client)
    stubber = Stubber(client)
    stubber.activate()
    yield wrapper, stubber
    stubber.deactivate()


# ---------------------------------------------------------------------------
# Test class
# ---------------------------------------------------------------------------
class TestKinesisWrapper:
    """Unit tests for each wrapper method."""

    # ---- ListStreams (success) ------------------------------------------
    def test_list_streams(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_response(
            "list_streams",
            {
                "HasMoreStreams": False,
                "StreamNames": [STREAM_NAME],
                "StreamSummaries": [
                    {
                        "StreamName": STREAM_NAME,
                        "StreamARN": STREAM_ARN,
                        "StreamStatus": "ACTIVE",
                        "StreamCreationTimestamp": CREATION_TIMESTAMP,
                        "StreamModeDetails": {"StreamMode": "PROVISIONED"},
                    }
                ],
            },
            {"Limit": 100},
        )
        streams = wrapper.list_streams()
        assert len(streams) == 1
        assert streams[0]["StreamName"] == STREAM_NAME
        stubber.assert_no_pending_responses()

    # ---- ListStreams (error) --------------------------------------------
    def test_list_streams_limit_exceeded(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_client_error(
            "list_streams",
            service_error_code="LimitExceededException",
            service_message="Rate exceeded",
        )
        with pytest.raises(Exception) as exc_info:
            wrapper.list_streams()
        assert exc_info.value.response["Error"]["Code"] == "LimitExceededException"

    # ---- DescribeStreamSummary (success) --------------------------------
    def test_describe_stream_summary(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_response(
            "describe_stream_summary",
            {
                "StreamDescriptionSummary": {
                    "StreamName": STREAM_NAME,
                    "StreamARN": STREAM_ARN,
                    "StreamStatus": "ACTIVE",
                    "RetentionPeriodHours": 24,
                    "StreamCreationTimestamp": CREATION_TIMESTAMP,
                    "EnhancedMonitoring": [
                        {"ShardLevelMetrics": ["IncomingBytes"]}
                    ],
                    "EncryptionType": "NONE",
                    "OpenShardCount": 1,
                    "ConsumerCount": 0,
                    "StreamModeDetails": {"StreamMode": "PROVISIONED"},
                }
            },
            {"StreamName": STREAM_NAME},
        )
        summary = wrapper.describe_stream_summary(STREAM_NAME)
        assert summary["StreamStatus"] == "ACTIVE"
        assert summary["StreamARN"] == STREAM_ARN
        stubber.assert_no_pending_responses()

    # ---- DescribeStreamSummary (not found) ------------------------------
    def test_describe_stream_summary_not_found(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_client_error(
            "describe_stream_summary",
            service_error_code="ResourceNotFoundException",
            service_message="Stream not found",
        )
        with pytest.raises(Exception) as exc_info:
            wrapper.describe_stream_summary("nonexistent-stream")
        assert (
            exc_info.value.response["Error"]["Code"]
            == "ResourceNotFoundException"
        )

    # ---- ListShards (success) -------------------------------------------
    def test_list_shards(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_response(
            "list_shards",
            {
                "Shards": [
                    {
                        "ShardId": SHARD_ID,
                        "HashKeyRange": {
                            "StartingHashKey": "0",
                            "EndingHashKey": "340282366920938463463374607431768211455",
                        },
                        "SequenceNumberRange": {
                            "StartingSequenceNumber": SEQ_NUMBER,
                        },
                    }
                ],
            },
            {"StreamName": STREAM_NAME},
        )
        shards = wrapper.list_shards(STREAM_NAME)
        assert len(shards) == 1
        assert shards[0]["ShardId"] == SHARD_ID
        stubber.assert_no_pending_responses()

    # ---- ListShards (not found) -----------------------------------------
    def test_list_shards_not_found(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_client_error(
            "list_shards",
            service_error_code="ResourceNotFoundException",
            service_message="Stream not found",
        )
        with pytest.raises(Exception) as exc_info:
            wrapper.list_shards("nonexistent-stream")
        assert (
            exc_info.value.response["Error"]["Code"]
            == "ResourceNotFoundException"
        )

    # ---- PutRecords (success) -------------------------------------------
    def test_put_records(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        records_input = [
            {
                "Data": json.dumps({"sensor_id": "s1", "temp": 72.5}).encode("utf-8"),
                "PartitionKey": "s1",
            },
            {
                "Data": json.dumps({"sensor_id": "s2", "temp": 68.3}).encode("utf-8"),
                "PartitionKey": "s2",
            },
        ]
        # FailedRecordCount has min value 1, so omit it for a success case
        # (it is not required). The SDK will treat an absent field as 0.
        stubber.add_response(
            "put_records",
            {
                "Records": [
                    {
                        "ShardId": SHARD_ID,
                        "SequenceNumber": SEQ_NUMBER,
                    },
                    {
                        "ShardId": SHARD_ID,
                        "SequenceNumber": SEQ_NUMBER,
                    },
                ],
                "EncryptionType": "NONE",
            },
            {"StreamName": STREAM_NAME, "Records": records_input},
        )
        response = wrapper.put_records(STREAM_NAME, records_input)
        assert len(response["Records"]) == 2
        stubber.assert_no_pending_responses()

    # ---- PutRecords (error) ---------------------------------------------
    def test_put_records_invalid_argument(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_client_error(
            "put_records",
            service_error_code="InvalidArgumentException",
            service_message="Invalid argument",
        )
        records_input = [
            {
                "Data": b"test",
                "PartitionKey": "pk",
            }
        ]
        with pytest.raises(Exception) as exc_info:
            wrapper.put_records(STREAM_NAME, records_input)
        assert exc_info.value.response["Error"]["Code"] == "InvalidArgumentException"

    # ---- GetShardIterator (success) -------------------------------------
    def test_get_shard_iterator(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_response(
            "get_shard_iterator",
            {"ShardIterator": SHARD_ITERATOR},
            {
                "StreamName": STREAM_NAME,
                "ShardId": SHARD_ID,
                "ShardIteratorType": "TRIM_HORIZON",
            },
        )
        iterator = wrapper.get_shard_iterator(STREAM_NAME, SHARD_ID, "TRIM_HORIZON")
        assert iterator == SHARD_ITERATOR
        stubber.assert_no_pending_responses()

    # ---- GetShardIterator (not found) -----------------------------------
    def test_get_shard_iterator_not_found(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_client_error(
            "get_shard_iterator",
            service_error_code="ResourceNotFoundException",
            service_message="Stream or shard not found",
        )
        with pytest.raises(Exception) as exc_info:
            wrapper.get_shard_iterator(STREAM_NAME, SHARD_ID)
        assert (
            exc_info.value.response["Error"]["Code"]
            == "ResourceNotFoundException"
        )

    # ---- AddTagsToStream + ListTagsForStream (success) ------------------
    def test_add_and_list_tags(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        tags = {"Project": "kinesis-demo", "Environment": "development"}

        # add_tags_to_stream returns empty body
        stubber.add_response(
            "add_tags_to_stream",
            {},
            {"StreamName": STREAM_NAME, "Tags": tags},
        )
        wrapper.add_tags_to_stream(STREAM_NAME, tags)

        # list_tags_for_stream
        stubber.add_response(
            "list_tags_for_stream",
            {
                "Tags": [
                    {"Key": "Project", "Value": "kinesis-demo"},
                    {"Key": "Environment", "Value": "development"},
                ],
                "HasMoreTags": False,
            },
            {"StreamName": STREAM_NAME},
        )
        tag_list = wrapper.list_tags_for_stream(STREAM_NAME)
        assert len(tag_list) == 2
        stubber.assert_no_pending_responses()

    # ---- RemoveTagsFromStream (success) ---------------------------------
    def test_remove_tags(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_response(
            "remove_tags_from_stream",
            {},
            {"StreamName": STREAM_NAME, "TagKeys": ["CostCenter"]},
        )
        wrapper.remove_tags_from_stream(STREAM_NAME, ["CostCenter"])
        stubber.assert_no_pending_responses()

    # ---- RegisterStreamConsumer + DeregisterStreamConsumer ---------------
    def test_register_and_deregister_consumer(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber

        # register_stream_consumer
        stubber.add_response(
            "register_stream_consumer",
            {
                "Consumer": {
                    "ConsumerName": CONSUMER_NAME,
                    "ConsumerARN": CONSUMER_ARN,
                    "ConsumerStatus": "CREATING",
                    "ConsumerCreationTimestamp": CREATION_TIMESTAMP,
                }
            },
            {"StreamARN": STREAM_ARN, "ConsumerName": CONSUMER_NAME},
        )
        consumer = wrapper.register_stream_consumer(STREAM_ARN, CONSUMER_NAME)
        assert consumer["ConsumerARN"] == CONSUMER_ARN
        assert consumer["ConsumerStatus"] == "CREATING"

        # deregister_stream_consumer
        stubber.add_response(
            "deregister_stream_consumer",
            {},
            {"ConsumerARN": CONSUMER_ARN},
        )
        wrapper.deregister_stream_consumer(CONSUMER_ARN)
        stubber.assert_no_pending_responses()

    # ---- RegisterStreamConsumer (resource in use) -----------------------
    def test_register_consumer_resource_in_use(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_client_error(
            "register_stream_consumer",
            service_error_code="ResourceInUseException",
            service_message="Consumer already exists",
        )
        with pytest.raises(Exception) as exc_info:
            wrapper.register_stream_consumer(STREAM_ARN, CONSUMER_NAME)
        assert exc_info.value.response["Error"]["Code"] == "ResourceInUseException"

    # ---- DeregisterStreamConsumer (not found) ---------------------------
    def test_deregister_consumer_not_found(self, kinesis_stubber):
        wrapper, stubber = kinesis_stubber
        stubber.add_client_error(
            "deregister_stream_consumer",
            service_error_code="ResourceNotFoundException",
            service_message="Consumer not found",
        )
        with pytest.raises(Exception) as exc_info:
            wrapper.deregister_stream_consumer(CONSUMER_ARN)
        assert (
            exc_info.value.response["Error"]["Code"]
            == "ResourceNotFoundException"
        )
