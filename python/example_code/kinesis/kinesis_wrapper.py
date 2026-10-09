# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Kinesis Data Streams wrapper class for advanced stream management operations.

This module provides a wrapper around the Kinesis Data Streams client that demonstrates:
- Listing and inspecting streams and their shards
- Batch-writing records with PutRecords
- Obtaining shard iterators for reading records
- Managing stream tags for cost allocation
- Registering and deregistering enhanced fan-out consumers
"""

import json
import logging
import time
from datetime import datetime, timezone

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kinesis.KinesisWrapper.decl]
class KinesisWrapper:
    """Encapsulates Amazon Kinesis Data Streams operations."""

    def __init__(self, kinesis_client):
        """
        Initializes the KinesisWrapper with a Kinesis client.

        :param kinesis_client: A Boto3 Kinesis client.
        """
        self.kinesis_client = kinesis_client

    @classmethod
    def from_client(cls):
        """
        Creates a KinesisWrapper instance with a default Kinesis client.

        :return: A KinesisWrapper instance.
        """
        kinesis_client = boto3.client("kinesis")
        return cls(kinesis_client)
# snippet-end:[python.example_code.kinesis.KinesisWrapper.decl]

    # snippet-start:[python.example_code.kinesis.ListStreams]
    def list_streams(self, limit=100):
        """
        Lists Kinesis data streams in the account.

        :param limit: The maximum number of streams to list.
        :return: A list of stream summary dictionaries.
        """
        try:
            streams = list()
            response = self.kinesis_client.list_streams(Limit=limit)
            streams.extend(response.get("StreamSummaries", list()))
            while response.get("HasMoreStreams", False):
                next_name = response["StreamSummaries"][-1]["StreamName"]
                response = self.kinesis_client.list_streams(
                    Limit=limit, ExclusiveStartStreamName=next_name
                )
                streams.extend(response.get("StreamSummaries", list()))
            logger.info("Listed %s stream(s).", len(streams))
            return streams
        except ClientError as err:
            if err.response["Error"]["Code"] == "LimitExceededException":
                logger.error(
                    "Request rate too high for ListStreams. Retry after a brief delay."
                )
            raise
    # snippet-end:[python.example_code.kinesis.ListStreams]

    # snippet-start:[python.example_code.kinesis.DescribeStreamSummary]
    def describe_stream_summary(self, stream_name):
        """
        Provides a summarized description of a Kinesis data stream.

        :param stream_name: The name of the stream to describe.
        :return: A dictionary containing the stream description summary.
        """
        try:
            response = self.kinesis_client.describe_stream_summary(
                StreamName=stream_name
            )
            summary = response.get("StreamDescriptionSummary", dict())
            logger.info(
                "Described stream summary for '%s'. Status: %s.",
                stream_name,
                summary.get("StreamStatus", "UNKNOWN"),
            )
            return summary
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' not found. Verify the stream name.", stream_name
                )
            raise
    # snippet-end:[python.example_code.kinesis.DescribeStreamSummary]

    # snippet-start:[python.example_code.kinesis.ListShards]
    def list_shards(self, stream_name):
        """
        Lists the shards in a Kinesis data stream.

        :param stream_name: The name of the stream whose shards to list.
        :return: A list of shard dictionaries.
        """
        try:
            shards = list()
            response = self.kinesis_client.list_shards(StreamName=stream_name)
            shards.extend(response.get("Shards", list()))
            while "NextToken" in response:
                response = self.kinesis_client.list_shards(
                    NextToken=response["NextToken"]
                )
                shards.extend(response.get("Shards", list()))
            logger.info("Listed %s shard(s) for stream '%s'.", len(shards), stream_name)
            return shards
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' not found. Verify the stream is ACTIVE.",
                    stream_name,
                )
            raise
    # snippet-end:[python.example_code.kinesis.ListShards]

    # snippet-start:[python.example_code.kinesis.PutRecords]
    def put_records(self, stream_name, records):
        """
        Writes multiple data records into a Kinesis data stream in a single call.

        :param stream_name: The name of the stream to write records to.
        :param records: A list of dicts, each with 'Data' (bytes) and 'PartitionKey' (str).
        :return: The response from the PutRecords call.
        """
        try:
            response = self.kinesis_client.put_records(
                StreamName=stream_name, Records=records
            )
            failed_count = response.get("FailedRecordCount", 0)
            logger.info(
                "Put %s record(s) to stream '%s' with %s failure(s).",
                len(records),
                stream_name,
                failed_count,
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidArgumentException":
                logger.error(
                    "Invalid argument for PutRecords. Check record sizes and parameters."
                )
            raise
    # snippet-end:[python.example_code.kinesis.PutRecords]

    # snippet-start:[python.example_code.kinesis.GetShardIterator]
    def get_shard_iterator(self, stream_name, shard_id, iterator_type="TRIM_HORIZON"):
        """
        Gets a shard iterator for reading data records from a shard.

        :param stream_name: The name of the stream.
        :param shard_id: The ID of the shard.
        :param iterator_type: The shard iterator type (e.g., TRIM_HORIZON, LATEST).
        :return: A shard iterator string.
        """
        try:
            response = self.kinesis_client.get_shard_iterator(
                StreamName=stream_name,
                ShardId=shard_id,
                ShardIteratorType=iterator_type,
            )
            shard_iterator = response.get("ShardIterator", None)
            logger.info(
                "Got shard iterator for shard '%s' of type '%s'.",
                shard_id,
                iterator_type,
            )
            return shard_iterator
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' or shard '%s' not found. Verify shard ID is correct.",
                    stream_name,
                    shard_id,
                )
            raise
    # snippet-end:[python.example_code.kinesis.GetShardIterator]

    # snippet-start:[python.example_code.kinesis.AddTagsToStream]
    def add_tags_to_stream(self, stream_name, tags):
        """
        Adds or updates tags for a Kinesis data stream.

        :param stream_name: The name of the stream to tag.
        :param tags: A dictionary of tag key-value pairs.
        """
        try:
            self.kinesis_client.add_tags_to_stream(
                StreamName=stream_name, Tags=tags
            )
            logger.info(
                "Added %s tag(s) to stream '%s'.", len(tags), stream_name
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' not found. Cannot add tags.", stream_name
                )
            raise
    # snippet-end:[python.example_code.kinesis.AddTagsToStream]

    # snippet-start:[python.example_code.kinesis.ListTagsForStream]
    def list_tags_for_stream(self, stream_name):
        """
        Lists the tags for a Kinesis data stream.

        :param stream_name: The name of the stream whose tags to list.
        :return: A list of tag dictionaries with 'Key' and 'Value'.
        """
        try:
            response = self.kinesis_client.list_tags_for_stream(
                StreamName=stream_name
            )
            tags = response.get("Tags", list())
            has_more = response.get("HasMoreTags", False)
            logger.info(
                "Listed %s tag(s) for stream '%s'. HasMoreTags: %s.",
                len(tags),
                stream_name,
                has_more,
            )
            return tags
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' not found when listing tags.", stream_name
                )
            raise
    # snippet-end:[python.example_code.kinesis.ListTagsForStream]

    # snippet-start:[python.example_code.kinesis.RemoveTagsFromStream]
    def remove_tags_from_stream(self, stream_name, tag_keys):
        """
        Removes tags from a Kinesis data stream.

        :param stream_name: The name of the stream to remove tags from.
        :param tag_keys: A list of tag keys to remove.
        """
        try:
            self.kinesis_client.remove_tags_from_stream(
                StreamName=stream_name, TagKeys=tag_keys
            )
            logger.info(
                "Removed tag(s) %s from stream '%s'.", tag_keys, stream_name
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' not found for tag removal.", stream_name
                )
            raise
    # snippet-end:[python.example_code.kinesis.RemoveTagsFromStream]

    # snippet-start:[python.example_code.kinesis.RegisterStreamConsumer]
    def register_stream_consumer(self, stream_arn, consumer_name):
        """
        Registers an enhanced fan-out consumer with a Kinesis data stream.

        :param stream_arn: The ARN of the stream.
        :param consumer_name: The name for the consumer.
        :return: A dictionary containing consumer details.
        """
        try:
            response = self.kinesis_client.register_stream_consumer(
                StreamARN=stream_arn, ConsumerName=consumer_name
            )
            consumer = response.get("Consumer", dict())
            logger.info(
                "Registered consumer '%s' with status '%s'.",
                consumer_name,
                consumer.get("ConsumerStatus", "UNKNOWN"),
            )
            return consumer
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceInUseException":
                logger.error(
                    "Consumer '%s' already exists or stream is not available.",
                    consumer_name,
                )
            raise
    # snippet-end:[python.example_code.kinesis.RegisterStreamConsumer]

    # snippet-start:[python.example_code.kinesis.DeregisterStreamConsumer]
    def deregister_stream_consumer(self, consumer_arn):
        """
        Deregisters an enhanced fan-out consumer.

        :param consumer_arn: The ARN of the consumer to deregister.
        """
        try:
            self.kinesis_client.deregister_stream_consumer(
                ConsumerARN=consumer_arn
            )
            logger.info("Deregistered consumer with ARN '%s'.", consumer_arn)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Consumer not found. It may have already been deregistered."
                )
            raise
    # snippet-end:[python.example_code.kinesis.DeregisterStreamConsumer]

    def wait_for_stream_active(self, stream_name, max_wait=60, poll_interval=2):
        """
        Waits for a stream to become ACTIVE by polling DescribeStreamSummary.

        :param stream_name: The name of the stream.
        :param max_wait: Maximum seconds to wait.
        :param poll_interval: Seconds between polls.
        :return: The stream description summary when ACTIVE.
        """
        elapsed = 0
        while elapsed < max_wait:
            summary = self.describe_stream_summary(stream_name)
            status = summary.get("StreamStatus", "UNKNOWN")
            if status == "ACTIVE":
                return summary
            logger.info("Stream status: %s. Waiting...", status)
            time.sleep(poll_interval)
            elapsed += poll_interval
        raise TimeoutError(
            f"Stream '{stream_name}' did not become ACTIVE within {max_wait}s."
        )

    def wait_for_consumer_active(
        self, stream_arn, consumer_name, max_wait=60, poll_interval=2
    ):
        """
        Waits for a consumer to become ACTIVE by polling DescribeStreamConsumer.

        :param stream_arn: The ARN of the stream.
        :param consumer_name: The consumer name.
        :param max_wait: Maximum seconds to wait.
        :param poll_interval: Seconds between polls.
        :return: The consumer description when ACTIVE.
        """
        elapsed = 0
        while elapsed < max_wait:
            response = self.kinesis_client.describe_stream_consumer(
                StreamARN=stream_arn, ConsumerName=consumer_name
            )
            desc = response.get("ConsumerDescription", dict())
            status = desc.get("ConsumerStatus", "UNKNOWN")
            if status == "ACTIVE":
                return desc
            logger.info("Consumer status: %s. Waiting...", status)
            time.sleep(poll_interval)
            elapsed += poll_interval
        raise TimeoutError(
            f"Consumer '{consumer_name}' did not become ACTIVE within {max_wait}s."
        )
