# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Wrapper class for Amazon S3 Object Annotations operations.

This module encapsulates S3 client calls for the Object Annotations feature,
including creating buckets, uploading objects, and managing annotations
(put, get, list, delete).
"""

import logging
from typing import Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.s3.S3AnnotationsWrapper.decl]
class S3AnnotationsWrapper:
    """Encapsulates Amazon S3 Object Annotations operations."""

    def __init__(self, s3_client):
        """
        Initializes the S3AnnotationsWrapper with an S3 client.

        :param s3_client: A Boto3 S3 client.
        """
        self.s3_client = s3_client

    @classmethod
    def from_client(cls):
        """
        Creates an S3AnnotationsWrapper using a default Boto3 S3 client.

        :return: An initialized S3AnnotationsWrapper instance.
        """
        s3_client = boto3.client("s3")
        return cls(s3_client)

    # snippet-end:[python.example_code.s3.S3AnnotationsWrapper.decl]

    def create_bucket(self, bucket_name: str, region: Optional[str] = None) -> None:
        """
        Creates an S3 general purpose bucket.

        :param bucket_name: The name of the bucket to create.
        :param region: The AWS Region in which to create the bucket. If None,
                       the client's configured region is used.
        :raises ClientError: If the bucket already exists and is owned by you
                             (BucketAlreadyOwnedByYou) or another error occurs.
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
            if err.response["Error"]["Code"] == "BucketAlreadyOwnedByYou":
                logger.error(
                    "Bucket '%s' already exists and is owned by you. "
                    "Please choose a different name.",
                    bucket_name,
                )
            raise

    def put_object(self, bucket_name: str, object_key: str, body: str) -> dict:
        """
        Uploads an object to an S3 bucket.

        :param bucket_name: The name of the bucket to upload to.
        :param object_key: The key to assign to the uploaded object.
        :param body: The content of the object to upload.
        :return: A dict containing the response from PutObject, including ETag.
        :raises ClientError: If the bucket does not exist (NoSuchBucket) or
                             another error occurs.
        """
        try:
            response = self.s3_client.put_object(
                Bucket=bucket_name,
                Key=object_key,
                Body=body,
            )
            logger.info(
                "Uploaded object '%s' to bucket '%s'. ETag: %s",
                object_key,
                bucket_name,
                response.get("ETag", "N/A"),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchBucket":
                logger.error(
                    "Bucket '%s' does not exist. Verify bucket creation succeeded.",
                    bucket_name,
                )
            raise

    # snippet-start:[python.example_code.s3.PutObjectAnnotation]
    def put_object_annotation(
        self,
        bucket_name: str,
        object_key: str,
        annotation_name: str,
        annotation_payload: str,
    ) -> dict:
        """
        Attaches a named annotation payload to an S3 object.

        :param bucket_name: The name of the bucket containing the object.
        :param object_key: The key of the object to annotate.
        :param annotation_name: The name of the annotation (1-512 bytes).
        :param annotation_payload: The annotation content (1 byte to 1 MiB).
        :return: A dict containing the PutObjectAnnotation response with ETag.
        :raises ClientError: If the annotation name is invalid
                             (InvalidAnnotationName) or another error occurs.
        """
        try:
            response = self.s3_client.put_object_annotation(
                Bucket=bucket_name,
                Key=object_key,
                AnnotationName=annotation_name,
                AnnotationPayload=annotation_payload.encode("utf-8"),
            )
            logger.info(
                "Put annotation '%s' on object '%s' in bucket '%s'. ETag: %s",
                annotation_name,
                object_key,
                bucket_name,
                response.get("ETag", "N/A"),
            )
            return response
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidAnnotationName":
                logger.error(
                    "Annotation name '%s' is invalid. Names must be 1-512 bytes, "
                    "UTF-8 encoded, and cannot start with 'aws' or 's3'.",
                    annotation_name,
                )
            raise

    # snippet-end:[python.example_code.s3.PutObjectAnnotation]

    # snippet-start:[python.example_code.s3.GetObjectAnnotation]
    def get_object_annotation(
        self,
        bucket_name: str,
        object_key: str,
        annotation_name: str,
    ) -> dict:
        """
        Retrieves a specific annotation by name from an S3 object.

        :param bucket_name: The name of the bucket containing the object.
        :param object_key: The key of the object.
        :param annotation_name: The name of the annotation to retrieve.
        :return: A dict containing the annotation payload (decoded), ETag,
                 ContentLength, and LastModified.
        :raises ClientError: If the annotation does not exist
                             (NoSuchAnnotation) or another error occurs.
        """
        try:
            response = self.s3_client.get_object_annotation(
                Bucket=bucket_name,
                Key=object_key,
                AnnotationName=annotation_name,
            )
            payload = response["AnnotationPayload"].read().decode("utf-8")
            result = dict(
                Payload=payload,
                ETag=response.get("ETag", "N/A"),
                ContentLength=response.get("ContentLength", 0),
                LastModified=response.get("LastModified", None),
            )
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
        Lists annotations attached to an S3 object. Uses a paginator to
        handle results that span multiple pages.

        :param bucket_name: The name of the bucket containing the object.
        :param object_key: The key of the object.
        :param annotation_prefix: Optional prefix to filter annotation names.
        :return: A list of annotation entry dicts, each containing
                 AnnotationName, Size, ETag, and LastModified.
        :raises ClientError: If the object does not exist (NoSuchKey) or
                             another error occurs.
        """
        try:
            annotations = list()
            paginator = self.s3_client.get_paginator("list_object_annotations")
            params = dict(Bucket=bucket_name, Key=object_key)
            if annotation_prefix is not None:
                params["AnnotationPrefix"] = annotation_prefix
            for page in paginator.paginate(**params):
                page_annotations = page.get("Annotations", list())
                for annotation in page_annotations:
                    annotations.append(
                        dict(
                            AnnotationName=annotation.get("AnnotationName", ""),
                            Size=annotation.get("Size", 0),
                            ETag=annotation.get("ETag", ""),
                            LastModified=annotation.get("LastModified", None),
                        )
                    )
            logger.info(
                "Listed %d annotation(s) on object '%s' in bucket '%s'%s.",
                len(annotations),
                object_key,
                bucket_name,
                f" with prefix '{annotation_prefix}'" if annotation_prefix else "",
            )
            return annotations
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchKey":
                logger.error(
                    "Object '%s' does not exist in bucket '%s'.",
                    object_key,
                    bucket_name,
                )
            raise

    # snippet-end:[python.example_code.s3.ListObjectAnnotations]

    # snippet-start:[python.example_code.s3.DeleteObjectAnnotation]
    def delete_object_annotation(
        self,
        bucket_name: str,
        object_key: str,
        annotation_name: str,
    ) -> None:
        """
        Permanently deletes a specific annotation from an S3 object.

        :param bucket_name: The name of the bucket containing the object.
        :param object_key: The key of the object.
        :param annotation_name: The name of the annotation to delete.
        :raises ClientError: If the annotation does not exist
                             (NoSuchAnnotation) or another error occurs.
        """
        try:
            self.s3_client.delete_object_annotation(
                Bucket=bucket_name,
                Key=object_key,
                AnnotationName=annotation_name,
            )
            logger.info(
                "Deleted annotation '%s' from object '%s' in bucket '%s'.",
                annotation_name,
                object_key,
                bucket_name,
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchAnnotation":
                logger.error(
                    "Annotation '%s' has already been deleted or does not exist "
                    "on object '%s' in bucket '%s'.",
                    annotation_name,
                    object_key,
                    bucket_name,
                )
            raise

    # snippet-end:[python.example_code.s3.DeleteObjectAnnotation]

    def delete_object(self, bucket_name: str, object_key: str) -> None:
        """
        Deletes an object from an S3 bucket.

        :param bucket_name: The name of the bucket containing the object.
        :param object_key: The key of the object to delete.
        :raises ClientError: If the object does not exist (NoSuchKey) or
                             another error occurs.
        """
        try:
            self.s3_client.delete_object(Bucket=bucket_name, Key=object_key)
            logger.info(
                "Deleted object '%s' from bucket '%s'.", object_key, bucket_name
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchKey":
                logger.error(
                    "Object '%s' does not exist in bucket '%s'. "
                    "It may have already been deleted.",
                    object_key,
                    bucket_name,
                )
            raise

    def delete_bucket(self, bucket_name: str) -> None:
        """
        Deletes an S3 bucket.

        :param bucket_name: The name of the bucket to delete.
        :raises ClientError: If the bucket does not exist (NoSuchBucket) or
                             another error occurs.
        """
        try:
            self.s3_client.delete_bucket(Bucket=bucket_name)
            logger.info("Deleted bucket '%s'.", bucket_name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchBucket":
                logger.error(
                    "Bucket '%s' does not exist. It may have already been deleted.",
                    bucket_name,
                )
            raise
