# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging
import random
from datetime import datetime, timedelta
from typing import Dict

from botocore.exceptions import ClientError

# Configure logging
logger = logging.getLogger(__name__)


class S3ConditionalRequests:
    """Encapsulates S3 conditional request operations."""

    def __init__(self, s3_client, source_bucket: str, dest_bucket: str):
        self.s3 = s3_client
        self.source_bucket = source_bucket
        self.dest_bucket = dest_bucket

    # snippet-start:[python.example_code.s3.GetObjectConditional]

    def get_object_conditional(self, object_key: str, condition_type: str, condition_value: str) -> bytes:
        """
        Retrieves an object from Amazon S3 with a conditional request.

        :param object_key: The key of the object to retrieve.
        :param condition_type: The type of condition to apply, e.g. 'IfMatch', 'IfNoneMatch', 'IfModifiedSince', 'IfUnmodifiedSince'.
        :param condition_value: The value to use for the condition.
        :return: The object data in bytes.
        """
        try:
            response = self.s3.get_object(
                Bucket=self.source_bucket,
                Key=object_key,
                **{condition_type: condition_value}
            )
            return response['Body'].read()
        except ClientError as e:
            error_code = e.response['Error']['Code']
            if error_code in ['NoSuchKey', 'NoSuchBucket']:
                logger.error(f"Error: {error_code}")
            elif error_code == 'PreconditionFailed':
                logger.error("Conditional read failed: Precondition failed")
            elif error_code == 'NotModified':
                logger.error("Conditional read failed: Object not modified")
            else:
                logger.error(f"Unexpected error: {error_code}")
            raise

    # snippet-end:[python.example_code.s3.GetObjectConditional]

    # snippet-start:[python.example_code.s3.PutObjectConditional]

    def put_object_conditional(self, object_key: str, data: bytes, condition_type: str, condition_value: str) -> dict:
        """
        Uploads an object to Amazon S3 with a conditional request.

        :param object_key: The key of the object to upload.
        :param data: The data to upload.
        :param condition_type: The type of condition to apply, e.g. 'IfNoneMatch'.
        :param condition_value: The value to use for the condition.
        :return: The response from the PUT operation.
        """
        try:
            response = self.s3.put_object(
                Bucket=self.source_bucket,
                Key=object_key,
                Body=data,
                **{condition_type: condition_value}
            )
            return response
        except ClientError as e:
            error_code = e.response['Error']['Code']
            if error_code == 'PreconditionFailed':
                logger.error("Conditional write failed: Precondition failed")
            else:
                logger.error(f"Unexpected error: {error_code}")
            raise

    # snippet-end:[python.example_code.s3.PutObjectConditional]

    # snippet-start:[python.example_code.s3.CopyObjectConditional]
    def copy_object_conditional(self, source_key: str, dest_key: str, condition_type: str, condition_value: str) -> dict:
        """
        Copies an object from one Amazon S3 bucket to another with a conditional request.

        :param source_key: The key of the source object to copy.
        :param dest_key: The key of the destination object.
        :param condition_type: The type of condition to apply, e.g. 'CopySourceIfMatch', 'CopySourceIfNoneMatch', 'CopySourceIfModifiedSince', 'CopySourceIfUnmodifiedSince'.
        :param condition_value: The value to use for the condition.
        :return: The response from the COPY operation.
        """
        try:
            response = self.s3.copy_object(
                Bucket=self.dest_bucket,
                Key=dest_key,
                CopySource={'Bucket': self.source_bucket, 'Key': source_key},
                **{condition_type: condition_value}
            )
            return response
        except ClientError as e:
            error_code = e.response['Error']['Code']
            if error_code in ['NoSuchKey', 'NoSuchBucket']:
                logger.error(f"Error: {error_code}")
            elif error_code == 'PreconditionFailed':
                logger.error("Conditional copy failed: Precondition failed")
            elif error_code == 'NotModified':
                logger.error("Conditional copy failed: Object not modified")
            else:
                logger.error(f"Unexpected error: {error_code}")
            raise

    # snippet-end:[python.example_code.s3.CopyObjectConditional]

    def _print_bucket_summary(buckets: Dict[str, str]) -> None:
        """
        Print a summary table of the created buckets.

        Args:
            buckets: A dictionary containing the names of the created buckets.
        """
        summary_table = PrettyTable()
        summary_table.field_names = [
            "Bucket Name",
            "Object Lock",
            "Default Retention",
            "Bucket Versioning",
        ]
        summary_table.align = "l"
        summary_table.add_row([buckets["no_lock"], "Disabled", "Disabled", "Disabled"])
        summary_table.add_row([buckets["lock_enabled"], "Enabled", "Disabled", "Enabled"])
        summary_table.add_row([buckets["retention"], "Disabled", "Disabled", "Enabled"])

        print("\nSummary of Buckets Created:")
        print(summary_table)


