# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose: Wrapper class for Amazon Data Firehose operations.
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

    def __init__(self, firehose_client: boto3.client):
        """
        :param firehose_client: A Boto3 Amazon Data Firehose client.
        """
        self.firehose_client = firehose_client

    @classmethod
    def from_client(cls, region_name: str = "us-east-1"):
        """
        Creates a FirehoseWrapper instance with a default Boto3 Firehose client.

        :param region_name: AWS region for the Firehose client.
        """
        firehose_client = boto3.client("firehose", region_name=region_name)
        return cls(firehose_client)

    # snippet-end:[python.example_code.firehose.FirehoseWrapper.decl]

    # snippet-start:[python.example_code.firehose.CreateDeliveryStream]
    def create_delivery_stream(
        self,
        stream_name: str,
        bucket_arn: str,
        role_arn: str,
        interval_in_seconds: int = 60,
        size_in_mbs: int = 1,
    ) -> str:
        """
        Creates a Firehose delivery stream with an S3 destination.

        :param stream_name: The name of the delivery stream to create.
        :param bucket_arn: The ARN of the S3 bucket destination.
        :param role_arn: The ARN of the IAM role that allows Firehose to write to S3.
        :param interval_in_seconds: Buffer interval in seconds before delivery.
        :param size_in_mbs: Buffer size in MB before delivery.
        :return: The ARN of the created delivery stream.
        """
        try:
            response = self.firehose_client.create_delivery_stream(
                DeliveryStreamName=stream_name,
                DeliveryStreamType="DirectPut",
                ExtendedS3DestinationConfiguration={
                    "BucketARN": bucket_arn,
                    "RoleARN": role_arn,
                    "BufferingHints": {
                        "IntervalInSeconds": interval_in_seconds,
                        "SizeInMBs": size_in_mbs,
                    },
                },
            )
            stream_arn = response["DeliveryStreamARN"]
            logger.info(
                "Created delivery stream %s with ARN %s.", stream_name, stream_arn
            )
            return stream_arn
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceInUseException":
                logger.error(
                    "A delivery stream with name '%s' already exists. "
                    "Use a different name.",
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
        """
        try:
            response = self.firehose_client.describe_delivery_stream(
                DeliveryStreamName=stream_name
            )
            description = response.get("DeliveryStreamDescription", dict())
            logger.info(
                "Described delivery stream %s, status: %s.",
                stream_name,
                description.get("DeliveryStreamStatus"),
            )
            return description
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Delivery stream '%s' not found. Verify the stream name is correct.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.DescribeDeliveryStream]

    # snippet-start:[python.example_code.firehose.TagDeliveryStream]
    def tag_delivery_stream(self, stream_name: str, tags: List[Dict[str, str]]) -> None:
        """
        Adds tags to a Firehose delivery stream.

        :param stream_name: The name of the delivery stream to tag.
        :param tags: A list of tag dictionaries with 'Key' and 'Value' entries.
        """
        try:
            self.firehose_client.tag_delivery_stream(
                DeliveryStreamName=stream_name,
                Tags=tags,
            )
            logger.info(
                "Added %d tag(s) to delivery stream %s.", len(tags), stream_name
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "LimitExceededException":
                logger.error(
                    "Cannot add tags to '%s'. The maximum number of tags (50) "
                    "has been reached.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.TagDeliveryStream]

    # snippet-start:[python.example_code.firehose.ListTagsForDeliveryStream]
    def list_tags_for_delivery_stream(self, stream_name: str) -> List[Dict[str, str]]:
        """
        Lists the tags for a Firehose delivery stream.

        :param stream_name: The name of the delivery stream.
        :return: A list of tag dictionaries with 'Key' and 'Value' entries.
        """
        try:
            response = self.firehose_client.list_tags_for_delivery_stream(
                DeliveryStreamName=stream_name
            )
            tags = response.get("Tags", list())
            has_more = response.get("HasMoreTags", False)
            if has_more:
                logger.info(
                    "There are additional tags not shown for stream %s.", stream_name
                )
            logger.info(
                "Retrieved %d tag(s) for delivery stream %s.", len(tags), stream_name
            )
            return tags
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceNotFoundException":
                logger.error(
                    "Delivery stream '%s' not found. Cannot list tags.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.ListTagsForDeliveryStream]

    # snippet-start:[python.example_code.firehose.StartDeliveryStreamEncryption]
    def start_delivery_stream_encryption(self, stream_name: str) -> None:
        """
        Enables server-side encryption (SSE) on a Firehose delivery stream using
        an AWS-owned CMK.

        :param stream_name: The name of the delivery stream to encrypt.
        """
        try:
            self.firehose_client.start_delivery_stream_encryption(
                DeliveryStreamName=stream_name,
                DeliveryStreamEncryptionConfigurationInput={"KeyType": "AWS_OWNED_CMK"},
            )
            logger.info("Started encryption for delivery stream %s.", stream_name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidKMSResourceException":
                logger.error(
                    "The KMS key is inaccessible for stream '%s'. "
                    "Verify key policy and permissions.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.StartDeliveryStreamEncryption]

    # snippet-start:[python.example_code.firehose.PutRecord]
    def put_record(self, stream_name: str, record_data: str) -> Dict[str, Any]:
        """
        Puts a single record into a Firehose delivery stream.

        :param stream_name: The name of the delivery stream.
        :param record_data: The data string for the record (will be encoded to bytes).
        :return: A dictionary with the record ID and encryption status.
        """
        try:
            response = self.firehose_client.put_record(
                DeliveryStreamName=stream_name,
                Record={"Data": record_data.encode("utf-8")},
            )
            result = {
                "RecordId": response.get("RecordId"),
                "Encrypted": response.get("Encrypted", False),
            }
            logger.info(
                "Put record to stream %s. RecordId: %s, Encrypted: %s.",
                stream_name,
                result["RecordId"],
                result["Encrypted"],
            )
            return result
        except ClientError as err:
            if err.response["Error"]["Code"] == "ServiceUnavailableException":
                logger.error(
                    "Service unavailable while putting record to '%s'. "
                    "Retry with exponential backoff.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.PutRecord]

    # snippet-start:[python.example_code.firehose.PutRecordBatch]
    def put_record_batch(self, stream_name: str, records: List[str]) -> Dict[str, Any]:
        """
        Puts multiple records into a Firehose delivery stream in a single batch.

        :param stream_name: The name of the delivery stream.
        :param records: A list of data strings to send as records.
        :return: A dictionary with counts of successful/failed records and details.
        """
        try:
            record_entries = [{"Data": r.encode("utf-8")} for r in records]
            response = self.firehose_client.put_record_batch(
                DeliveryStreamName=stream_name,
                Records=record_entries,
            )
            failed_count = response.get("FailedPutCount", 0)
            request_responses = response.get("RequestResponses", list())
            successful = list()
            failed = list()
            for i, resp in enumerate(request_responses):
                if resp.get("ErrorCode"):
                    failed.append(
                        {
                            "index": i,
                            "ErrorCode": resp["ErrorCode"],
                            "ErrorMessage": resp.get("ErrorMessage", ""),
                        }
                    )
                else:
                    successful.append(resp.get("RecordId"))
            result = {
                "FailedPutCount": failed_count,
                "SuccessCount": len(successful),
                "SuccessfulRecordIds": successful,
                "FailedRecords": failed,
            }
            logger.info(
                "Put batch of %d records to stream %s. Success: %d, Failed: %d.",
                len(records),
                stream_name,
                len(successful),
                failed_count,
            )
            return result
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidArgumentException":
                logger.error(
                    "Invalid argument for PutRecordBatch to '%s'. "
                    "Validate: max 500 records, max 1000 KB per record, max 4 MB total.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.PutRecordBatch]

    # snippet-start:[python.example_code.firehose.UpdateDestination]
    def update_destination(
        self,
        stream_name: str,
        interval_in_seconds: int = 120,
        size_in_mbs: int = 5,
    ) -> None:
        """
        Updates the destination configuration of a Firehose delivery stream.

        :param stream_name: The name of the delivery stream.
        :param interval_in_seconds: New buffer interval in seconds.
        :param size_in_mbs: New buffer size in MB.
        """
        try:
            # Get the current version ID and destination ID
            description = self.describe_delivery_stream(stream_name)
            version_id = description.get("VersionId")
            destinations = description.get("Destinations", list())
            if not destinations:
                raise ValueError(f"No destinations found for stream '{stream_name}'.")
            destination_id = destinations[0].get("DestinationId")

            self.firehose_client.update_destination(
                DeliveryStreamName=stream_name,
                CurrentDeliveryStreamVersionId=version_id,
                DestinationId=destination_id,
                ExtendedS3DestinationUpdate={
                    "BufferingHints": {
                        "IntervalInSeconds": interval_in_seconds,
                        "SizeInMBs": size_in_mbs,
                    }
                },
            )
            logger.info(
                "Updated destination for stream %s. " "BufferingHints: %ds / %d MB.",
                stream_name,
                interval_in_seconds,
                size_in_mbs,
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "ConcurrentModificationException":
                logger.error(
                    "Concurrent modification detected for stream '%s'. "
                    "Fetch the latest VersionId and retry.",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.UpdateDestination]

    # snippet-start:[python.example_code.firehose.ListDeliveryStreams]
    def list_delivery_streams(
        self,
        stream_type: Optional[str] = None,
        limit: int = 20,
    ) -> List[str]:
        """
        Lists Firehose delivery streams in the account.

        :param stream_type: Optional filter by delivery stream type (e.g., 'DirectPut').
        :param limit: Maximum number of stream names to return per call.
        :return: A list of delivery stream names.
        """
        try:
            params = dict()
            params["Limit"] = limit
            if stream_type is not None:
                params["DeliveryStreamType"] = stream_type

            all_stream_names = list()
            has_more = True
            while has_more:
                response = self.firehose_client.list_delivery_streams(**params)
                stream_names = response.get("DeliveryStreamNames", list())
                all_stream_names.extend(stream_names)
                has_more = response.get("HasMoreDeliveryStreams", False)
                if has_more and stream_names:
                    params["ExclusiveStartDeliveryStreamName"] = stream_names[-1]

            logger.info("Listed %d delivery stream(s).", len(all_stream_names))
            return all_stream_names
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidArgumentException":
                logger.error(
                    "Invalid argument for ListDeliveryStreams. "
                    "Validate DeliveryStreamType and Limit parameters."
                )
            raise

    # snippet-end:[python.example_code.firehose.ListDeliveryStreams]

    # snippet-start:[python.example_code.firehose.DeleteDeliveryStream]
    def delete_delivery_stream(
        self, stream_name: str, allow_force_delete: bool = True
    ) -> None:
        """
        Deletes a Firehose delivery stream.

        :param stream_name: The name of the delivery stream to delete.
        :param allow_force_delete: If True, forces deletion even if there are issues.
        """
        try:
            self.firehose_client.delete_delivery_stream(
                DeliveryStreamName=stream_name,
                AllowForceDelete=allow_force_delete,
            )
            logger.info("Initiated deletion of delivery stream %s.", stream_name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "ResourceInUseException":
                logger.error(
                    "Cannot delete stream '%s'. Wait for it to reach a "
                    "deletable state (ACTIVE, CREATING_FAILED, or DELETING_FAILED).",
                    stream_name,
                )
            raise

    # snippet-end:[python.example_code.firehose.DeleteDeliveryStream]

    def wait_for_stream_active(
        self, stream_name: str, poll_interval: int = 5, max_wait: int = 300
    ) -> Dict[str, Any]:
        """
        Polls DescribeDeliveryStream until the stream status is ACTIVE.

        :param stream_name: The name of the delivery stream.
        :param poll_interval: Seconds between polling attempts.
        :param max_wait: Maximum total seconds to wait.
        :return: The stream description once active.
        :raises TimeoutError: If the stream does not become active within max_wait.
        """
        elapsed = 0
        while elapsed < max_wait:
            description = self.describe_delivery_stream(stream_name)
            status = description.get("DeliveryStreamStatus")
            if status == "ACTIVE":
                logger.info("Stream %s is now ACTIVE.", stream_name)
                return description
            logger.info(
                "Stream %s status is %s. Waiting %d seconds...",
                stream_name,
                status,
                poll_interval,
            )
            time.sleep(poll_interval)
            elapsed += poll_interval
        raise TimeoutError(
            f"Delivery stream '{stream_name}' did not become ACTIVE "
            f"within {max_wait} seconds."
        )

    def wait_for_encryption_enabled(
        self, stream_name: str, poll_interval: int = 5, max_wait: int = 60
    ) -> str:
        """
        Polls DescribeDeliveryStream until encryption status is ENABLED.

        :param stream_name: The name of the delivery stream.
        :param poll_interval: Seconds between polling attempts.
        :param max_wait: Maximum total seconds to wait.
        :return: The encryption status string.
        """
        elapsed = 0
        while elapsed < max_wait:
            description = self.describe_delivery_stream(stream_name)
            encryption_config = description.get(
                "DeliveryStreamEncryptionConfiguration", dict()
            )
            status = encryption_config.get("Status", "DISABLED")
            if status == "ENABLED":
                logger.info("Encryption is now ENABLED for stream %s.", stream_name)
                return status
            logger.info(
                "Encryption status for %s is %s. Waiting...",
                stream_name,
                status,
            )
            time.sleep(poll_interval)
            elapsed += poll_interval
        raise TimeoutError(
            f"Encryption for stream '{stream_name}' did not become ENABLED "
            f"within {max_wait} seconds."
        )


# snippet-end:[python.example_code.firehose.FirehoseWrapper.class]
