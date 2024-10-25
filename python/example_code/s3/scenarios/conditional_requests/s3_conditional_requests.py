# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import logging

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

    def get_object_conditional(self, object_key: str, condition_type: str, condition_value: str):
        """
        Retrieves an object from Amazon S3 with a conditional request.

        :param object_key: The key of the object to retrieve.
        :param condition_type: The type of condition: 'IfMatch', 'IfNoneMatch', 'IfModifiedSince', 'IfUnmodifiedSince'.
        :param condition_value: The value to use for the condition.
        """
        try:
            response = self.s3.get_object(
                Bucket=self.source_bucket,
                Key=object_key,
                **{condition_type: condition_value}
            )
            sample_bytes = response['Body'].read(20)
            print(f"\tConditional read successful. Here are the first 20 bytes of the object:\n")
            print(f"\t{sample_bytes}")
        except ClientError as e:
            error_code = e.response['Error']['Code']
            if error_code == '412':
                print("\tConditional read failed: Precondition failed")
            if error_code == '304':
                print("\tConditional read failed: Object not modified")
            else:
                logger.error(f"Unexpected error: {error_code}")
                raise

    # snippet-end:[python.example_code.s3.GetObjectConditional]

    # snippet-start:[python.example_code.s3.PutObjectConditional]

    def put_object_conditional(self, object_key: str, data: bytes, condition_type: str, condition_value: str):
        """
        Uploads an object to Amazon S3 with a conditional request.

        :param object_key: The key of the object to upload.
        :param data: The data to upload.
        :param condition_type: The type of condition to apply, e.g. 'IfNoneMatch'.
        :param condition_value: The value to use for the condition.
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
            if error_code == '412':
                print("\tConditional copy failed: Precondition failed")
            else:
                logger.error(f"Unexpected error: {error_code}")
                raise

    # snippet-end:[python.example_code.s3.PutObjectConditional]

    # snippet-start:[python.example_code.s3.CopyObjectConditional]
    def copy_object_conditional(self, source_key: str, dest_key: str, condition_type: str, condition_value: str):
        """
        Copies an object from one Amazon S3 bucket to another with a conditional request.

        :param source_key: The key of the source object to copy.
        :param dest_key: The key of the destination object.
        :param condition_type: The type of condition to apply, e.g.
        'CopySourceIfMatch', 'CopySourceIfNoneMatch', 'CopySourceIfModifiedSince', 'CopySourceIfUnmodifiedSince'.
        :param condition_value: The value to use for the condition.
        """
        try:
            response = self.s3.copy_object(
                Bucket=self.dest_bucket,
                Key=dest_key,
                CopySource={'Bucket': self.source_bucket, 'Key': source_key},
                **{condition_type: condition_value}
            )
            sample_bytes = response['Body'].read(20)
            print(f"\tConditional copy successful. Here are the first 20 bytes of the object:\n")
            print(f"\t{sample_bytes}")
        except ClientError as e:
            error_code = e.response['Error']['Code']
            if error_code == '412':
                print("\tConditional copy failed: Precondition failed")
            if error_code == '304':
                print("\tConditional copy failed: Object not modified")
            else:
                logger.error(f"Unexpected error: {error_code}")
                raise

    # snippet-end:[python.example_code.s3.CopyObjectConditional]



