# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Data Firehose wrapper class that encapsulates Firehose operations.
"""

import logging
import time
from typing import Any, Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.firehose.FirehoseWrapper.class]
# snippet-start:[python.example_code.firehose.FirehoseWrapper.decl]
class FirehoseWrapper:
    """Encapsulates Amazon Data Firehose operations."""

    def __init__(self, firehose_client):
        """
        :param firehose_client: A Boto3 Amazon Data Firehose client.
        """
        self.firehose_client = firehose_client

    @classmethod
    def from_client(cls):
        """
        Instantiates this class from a Boto3 client.
        """
        firehose_client = boto3.client("firehose")
        return cls(firehose_client)

    # snippet-end:[python.example_code.firehose.FirehoseWrapper.decl]

    # snippet-start:[python.example_code.firehose.CreateDeliveryStream]
    def create_delivery_stream(
        self,
        stream_name: str,
        role_arn: str,
        bucket_arn: str,
        buffer_size_mb: int = 1,
        buffer_interval_seconds: int = 60,
    ) -> str:
        """
        Creates a Firehose delivery stream with an S3 destination.

        :param stream_name: The name of the delivery stream to create.
        :param role_arn: The ARN of the IAM role that Firehose assumes to write to S3.
        :param bucket_arn: The ARN of the S3 bucket destination.
        :param buffer_size_mb: The buffer size in MB before delivery.
        :param buffer_interval_seconds: The buffer interval in seconds before delivery.
        :return: The ARN of the created delivery stream.
        """
        try:
            response = self.firehose_client.create_delivery_stream(
                DeliveryStreamName=stream_name,
                DeliveryStreamType="DirectPut",
                ExtendedS3DestinationConfiguration={
                    "RoleARN": role_arn,
                    "BucketARN": bucket_arn,
                    "BufferingHints": {
                        "SizeInMBs": buffer_size_mb,
                        "IntervalInSeconds": buffer_interval_seconds,
                    },
                },
            )
            stream_arn = response["DeliveryStreamARN"]
            logger.info(
                "Created delivery stream %s with ARN: %s", stream_name, stream_arn
            )
            return stream_arn
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceInUseException":
                logger.error(
                    "A stream with name '%s' already exists. Use a unique name.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.CreateDeliveryStream]

    # snippet-start:[python.example_code.firehose.DescribeDeliveryStream]
    def describe_delivery_stream(self, stream_name: str) -> Dict[str, Any]:
        """
        Describes the specified Firehose delivery stream and its status.

        :param stream_name: The name of the delivery stream to describe.
        :return: A dictionary with the stream description.
        """
        try:
            response = self.firehose_client.describe_delivery_stream(
                DeliveryStreamName=stream_name
            )
            description = response["DeliveryStreamDescription"]
            logger.info(
                "Described stream '%s', status: %s",
                stream_name,
                description.get("DeliveryStreamStatus"),
            )
            return description
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' not found. It may have been deleted.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.DescribeDeliveryStream]

    # snippet-start:[python.example_code.firehose.ListDeliveryStreams]
    def list_delivery_streams(self, stream_type: str = "DirectPut") -> List[str]:
        """
        Lists Firehose delivery streams of the specified type.

        :param stream_type: The delivery stream type to filter by (e.g., 'DirectPut').
        :return: A list of delivery stream names.
        """
        try:
            stream_names = list()
            has_more = True
            exclusive_start = None
            while has_more:
                params = dict()
                params["DeliveryStreamType"] = stream_type
                if exclusive_start is not None:
                    params["ExclusiveStartDeliveryStreamName"] = exclusive_start
                response = self.firehose_client.list_delivery_streams(**params)
                names = response.get("DeliveryStreamNames", list())
                stream_names.extend(names)
                has_more = response.get("HasMoreDeliveryStreams", False)
                if has_more and len(names) > 0:
                    exclusive_start = names[-1]
                else:
                    has_more = False
            logger.info("Listed %d delivery streams.", len(stream_names))
            return stream_names
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidArgumentException":
                logger.error(
                    "Invalid DeliveryStreamType: '%s'. Valid values are 'DirectPut' or 'KinesisStreamAsSource'.",
                    stream_type,
                )
            raise

    # snippet-end:[python.example_code.firehose.ListDeliveryStreams]

    # snippet-start:[python.example_code.firehose.TagDeliveryStream]
    def tag_delivery_stream(self, stream_name: str, tags: List[Dict[str, str]]) -> None:
        """
        Adds or updates tags for the specified Firehose delivery stream.

        :param stream_name: The name of the delivery stream to tag.
        :param tags: A list of tag dicts with 'Key' and 'Value'.
        """
        try:
            self.firehose_client.tag_delivery_stream(
                DeliveryStreamName=stream_name,
                Tags=tags,
            )
            logger.info("Tagged stream '%s' with %d tags.", stream_name, len(tags))
        except ClientError as err:
            if err.response["Error"]["Code"] == "LimitExceededException":
                logger.error(
                    "Tag limit (50 per stream) reached for stream '%s'.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.TagDeliveryStream]

    # snippet-start:[python.example_code.firehose.ListTagsForDeliveryStream]
    def list_tags_for_delivery_stream(self, stream_name: str) -> List[Dict[str, str]]:
        """
        Lists the tags for the specified Firehose delivery stream.

        :param stream_name: The name of the delivery stream.
        :return: A list of tag dicts with 'Key' and 'Value'.
        """
        try:
            response = self.firehose_client.list_tags_for_delivery_stream(
                DeliveryStreamName=stream_name
            )
            tags = response.get("Tags", list())
            logger.info("Found %d tags for stream '%s'.", len(tags), stream_name)
            return tags
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' not found when listing tags.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.ListTagsForDeliveryStream]

    # snippet-start:[python.example_code.firehose.StartDeliveryStreamEncryption]
    def start_delivery_stream_encryption(
        self, stream_name: str, key_type: str = "AWS_OWNED_CMK"
    ) -> None:
        """
        Enables server-side encryption (SSE) for the specified delivery stream.

        :param stream_name: The name of the delivery stream.
        :param key_type: The key type to use for encryption (default: AWS_OWNED_CMK).
        """
        try:
            self.firehose_client.start_delivery_stream_encryption(
                DeliveryStreamName=stream_name,
                DeliveryStreamEncryptionConfigurationInput={
                    "KeyType": key_type,
                },
            )
            logger.info("Started encryption for stream '%s'.", stream_name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidKMSResourceException":
                logger.error(
                    "KMS key is invalid or inaccessible for stream '%s'. "
                    "Verify key permissions.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.StartDeliveryStreamEncryption]

    # snippet-start:[python.example_code.firehose.PutRecord]
    def put_record(self, stream_name: str, data: str) -> Dict[str, Any]:
        """
        Writes a single data record into the specified Firehose delivery stream.

        :param stream_name: The name of the delivery stream.
        :param data: The data string to send (will be encoded to bytes).
        :return: The response containing RecordId and Encrypted status.
        """
        try:
            response = self.firehose_client.put_record(
                DeliveryStreamName=stream_name,
                Record={"Data": data.encode("utf-8")},
            )
            logger.info(
                "Put record to stream '%s'. RecordId: %s",
                stream_name,
                response.get("RecordId"),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ServiceUnavailableException":
                logger.error(
                    "Service unavailable when putting record to stream '%s'. "
                    "Consider retrying with exponential backoff.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.PutRecord]

    # snippet-start:[python.example_code.firehose.PutRecordBatch]
    def put_record_batch(self, stream_name: str, records: List[str]) -> Dict[str, Any]:
        """
        Writes multiple data records into the specified Firehose delivery stream.

        :param stream_name: The name of the delivery stream.
        :param records: A list of data strings to send.
        :return: The response containing FailedPutCount and RequestResponses.
        """
        try:
            record_list = [{"Data": r.encode("utf-8")} for r in records]
            response = self.firehose_client.put_record_batch(
                DeliveryStreamName=stream_name,
                Records=record_list,
            )
            failed_count = response.get("FailedPutCount", 0)
            logger.info(
                "Put batch of %d records to stream '%s'. FailedPutCount: %d",
                len(records),
                stream_name,
                failed_count,
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "ServiceUnavailableException":
                logger.error(
                    "Service unavailable when putting batch to stream '%s'. "
                    "Consider retrying failed records with exponential backoff.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.PutRecordBatch]

    # snippet-start:[python.example_code.firehose.StopDeliveryStreamEncryption]
    def stop_delivery_stream_encryption(self, stream_name: str) -> None:
        """
        Disables server-side encryption for the specified delivery stream.

        :param stream_name: The name of the delivery stream.
        """
        try:
            self.firehose_client.stop_delivery_stream_encryption(
                DeliveryStreamName=stream_name
            )
            logger.info("Stopped encryption for stream '%s'.", stream_name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "LimitExceededException":
                logger.error(
                    "Encryption operation limit (25/day) reached for stream '%s'.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.StopDeliveryStreamEncryption]

    # snippet-start:[python.example_code.firehose.DeleteDeliveryStream]
    def delete_delivery_stream(self, stream_name: str) -> None:
        """
        Deletes the specified Firehose delivery stream and its data.

        :param stream_name: The name of the delivery stream to delete.
        """
        try:
            self.firehose_client.delete_delivery_stream(DeliveryStreamName=stream_name)
            logger.info("Deleted delivery stream '%s'.", stream_name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.info(
                    "Stream '%s' already deleted. Continuing cleanup.",
                    stream_name,
                )
            else:
                raise

    # snippet-end:[python.example_code.firehose.DeleteDeliveryStream]

    def wait_for_stream_active(
        self, stream_name: str, timeout: int = 120, interval: int = 5
    ) -> Dict[str, Any]:
        """
        Polls DescribeDeliveryStream until the stream status is ACTIVE.

        :param stream_name: The name of the delivery stream.
        :param timeout: Maximum seconds to wait.
        :param interval: Seconds between polls.
        :return: The stream description once active.
        :raises TimeoutError: If stream doesn't become active in time.
        """
        elapsed = 0
        while elapsed < timeout:
            description = self.describe_delivery_stream(stream_name)
            status = description.get("DeliveryStreamStatus")
            if status == "ACTIVE":
                return description
            logger.info("Stream '%s' status: %s. Waiting...", stream_name, status)
            time.sleep(interval)
            elapsed += interval
        raise TimeoutError(
            f"Stream '{stream_name}' did not become ACTIVE within {timeout} seconds."
        )

    def wait_for_encryption_status(
        self,
        stream_name: str,
        target_status: str,
        timeout: int = 120,
        interval: int = 5,
    ) -> None:
        """
        Polls DescribeDeliveryStream until the encryption status matches target.

        :param stream_name: The name of the delivery stream.
        :param target_status: The target encryption status (ENABLED or DISABLED).
        :param timeout: Maximum seconds to wait.
        :param interval: Seconds between polls.
        :raises TimeoutError: If target status not reached in time.
        """
        elapsed = 0
        while elapsed < timeout:
            description = self.describe_delivery_stream(stream_name)
            encryption_config = description.get(
                "DeliveryStreamEncryptionConfiguration", dict()
            )
            status = encryption_config.get("Status", "DISABLED")
            if status == target_status:
                logger.info(
                    "Encryption status for '%s' is now %s.", stream_name, target_status
                )
                return
            logger.info(
                "Encryption status for '%s': %s. Waiting for %s...",
                stream_name,
                status,
                target_status,
            )
            time.sleep(interval)
            elapsed += interval
        raise TimeoutError(
            f"Encryption for '{stream_name}' did not reach {target_status} within {timeout}s."
        )


# snippet-end:[python.example_code.firehose.FirehoseWrapper.class]
