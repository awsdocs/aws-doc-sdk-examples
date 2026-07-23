# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Data Firehose wrapper class for managing delivery stream operations.
"""

import logging
import time
from typing import Any, Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.firehose.FirehoseWrapper.decl]
class FirehoseWrapper:
    """Encapsulates Amazon Data Firehose operations."""

    def __init__(self, firehose_client: boto3.client):
        """
        Initializes the FirehoseWrapper with a Boto3 Firehose client.

        :param firehose_client: A Boto3 Amazon Data Firehose client.
        """
        self.firehose_client = firehose_client

    @classmethod
    def from_client(cls):
        """Creates a FirehoseWrapper instance with a default Boto3 Firehose client."""
        firehose_client = boto3.client("firehose")
        return cls(firehose_client)

    # snippet-end:[python.example_code.firehose.FirehoseWrapper.decl]

    # snippet-start:[python.example_code.firehose.CreateDeliveryStream]
    def create_delivery_stream(
        self,
        stream_name: str,
        role_arn: str,
        bucket_arn: str,
        prefix: str = "firehose-output/",
        error_prefix: str = "firehose-errors/",
        buffer_size_mb: int = 1,
        buffer_interval_seconds: int = 60,
    ) -> str:
        """
        Creates a Firehose delivery stream with an S3 destination.

        :param stream_name: The name for the delivery stream.
        :param role_arn: The ARN of the IAM role for Firehose to assume.
        :param bucket_arn: The ARN of the destination S3 bucket.
        :param prefix: The prefix for delivered objects in S3.
        :param error_prefix: The prefix for error output in S3.
        :param buffer_size_mb: The buffer size in MB before delivery.
        :param buffer_interval_seconds: The buffer interval in seconds.
        :return: The ARN of the created delivery stream.
        :raises ClientError: If the stream already exists (ResourceInUseException).
        """
        try:
            response = self.firehose_client.create_delivery_stream(
                DeliveryStreamName=stream_name,
                DeliveryStreamType="DirectPut",
                ExtendedS3DestinationConfiguration={
                    "RoleARN": role_arn,
                    "BucketARN": bucket_arn,
                    "Prefix": prefix,
                    "ErrorOutputPrefix": error_prefix,
                    "BufferingHints": {
                        "SizeInMBs": buffer_size_mb,
                        "IntervalInSeconds": buffer_interval_seconds,
                    },
                    "CompressionFormat": "UNCOMPRESSED",
                },
            )
            stream_arn = response["DeliveryStreamARN"]
            logger.info("Created delivery stream '%s' with ARN: %s", stream_name, stream_arn)
            return stream_arn
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceInUseException":
                logger.error(
                    "A delivery stream with name '%s' already exists. "
                    "Use a different name or delete the existing stream.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.CreateDeliveryStream]

    # snippet-start:[python.example_code.firehose.DescribeDeliveryStream]
    def describe_delivery_stream(self, stream_name: str) -> Dict[str, Any]:
        """
        Describes a Firehose delivery stream and returns its details.

        :param stream_name: The name of the delivery stream to describe.
        :return: A dictionary containing the delivery stream description.
        :raises ClientError: If the stream does not exist (ResourceNotFoundException).
        """
        try:
            response = self.firehose_client.describe_delivery_stream(
                DeliveryStreamName=stream_name
            )
            stream_description = response["DeliveryStreamDescription"]
            logger.info("Described delivery stream '%s'.", stream_name)
            return stream_description
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error("Delivery stream '%s' does not exist.", stream_name)
            raise

    # snippet-end:[python.example_code.firehose.DescribeDeliveryStream]

    # snippet-start:[python.example_code.firehose.ListDeliveryStreams]
    def list_delivery_streams(
        self,
        stream_type: str = "DirectPut",
        limit: int = 10,
    ) -> Dict[str, Any]:
        """
        Lists Firehose delivery streams of a given type.

        :param stream_type: The type of delivery streams to list (e.g., 'DirectPut').
        :param limit: The maximum number of streams to return.
        :return: A dictionary containing stream names and pagination info.
        :raises ClientError: If an invalid argument is provided (InvalidArgumentException).
        """
        try:
            response = self.firehose_client.list_delivery_streams(
                DeliveryStreamType=stream_type,
                Limit=limit,
            )
            stream_names = response.get("DeliveryStreamNames", list())
            has_more = response.get("HasMoreDeliveryStreams", False)
            logger.info(
                "Listed %d delivery stream(s). Has more: %s",
                len(stream_names),
                has_more,
            )
            return {"DeliveryStreamNames": stream_names, "HasMoreDeliveryStreams": has_more}
        except ClientError as error:
            if error.response["Error"]["Code"] == "InvalidArgumentException":
                logger.error(
                    "Invalid argument for ListDeliveryStreams. "
                    "Verify DeliveryStreamType is valid."
                )
            raise

    # snippet-end:[python.example_code.firehose.ListDeliveryStreams]

    # snippet-start:[python.example_code.firehose.TagDeliveryStream]
    def tag_delivery_stream(self, stream_name: str, tags: List[Dict[str, str]]) -> None:
        """
        Adds or updates tags on a Firehose delivery stream.

        :param stream_name: The name of the delivery stream to tag.
        :param tags: A list of tag dictionaries with 'Key' and 'Value' entries.
        :raises ClientError: If the stream does not exist (ResourceNotFoundException).
        """
        try:
            self.firehose_client.tag_delivery_stream(
                DeliveryStreamName=stream_name,
                Tags=tags,
            )
            logger.info("Applied %d tag(s) to stream '%s'.", len(tags), stream_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Cannot tag stream '%s' because it does not exist.", stream_name
                )
            raise

    # snippet-end:[python.example_code.firehose.TagDeliveryStream]

    # snippet-start:[python.example_code.firehose.ListTagsForDeliveryStream]
    def list_tags_for_delivery_stream(
        self, stream_name: str
    ) -> Dict[str, Any]:
        """
        Lists all tags for a Firehose delivery stream.

        :param stream_name: The name of the delivery stream.
        :return: A dictionary containing the list of tags and HasMoreTags flag.
        :raises ClientError: If the stream does not exist (ResourceNotFoundException).
        """
        try:
            response = self.firehose_client.list_tags_for_delivery_stream(
                DeliveryStreamName=stream_name
            )
            tags = response.get("Tags", list())
            has_more_tags = response.get("HasMoreTags", False)
            logger.info(
                "Listed %d tag(s) for stream '%s'. Has more: %s",
                len(tags),
                stream_name,
                has_more_tags,
            )
            return {"Tags": tags, "HasMoreTags": has_more_tags}
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Cannot list tags for stream '%s' because it does not exist.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.ListTagsForDeliveryStream]

    # snippet-start:[python.example_code.firehose.UntagDeliveryStream]
    def untag_delivery_stream(self, stream_name: str, tag_keys: List[str]) -> None:
        """
        Removes tags from a Firehose delivery stream.

        :param stream_name: The name of the delivery stream to untag.
        :param tag_keys: A list of tag keys to remove.
        :raises ClientError: If the stream does not exist (ResourceNotFoundException).
        """
        try:
            self.firehose_client.untag_delivery_stream(
                DeliveryStreamName=stream_name,
                TagKeys=tag_keys,
            )
            logger.info("Removed tag(s) %s from stream '%s'.", tag_keys, stream_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Cannot untag stream '%s' because it does not exist.", stream_name
                )
            raise

    # snippet-end:[python.example_code.firehose.UntagDeliveryStream]

    # snippet-start:[python.example_code.firehose.StartDeliveryStreamEncryption]
    def start_delivery_stream_encryption(
        self, stream_name: str, key_type: str = "AWS_OWNED_CMK"
    ) -> None:
        """
        Enables server-side encryption on a Firehose delivery stream.

        :param stream_name: The name of the delivery stream.
        :param key_type: The type of encryption key (e.g., 'AWS_OWNED_CMK').
        :raises ClientError: If there is a KMS issue (InvalidKMSResourceException).
        """
        try:
            self.firehose_client.start_delivery_stream_encryption(
                DeliveryStreamName=stream_name,
                DeliveryStreamEncryptionConfigurationInput={"KeyType": key_type},
            )
            logger.info(
                "Started encryption on stream '%s' with key type '%s'.",
                stream_name,
                key_type,
            )
        except ClientError as error:
            if error.response["Error"]["Code"] == "InvalidKMSResourceException":
                logger.error(
                    "KMS configuration issue for stream '%s'. "
                    "Verify KMS key permissions and state.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.StartDeliveryStreamEncryption]

    # snippet-start:[python.example_code.firehose.StopDeliveryStreamEncryption]
    def stop_delivery_stream_encryption(self, stream_name: str) -> None:
        """
        Disables server-side encryption on a Firehose delivery stream.

        :param stream_name: The name of the delivery stream.
        :raises ClientError: If the rate limit is exceeded (LimitExceededException).
        """
        try:
            self.firehose_client.stop_delivery_stream_encryption(
                DeliveryStreamName=stream_name
            )
            logger.info("Stopped encryption on stream '%s'.", stream_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "LimitExceededException":
                logger.error(
                    "Rate limit exceeded for encryption operations on stream '%s'. "
                    "The combined start/stop limit is 25 per 24 hours. Wait and retry.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.StopDeliveryStreamEncryption]

    # snippet-start:[python.example_code.firehose.UpdateDestination]
    def update_destination(
        self,
        stream_name: str,
        version_id: str,
        destination_id: str,
        prefix: str,
        buffer_size_mb: int = 5,
        buffer_interval_seconds: int = 300,
        compression_format: str = "GZIP",
    ) -> None:
        """
        Updates the destination configuration of a Firehose delivery stream.

        :param stream_name: The name of the delivery stream.
        :param version_id: The current version ID of the stream (for concurrency).
        :param destination_id: The destination ID to update.
        :param prefix: The new S3 prefix for delivered objects.
        :param buffer_size_mb: The new buffer size in MB.
        :param buffer_interval_seconds: The new buffer interval in seconds.
        :param compression_format: The new compression format.
        :raises ClientError: If a concurrent modification occurs (ConcurrentModificationException).
        """
        try:
            self.firehose_client.update_destination(
                DeliveryStreamName=stream_name,
                CurrentDeliveryStreamVersionId=version_id,
                DestinationId=destination_id,
                ExtendedS3DestinationUpdate={
                    "Prefix": prefix,
                    "BufferingHints": {
                        "SizeInMBs": buffer_size_mb,
                        "IntervalInSeconds": buffer_interval_seconds,
                    },
                    "CompressionFormat": compression_format,
                },
            )
            logger.info(
                "Updated destination for stream '%s': prefix='%s', "
                "buffer=%dMB/%ds, compression='%s'.",
                stream_name,
                prefix,
                buffer_size_mb,
                buffer_interval_seconds,
                compression_format,
            )
        except ClientError as error:
            if error.response["Error"]["Code"] == "ConcurrentModificationException":
                logger.error(
                    "Concurrent modification on stream '%s'. "
                    "Fetch a fresh VersionId and retry.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.UpdateDestination]

    # snippet-start:[python.example_code.firehose.DeleteDeliveryStream]
    def delete_delivery_stream(
        self, stream_name: str, allow_force_delete: bool = False
    ) -> None:
        """
        Deletes a Firehose delivery stream.

        :param stream_name: The name of the delivery stream to delete.
        :param allow_force_delete: Whether to force deletion if the stream is in use.
        :raises ClientError: If the stream does not exist (ResourceNotFoundException).
        """
        try:
            self.firehose_client.delete_delivery_stream(
                DeliveryStreamName=stream_name,
                AllowForceDelete=allow_force_delete,
            )
            logger.info("Initiated deletion of delivery stream '%s'.", stream_name)
        except ClientError as error:
            if error.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Stream '%s' not found. It may already be deleted.", stream_name
                )
            raise

    # snippet-end:[python.example_code.firehose.DeleteDeliveryStream]

    def wait_for_stream_active(
        self, stream_name: str, max_wait_seconds: int = 60, poll_interval: int = 5
    ) -> Dict[str, Any]:
        """
        Polls DescribeDeliveryStream until the stream status is ACTIVE.

        :param stream_name: The name of the delivery stream.
        :param max_wait_seconds: Maximum time to wait for the stream to become active.
        :param poll_interval: Time between polls in seconds.
        :return: The stream description once active.
        :raises TimeoutError: If the stream does not become active in time.
        :raises RuntimeError: If the stream enters CREATING_FAILED status.
        """
        elapsed = 0
        while elapsed < max_wait_seconds:
            description = self.describe_delivery_stream(stream_name)
            status = description.get("DeliveryStreamStatus", "UNKNOWN")
            logger.info("Stream '%s' status: %s", stream_name, status)
            if status == "ACTIVE":
                return description
            if status == "CREATING_FAILED":
                raise RuntimeError(
                    f"Delivery stream '{stream_name}' creation failed."
                )
            time.sleep(poll_interval)
            elapsed += poll_interval
        raise TimeoutError(
            f"Stream '{stream_name}' did not become ACTIVE within {max_wait_seconds}s."
        )

    def wait_for_encryption_status(
        self,
        stream_name: str,
        target_status: str,
        max_wait_seconds: int = 60,
        poll_interval: int = 5,
    ) -> str:
        """
        Polls DescribeDeliveryStream until encryption status matches target.

        :param stream_name: The name of the delivery stream.
        :param target_status: The desired encryption status (e.g., 'ENABLED', 'DISABLED').
        :param max_wait_seconds: Maximum time to wait.
        :param poll_interval: Time between polls in seconds.
        :return: The final encryption status.
        """
        elapsed = 0
        current_status = "UNKNOWN"
        while elapsed < max_wait_seconds:
            description = self.describe_delivery_stream(stream_name)
            encryption_config = description.get(
                "DeliveryStreamEncryptionConfiguration", dict()
            )
            current_status = encryption_config.get("Status", "UNKNOWN")
            logger.info(
                "Stream '%s' encryption status: %s", stream_name, current_status
            )
            if current_status == target_status:
                return current_status
            time.sleep(poll_interval)
            elapsed += poll_interval
        logger.warning(
            "Encryption status for '%s' did not reach '%s' within %ds.",
            stream_name,
            target_status,
            max_wait_seconds,
        )
        return current_status

    def wait_for_stream_deleted(
        self, stream_name: str, max_wait_seconds: int = 60, poll_interval: int = 5
    ) -> bool:
        """
        Polls DescribeDeliveryStream until a ResourceNotFoundException confirms deletion.

        :param stream_name: The name of the delivery stream.
        :param max_wait_seconds: Maximum time to wait.
        :param poll_interval: Time between polls in seconds.
        :return: True if the stream is confirmed deleted.
        """
        elapsed = 0
        while elapsed < max_wait_seconds:
            try:
                description = self.describe_delivery_stream(stream_name)
                status = description.get("DeliveryStreamStatus", "UNKNOWN")
                logger.info("Stream '%s' status: %s (waiting for deletion)", stream_name, status)
            except ClientError as error:
                if error.response["Error"]["Code"] == "ResourceNotFoundException":
                    logger.info("Stream '%s' confirmed deleted.", stream_name)
                    return True
                raise
            time.sleep(poll_interval)
            elapsed += poll_interval
        logger.warning(
            "Stream '%s' was not confirmed deleted within %ds.", stream_name, max_wait_seconds
        )
        return False
