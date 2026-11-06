# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Wraps Amazon S3 object annotation actions, including creating, retrieving,
    listing, updating, and deleting annotations on S3 objects. Also wraps
    basic S3 bucket and object operations used by the scenario.
"""

import json
import logging
from typing import Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.s3.S3AnnotationsWrapper.decl]
class S3AnnotationsWrapper:
    """Encapsulates Amazon S3 object annotation operations."""

    def __init__(self, s3_client):
        """
        Initializes the S3AnnotationsWrapper with a Boto3 S3 client.

        :param s3_client: A Boto3 S3 client.
        """
        self.s3_client = s3_client

    @classmethod
    def from_client(cls):
        """
        Creates an S3AnnotationsWrapper instance using a default Boto3 S3 client.

        :return: A new S3AnnotationsWrapper instance.
        """
        s3_client = boto3.client("s3")
        return cls(s3_client)

    # snippet-end:[python.example_code.s3.S3AnnotationsWrapper.decl]

    # snippet-start:[python.example_code.s3.CreateBucket]
    def create_bucket(self, bucket_name: str, region: Optional[str] = None) -> str:
        """
        Creates an Amazon S3 bucket.

        :param bucket_name: The name of the bucket to create.
        :param region: The AWS Region in which to create the bucket. If not
                       specified, the bucket is created in the default Region.
        :return: The name of the created bucket.
        """
        try:
            if region is None:
                region = self.s3_client.meta.region_name
            if region == "us-east-1":
                self.s3_client.create_bucket(Bucket=bucket_name)
            else:
                self.s3_client.create_bucket(
                    Bucket=bucket_name,
                    CreateBucketConfiguration={"LocationConstraint": region},
                )
            logger.info("Created bucket '%s' in region '%s'.", bucket_name, region)
        except ClientError as err:
            if err.response["Error"]["Code"] == "BucketAlreadyExists":
                logger.error(
                    "Bucket name '%s' is already taken. Use a unique name.", bucket_name
                )
            raise
        return bucket_name

    # snippet-end:[python.example_code.s3.CreateBucket]

    # snippet-start:[python.example_code.s3.PutObject]
    def put_object(self, bucket_name: str, object_key: str, body: str) -> dict:
        """
        Uploads an object to an Amazon S3 bucket.

        :param bucket_name: The name of the bucket to upload the object to.
        :param object_key: The key name for the object.
        :param body: The content of the object.
        :return: The response from S3.
        """
        try:
            response = self.s3_client.put_object(
                Bucket=bucket_name, Key=object_key, Body=body
            )
            logger.info(
                "Uploaded object '%s' to bucket '%s'.", object_key, bucket_name
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchBucket":
                logger.error(
                    "Bucket '%s' does not exist. Verify the bucket was created.",
                    bucket_name,
                )
            raise

    # snippet-end:[python.example_code.s3.PutObject]

    # snippet-start:[python.example_code.s3.PutObjectAnnotation]
    def put_object_annotation(
        self,
        bucket_name: str,
        object_key: str,
        annotation_name: str,
        annotation_payload: str,
    ) -> dict:
        """
        Attaches a named annotation payload to an S3 object. If an annotation
        with the same name already exists, it is overwritten.

        :param bucket_name: The name of the bucket containing the object.
        :param object_key: The key of the object to annotate.
        :param annotation_name: The name of the annotation (1-512 bytes).
        :param annotation_payload: The annotation body (1 byte to 1 MiB).
        :return: The response from S3, including the annotation ETag.
        """
        try:
            response = self.s3_client.put_object_annotation(
                Bucket=bucket_name,
                Key=object_key,
                AnnotationName=annotation_name,
                AnnotationPayload=annotation_payload,
            )
            logger.info(
                "Attached annotation '%s' to object '%s' in bucket '%s'. ETag: %s",
                annotation_name,
                object_key,
                bucket_name,
                response.get("ETag", "N/A"),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchKey":
                logger.error(
                    "Object '%s' does not exist in bucket '%s'. Verify the object "
                    "exists before attaching an annotation.",
                    object_key,
                    bucket_name,
                )
            raise

    # snippet-end:[python.example_code.s3.PutObjectAnnotation]

    # snippet-start:[python.example_code.s3.GetObjectAnnotation]
    def get_object_annotation(
        self, bucket_name: str, object_key: str, annotation_name: str
    ) -> dict:
        """
        Retrieves a specific annotation by name from an S3 object.

        :param bucket_name: The name of the bucket containing the object.
        :param object_key: The key of the object.
        :param annotation_name: The name of the annotation to retrieve.
        :return: A dict with annotation details including the payload.
        """
        try:
            response = self.s3_client.get_object_annotation(
                Bucket=bucket_name, Key=object_key, AnnotationName=annotation_name
            )
            # Read the streaming body for the annotation payload.
            payload = response["AnnotationPayload"].read().decode("utf-8")
            result = dict()
            result["AnnotationPayload"] = payload
            result["ETag"] = response.get("ETag", "")
            result["LastModified"] = response.get("LastModified", None)
            result["ContentLength"] = response.get("ContentLength", 0)
            logger.info(
                "Retrieved annotation '%s' from object '%s' in bucket '%s'.",
                annotation_name,
                object_key,
                bucket_name,
            )
            return result
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchAnnotation":
                logger.error(
                    "Annotation '%s' does not exist on object '%s' in bucket '%s'.",
                    annotation_name,
                    object_key,
                    bucket_name,
                )
            raise

    # snippet-end:[python.example_code.s3.GetObjectAnnotation]

    # snippet-start:[python.example_code.s3.ListObjectAnnotations]
    def list_object_annotations(
        self,
        bucket_name: str,
        object_key: str,
        annotation_prefix: Optional[str] = None,
    ) -> list:
        """
        Lists all annotations on an S3 object. Uses the paginator for
        automatic handling of paginated results.

        :param bucket_name: The name of the bucket containing the object.
        :param object_key: The key of the object.
        :param annotation_prefix: Optional prefix to filter annotations by name.
        :return: A list of annotation entries.
        """
        try:
            annotations = list()
            paginator = self.s3_client.get_paginator("list_object_annotations")
            params = dict()
            params["Bucket"] = bucket_name
            params["Key"] = object_key
            if annotation_prefix is not None:
                params["AnnotationPrefix"] = annotation_prefix

            for page in paginator.paginate(**params):
                page_annotations = page.get("Annotations", list())
                annotations.extend(page_annotations)

            prefix_msg = (
                f" with prefix '{annotation_prefix}'" if annotation_prefix else ""
            )
            logger.info(
                "Listed %d annotation(s) on object '%s' in bucket '%s'%s.",
                len(annotations),
                object_key,
                bucket_name,
                prefix_msg,
            )
            return annotations
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchKey":
                logger.error(
                    "Object '%s' does not exist in bucket '%s'. Verify the object "
                    "key is correct.",
                    object_key,
                    bucket_name,
                )
            raise

    # snippet-end:[python.example_code.s3.ListObjectAnnotations]

    # snippet-start:[python.example_code.s3.DeleteObjectAnnotation]
    def delete_object_annotation(
        self, bucket_name: str, object_key: str, annotation_name: str
    ) -> None:
        """
        Permanently removes a specific annotation from an S3 object.
        Annotation deletion is permanent and irreversible.

        :param bucket_name: The name of the bucket containing the object.
        :param object_key: The key of the object.
        :param annotation_name: The name of the annotation to delete.
        """
        try:
            self.s3_client.delete_object_annotation(
                Bucket=bucket_name, Key=object_key, AnnotationName=annotation_name
            )
            logger.info(
                "Deleted annotation '%s' from object '%s' in bucket '%s'.",
                annotation_name,
                object_key,
                bucket_name,
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchBucket":
                logger.error(
                    "Bucket '%s' does not exist. Verify the bucket exists before "
                    "attempting to delete an annotation.",
                    bucket_name,
                )
            raise

    # snippet-end:[python.example_code.s3.DeleteObjectAnnotation]

    # snippet-start:[python.example_code.s3.DeleteObject]
    def delete_object(self, bucket_name: str, object_key: str) -> None:
        """
        Deletes an object from an S3 bucket.

        :param bucket_name: The name of the bucket.
        :param object_key: The key of the object to delete.
        """
        try:
            self.s3_client.delete_object(Bucket=bucket_name, Key=object_key)
            logger.info(
                "Deleted object '%s' from bucket '%s'.", object_key, bucket_name
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchKey":
                logger.warning(
                    "Object '%s' not found in bucket '%s'. It may have already "
                    "been deleted.",
                    object_key,
                    bucket_name,
                )
            raise

    # snippet-end:[python.example_code.s3.DeleteObject]

    # snippet-start:[python.example_code.s3.DeleteBucket]
    def delete_bucket(self, bucket_name: str) -> None:
        """
        Deletes an S3 bucket. The bucket must be empty first.

        :param bucket_name: The name of the bucket to delete.
        """
        try:
            self.s3_client.delete_bucket(Bucket=bucket_name)
            logger.info("Deleted bucket '%s'.", bucket_name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "BucketNotEmpty":
                logger.error(
                    "Bucket '%s' is not empty. Delete all objects and annotations "
                    "before deleting the bucket.",
                    bucket_name,
                )
            raise

    # snippet-end:[python.example_code.s3.DeleteBucket]
