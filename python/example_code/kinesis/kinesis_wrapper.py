# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Kinesis Data Streams wrapper class for managing stream operations.
"""

import json
import logging
from typing import Any, Dict, List

import boto3
from botocore.exceptions import ClientError, WaiterError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kinesis.KinesisWrapper.decl]
class KinesisWrapper:
    """Encapsulates Amazon Kinesis Data Streams operations."""

    def __init__(self, kinesis_client):
        """
        :param kinesis_client: A Boto3 Kinesis client.
        """
        self.kinesis_client = kinesis_client
        self.stream_name = None

    @classmethod
    def from_client(cls):
        """
        Creates a KinesisWrapper instance with a default Boto3 Kinesis client.
        """
        kinesis_client = boto3.client("kinesis")
        return cls(kinesis_client)

    # snippet-end:[python.example_code.kinesis.KinesisWrapper.decl]

    # snippet-start:[python.example_code.kinesis.CreateStream]
    def create_stream(self, stream_name: str) -> None:
        """
        Creates a Kinesis data stream with on-demand capacity mode.

        :param stream_name: The name for the new stream.
        :raises ClientError: If the stream already exists or another error occurs.
        """
        try:
            self.kinesis_client.create_stream(
                StreamName=stream_name,
                StreamModeDetails={"StreamMode": "ON_DEMAND"},
            )
            self.stream_name = stream_name
            logger.info("Created stream %s.", stream_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceInUseException":
                logger.error(
                    "Stream %s already exists. Use a different name.", stream_name
                )
            raise

    # snippet-end:[python.example_code.kinesis.CreateStream]

    # snippet-start:[python.example_code.kinesis.DescribeStream]
    def describe_stream(self, stream_name: str) -> Dict[str, Any]:
        """
        Gets metadata about a Kinesis stream, including its status and ARN.

        :param stream_name: The name of the stream to describe.
        :return: A dictionary containing the stream description.
        :raises ClientError: If the stream does not exist.
        """
        try:
            response = self.kinesis_client.describe_stream(StreamName=stream_name)
            stream_description = response["StreamDescription"]
            logger.info(
                "Stream %s has status %s.",
                stream_name,
                stream_description["StreamStatus"],
            )
            return stream_description
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Stream %s not found.", stream_name)
            raise

    # snippet-end:[python.example_code.kinesis.DescribeStream]

    # snippet-start:[python.example_code.kinesis.PutRecord]
    def put_record(
        self, stream_name: str, data: Dict[str, Any], partition_key: str
    ) -> Dict[str, str]:
        """
        Puts a single data record into a Kinesis stream.

        :param stream_name: The name of the stream.
        :param data: The data to put into the stream (will be JSON-encoded).
        :param partition_key: The partition key for shard placement.
        :return: A dictionary with ShardId and SequenceNumber.
        :raises ClientError: If throughput is exceeded.
        """
        try:
            response = self.kinesis_client.put_record(
                StreamName=stream_name,
                Data=json.dumps(data),
                PartitionKey=partition_key,
            )
            logger.info(
                "Put record to shard %s with sequence number %s.",
                response["ShardId"],
                response["SequenceNumber"],
            )
            return {
                "ShardId": response["ShardId"],
                "SequenceNumber": response["SequenceNumber"],
            }
        except ClientError as error:
            if (
                error.response["Error"]["Code"]
                == "ProvisionedThroughputExceededException"
            ):
                logger.error(
                    "Throughput exceeded for stream %s. Implement backoff.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.kinesis.PutRecord]

    # snippet-start:[python.example_code.kinesis.PutRecords]
    def put_records(
        self, stream_name: str, records: List[Dict[str, Any]]
    ) -> Dict[str, Any]:
        """
        Puts multiple data records into a Kinesis stream in a single batch.

        :param stream_name: The name of the stream.
        :param records: A list of dicts, each with 'Data' (dict) and 'PartitionKey' (str).
        :return: A dictionary with FailedRecordCount and Records results.
        :raises ClientError: If throughput is exceeded.
        """
        try:
            kinesis_records = list()
            for record in records:
                kinesis_records.append(
                    {
                        "Data": json.dumps(record["Data"]),
                        "PartitionKey": record["PartitionKey"],
                    }
                )
            response = self.kinesis_client.put_records(
                StreamName=stream_name, Records=kinesis_records
            )
            failed_count = response.get("FailedRecordCount", 0)
            logger.info(
                "Put %d records to stream %s. Failed: %d.",
                len(records),
                stream_name,
                failed_count,
            )
            return {
                "FailedRecordCount": failed_count,
                "Records": response["Records"],
            }
        except ClientError as error:
            if (
                error.response["Error"]["Code"]
                == "ProvisionedThroughputExceededException"
            ):
                logger.error(
                    "Throughput exceeded for stream %s. Retry failed records.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.kinesis.PutRecords]

    # snippet-start:[python.example_code.kinesis.ListShards]
    def list_shards(self, stream_name: str) -> List[Dict[str, Any]]:
        """
        Lists all shards in a Kinesis stream.

        :param stream_name: The name of the stream.
        :return: A list of shard dictionaries.
        :raises ClientError: If the stream does not exist.
        """
        try:
            shards = list()
            response = self.kinesis_client.list_shards(StreamName=stream_name)
            shards.extend(response.get("Shards", list()))
            # Handle pagination
            while response.get("NextToken"):
                response = self.kinesis_client.list_shards(
                    NextToken=response["NextToken"]
                )
                shards.extend(response.get("Shards", list()))
            logger.info("Found %d shards in stream %s.", len(shards), stream_name)
            return shards
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Stream %s not found.", stream_name)
            raise

    # snippet-end:[python.example_code.kinesis.ListShards]

    # snippet-start:[python.example_code.kinesis.GetShardIterator]
    def get_shard_iterator(
        self, stream_name: str, shard_id: str, iterator_type: str = "TRIM_HORIZON"
    ) -> str:
        """
        Gets a shard iterator for reading records from a shard.

        :param stream_name: The name of the stream.
        :param shard_id: The ID of the shard.
        :param iterator_type: The type of iterator (e.g., TRIM_HORIZON, LATEST).
        :return: The shard iterator string.
        :raises ClientError: If the shard ID or iterator type is invalid.
        """
        try:
            response = self.kinesis_client.get_shard_iterator(
                StreamName=stream_name,
                ShardId=shard_id,
                ShardIteratorType=iterator_type,
            )
            shard_iterator = response["ShardIterator"]
            logger.info("Got shard iterator for shard %s.", shard_id)
            return shard_iterator
        except ClientError as error:
            if error.response["Error"]["Code"] == "InvalidArgumentException":
                logger.error(
                    "Invalid shard ID %s or iterator type %s.",
                    shard_id,
                    iterator_type,
                )
            raise

    # snippet-end:[python.example_code.kinesis.GetShardIterator]

    # snippet-start:[python.example_code.kinesis.GetRecords]
    def get_records(self, shard_iterator: str, limit: int = 25) -> Dict[str, Any]:
        """
        Gets data records from a shard using a shard iterator.

        :param shard_iterator: The shard iterator to use.
        :param limit: The maximum number of records to return.
        :return: A dictionary with Records, NextShardIterator, and MillisBehindLatest.
        :raises ClientError: If the shard iterator has expired.
        """
        try:
            response = self.kinesis_client.get_records(
                ShardIterator=shard_iterator, Limit=limit
            )
            records = response.get("Records", list())
            logger.info("Got %d records from shard.", len(records))
            return {
                "Records": records,
                "NextShardIterator": response.get("NextShardIterator"),
                "MillisBehindLatest": response.get("MillisBehindLatest", 0),
            }
        except ClientError as error:
            if error.response["Error"]["Code"] == "ExpiredIteratorException":
                logger.error("Shard iterator expired. Get a new one.")
            raise

    # snippet-end:[python.example_code.kinesis.GetRecords]

    # snippet-start:[python.example_code.kinesis.AddTagsToStream]
    def add_tags_to_stream(self, stream_name: str, tags: Dict[str, str]) -> None:
        """
        Adds tags to a Kinesis stream for metadata and cost allocation.

        :param stream_name: The name of the stream.
        :param tags: A dictionary of tag key-value pairs.
        :raises ClientError: If the stream does not exist.
        """
        try:
            self.kinesis_client.add_tags_to_stream(StreamName=stream_name, Tags=tags)
            logger.info("Added %d tags to stream %s.", len(tags), stream_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Stream %s not found. Cannot add tags.", stream_name)
            raise

    # snippet-end:[python.example_code.kinesis.AddTagsToStream]

    # snippet-start:[python.example_code.kinesis.ListTagsForStream]
    def list_tags_for_stream(self, stream_name: str) -> List[Dict[str, str]]:
        """
        Lists all tags on a Kinesis stream.

        :param stream_name: The name of the stream.
        :return: A list of tag dictionaries with Key and Value.
        :raises ClientError: If the stream does not exist.
        """
        try:
            response = self.kinesis_client.list_tags_for_stream(StreamName=stream_name)
            tags = response.get("Tags", list())
            logger.info("Found %d tags on stream %s.", len(tags), stream_name)
            return tags
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Stream %s not found. Cannot list tags.", stream_name)
            raise

    # snippet-end:[python.example_code.kinesis.ListTagsForStream]

    # snippet-start:[python.example_code.kinesis.DeleteStream]
    def delete_stream(self, stream_name: str) -> None:
        """
        Deletes a Kinesis stream and all its data.

        :param stream_name: The name of the stream to delete.
        :raises ClientError: If the stream does not exist or was already deleted.
        """
        try:
            self.kinesis_client.delete_stream(
                StreamName=stream_name, EnforceConsumerDeletion=True
            )
            logger.info("Deleted stream %s.", stream_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream %s not found or already deleted.", stream_name
                )
            raise

    # snippet-end:[python.example_code.kinesis.DeleteStream]

    def wait_for_stream_active(
        self, stream_name: str, timeout: int = 180, interval: int = 5
    ) -> Dict[str, Any]:
        """
        Uses the stream_exists waiter to wait until the stream is ACTIVE,
        then returns the stream description.

        :param stream_name: The name of the stream.
        :param timeout: Maximum time to wait in seconds.
        :param interval: Time between polls in seconds.
        :return: The stream description when ACTIVE.
        :raises TimeoutError: If the stream does not become active within timeout.
        """
        try:
            waiter = self.kinesis_client.get_waiter("stream_exists")
            max_attempts = max(timeout // interval, 1)
            waiter.wait(
                StreamName=stream_name,
                WaiterConfig={"Delay": interval, "MaxAttempts": max_attempts},
            )
        except WaiterError as err:
            raise TimeoutError(
                f"Stream {stream_name} did not become ACTIVE within {timeout}s: {err}"
            )
        # Stream is now ACTIVE; return the description
        description = self.describe_stream(stream_name)
        return description
