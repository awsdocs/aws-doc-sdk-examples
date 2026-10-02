# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Kinesis Data Streams
to create, configure, produce to, consume from, scale, and clean up a stream.
"""

import json
import logging
import time

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kinesis.KinesisStreamWrapper.decl]
class KinesisStreamWrapper:
    """Encapsulates Amazon Kinesis Data Streams operations."""

    def __init__(self, kinesis_client):
        """
        :param kinesis_client: A Boto3 Kinesis client.
        """
        self.kinesis_client = kinesis_client
        self.name = None
        self.details = None

    @classmethod
    def from_client(cls) -> "KinesisStreamWrapper":
        """
        Creates a KinesisStreamWrapper instance with a default Boto3 Kinesis client.

        :return: An instance of KinesisStreamWrapper.
        """
        kinesis_client = boto3.client("kinesis")
        return cls(kinesis_client)

    # snippet-end:[python.example_code.kinesis.KinesisStreamWrapper.decl]

    # snippet-start:[python.example_code.kinesis.ListStreams]
    def list_streams(self, limit: int = 10) -> dict:
        """
        Lists Kinesis data streams in the current account.

        :param limit: The maximum number of streams to list.
        :return: A dict containing StreamNames, HasMoreStreams, and StreamSummaries.
        """
        try:
            response = self.kinesis_client.list_streams(Limit=limit)
            logger.info(
                "Listed %d stream(s).", len(response.get("StreamNames", list()))
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "LimitExceededException":
                logger.error(
                    "Request rate exceeded for ListStreams. "
                    "Please retry after a brief delay. %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.kinesis.ListStreams]

    def create_stream(self, name: str, shard_count: int = 2) -> None:
        """
        Creates a new Kinesis data stream in provisioned mode.

        :param name: The name of the stream to create.
        :param shard_count: The number of shards for the stream.
        """
        try:
            self.kinesis_client.create_stream(
                StreamName=name,
                ShardCount=shard_count,
                StreamModeDetails={"StreamMode": "PROVISIONED"},
            )
            self.name = name
            logger.info("Created stream %s with %d shards.", name, shard_count)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceInUseException":
                logger.error(
                    "A stream named '%s' already exists. %s",
                    name,
                    err.response["Error"]["Message"],
                )
            raise

    def describe_stream(self, name: str) -> dict:
        """
        Gets metadata about a Kinesis data stream.

        :param name: The name of the stream.
        :return: The StreamDescription dict.
        """
        try:
            response = self.kinesis_client.describe_stream(StreamName=name)
            self.name = name
            self.details = response["StreamDescription"]
            logger.info("Described stream %s.", name)
            return self.details
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' does not exist. %s",
                    name,
                    err.response["Error"]["Message"],
                )
            raise

    def wait_for_stream_active(self, name: str, max_wait_seconds: int = 60) -> dict:
        """
        Polls DescribeStream until the stream is ACTIVE or the timeout expires.

        :param name: The name of the stream.
        :param max_wait_seconds: Maximum seconds to wait.
        :return: The StreamDescription dict once ACTIVE.
        :raises TimeoutError: If the stream does not become ACTIVE within the timeout.
        """
        start = time.time()
        poll_interval = 3
        while True:
            details = self.describe_stream(name)
            status = details.get("StreamStatus", "")
            if status == "ACTIVE":
                logger.info("Stream %s is ACTIVE.", name)
                return details
            elapsed = time.time() - start
            # Check the deadline before sleeping so we do not overshoot it.
            if elapsed + poll_interval >= max_wait_seconds:
                raise TimeoutError(
                    f"Stream '{name}' did not become ACTIVE within "
                    f"{max_wait_seconds} seconds. Current status: {status}"
                )
            logger.info(
                "Stream %s status is %s. Waiting... (%.0fs elapsed)",
                name,
                status,
                elapsed,
            )
            time.sleep(poll_interval)

    def put_record(self, stream_name: str, data: dict, partition_key: str) -> dict:
        """
        Writes a single data record to a Kinesis data stream.

        :param stream_name: The name of the stream.
        :param data: The data payload to write (will be JSON-encoded).
        :param partition_key: The partition key for shard routing.
        :return: A dict containing ShardId and SequenceNumber.
        """
        try:
            response = self.kinesis_client.put_record(
                StreamName=stream_name,
                Data=json.dumps(data),
                PartitionKey=partition_key,
            )
            logger.info(
                "Put record to stream %s, shard %s, sequence %s.",
                stream_name,
                response.get("ShardId", ""),
                response.get("SequenceNumber", ""),
            )
            return response
        except ClientError as err:
            if (
                err.response["Error"]["Code"]
                == "ProvisionedThroughputExceededException"
            ):
                logger.error(
                    "Write throughput exceeded for stream '%s'. "
                    "Reduce request rate or add more shards. %s",
                    stream_name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-start:[python.example_code.kinesis.PutRecords]
    def put_records(self, stream_name: str, records: list) -> dict:
        """
        Writes multiple data records to a Kinesis data stream in a single call.

        :param stream_name: The name of the stream.
        :param records: A list of dicts, each with 'Data' (dict) and 'PartitionKey' (str).
        :return: The PutRecords response dict.
        """
        try:
            request_records = list()
            for rec in records:
                request_records.append(
                    {
                        "Data": json.dumps(rec["Data"]),
                        "PartitionKey": rec["PartitionKey"],
                    }
                )
            response = self.kinesis_client.put_records(
                StreamName=stream_name, Records=request_records
            )
            failed = response.get("FailedRecordCount", 0)
            logger.info(
                "Put %d records to stream %s (%d failed).",
                len(request_records),
                stream_name,
                failed,
            )
            return response
        except ClientError as err:
            if (
                err.response["Error"]["Code"]
                == "ProvisionedThroughputExceededException"
            ):
                logger.error(
                    "Batch write throughput exceeded for stream '%s'. "
                    "Consider splitting the batch or adding shards. %s",
                    stream_name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.kinesis.PutRecords]

    def get_shard_iterator(
        self,
        stream_name: str,
        shard_id: str,
        iterator_type: str = "TRIM_HORIZON",
    ) -> str:
        """
        Gets a shard iterator for reading records from a specific position in a shard.

        :param stream_name: The name of the stream.
        :param shard_id: The shard ID to get the iterator for.
        :param iterator_type: The shard iterator type (e.g. TRIM_HORIZON, LATEST).
        :return: The shard iterator string.
        """
        try:
            response = self.kinesis_client.get_shard_iterator(
                StreamName=stream_name,
                ShardId=shard_id,
                ShardIteratorType=iterator_type,
            )
            shard_iterator = response["ShardIterator"]
            logger.info(
                "Got shard iterator for stream %s, shard %s.",
                stream_name,
                shard_id,
            )
            return shard_iterator
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' or shard '%s' not found. %s",
                    stream_name,
                    shard_id,
                    err.response["Error"]["Message"],
                )
            raise

    def get_records(self, shard_iterator: str, limit: int = 10) -> dict:
        """
        Gets data records from a shard using a shard iterator.

        :param shard_iterator: The shard iterator to use.
        :param limit: Maximum number of records to return.
        :return: The GetRecords response dict.
        """
        try:
            response = self.kinesis_client.get_records(
                ShardIterator=shard_iterator, Limit=limit
            )
            logger.info("Got %d record(s).", len(response.get("Records", list())))
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ExpiredIteratorException":
                logger.error(
                    "Shard iterator expired. Obtain a new shard iterator and retry. %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-start:[python.example_code.kinesis.DescribeStreamSummary]
    def describe_stream_summary(self, stream_name: str) -> dict:
        """
        Gets a summarized description of a Kinesis data stream.

        :param stream_name: The name of the stream.
        :return: The StreamDescriptionSummary dict.
        """
        try:
            response = self.kinesis_client.describe_stream_summary(
                StreamName=stream_name
            )
            summary = response["StreamDescriptionSummary"]
            logger.info("Described stream summary for %s.", stream_name)
            return summary
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' does not exist or was already deleted. %s",
                    stream_name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.kinesis.DescribeStreamSummary]

    # snippet-start:[python.example_code.kinesis.UpdateShardCount]
    def update_shard_count(
        self,
        stream_name: str,
        target_shard_count: int,
        scaling_type: str = "UNIFORM_SCALING",
    ) -> dict:
        """
        Updates the shard count of a provisioned-mode stream.

        :param stream_name: The name of the stream.
        :param target_shard_count: The desired number of shards.
        :param scaling_type: The scaling type (e.g. UNIFORM_SCALING).
        :return: The UpdateShardCount response dict.
        """
        try:
            response = self.kinesis_client.update_shard_count(
                StreamName=stream_name,
                TargetShardCount=target_shard_count,
                ScalingType=scaling_type,
            )
            logger.info(
                "Updating shard count for stream %s: current=%d, target=%d.",
                stream_name,
                response.get("CurrentShardCount", 0),
                response.get("TargetShardCount", 0),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceInUseException":
                logger.error(
                    "Stream '%s' is not in ACTIVE state. "
                    "Wait for ACTIVE and retry. %s",
                    stream_name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.kinesis.UpdateShardCount]

    def delete_stream(self, stream_name: str) -> None:
        """
        Deletes a Kinesis data stream and all its data.

        :param stream_name: The name of the stream to delete.
        """
        try:
            self.kinesis_client.delete_stream(
                StreamName=stream_name, EnforceConsumerDeletion=True
            )
            self.name = None
            self.details = None
            logger.info("Deleted stream %s.", stream_name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' not found — may have already been deleted. %s",
                    stream_name,
                    err.response["Error"]["Message"],
                )
            raise
