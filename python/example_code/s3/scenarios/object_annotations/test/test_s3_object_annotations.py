# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the Amazon S3 Object Annotations scenario.

These tests run against the live S3 service and create/delete real resources.
Do NOT mock the S3 client — these are integration tests.
"""

import json
import uuid

import boto3
import pytest

from s3_wrapper import S3AnnotationsWrapper


@pytest.fixture(scope="module")
def s3_client():
    """Creates a shared Boto3 S3 client for the test module."""
    return boto3.client("s3")


@pytest.fixture(scope="module")
def s3_wrapper(s3_client):
    """Creates a shared S3AnnotationsWrapper for the test module."""
    return S3AnnotationsWrapper(s3_client)


@pytest.fixture(scope="module")
def test_bucket(s3_wrapper):
    """
    Creates a unique test bucket for the module and deletes it after all tests.
    """
    unique_suffix = str(uuid.uuid4())[:8]
    bucket_name = f"s3-annotations-integ-test-{unique_suffix}"
    s3_wrapper.create_bucket(bucket_name)

    yield bucket_name

    # Cleanup: delete bucket (should be empty by end of tests)
    try:
        s3_wrapper.delete_bucket(bucket_name)
    except Exception:
        pass


@pytest.fixture(scope="module")
def test_object(s3_wrapper, test_bucket):
    """
    Uploads a test object to the test bucket and deletes it after all tests.
    """
    object_key = "test-annotations-object.txt"
    content = "This is a test object for S3 object annotations integration tests."
    s3_wrapper.put_object(test_bucket, object_key, content)

    yield object_key

    # Cleanup: delete the object
    try:
        s3_wrapper.delete_object(test_bucket, object_key)
    except Exception:
        pass


@pytest.mark.integ
class TestS3ObjectAnnotations:
    """Integration tests for S3 object annotations."""

    def test_put_and_get_annotation(self, s3_wrapper, test_bucket, test_object):
        """Tests putting and getting an annotation on an S3 object."""
        annotation_name = "test-classification"
        payload = json.dumps({"category": "test", "confidence": 0.99})

        try:
            # Put an annotation
            put_response = s3_wrapper.put_object_annotation(
                test_bucket, test_object, annotation_name, payload
            )
            assert "ETag" in put_response

            # Get the annotation
            get_response = s3_wrapper.get_object_annotation(
                test_bucket, test_object, annotation_name
            )
            assert get_response["AnnotationPayload"] == payload
            assert get_response["ETag"] != ""
            assert get_response["ContentLength"] > 0
        finally:
            # Cleanup
            try:
                s3_wrapper.delete_object_annotation(
                    test_bucket, test_object, annotation_name
                )
            except Exception:
                pass

    def test_list_annotations(self, s3_wrapper, test_bucket, test_object):
        """Tests listing annotations on an S3 object."""
        annotation_names = ["list-test.alpha", "list-test.beta", "list-test.gamma"]
        payload = json.dumps({"test": True})

        try:
            # Put multiple annotations
            for name in annotation_names:
                s3_wrapper.put_object_annotation(
                    test_bucket, test_object, name, payload
                )

            # List all annotations
            annotations = s3_wrapper.list_object_annotations(
                test_bucket, test_object
            )
            found_names = [a["AnnotationName"] for a in annotations]
            for name in annotation_names:
                assert name in found_names, f"Annotation '{name}' not found in listing"
        finally:
            # Cleanup
            for name in annotation_names:
                try:
                    s3_wrapper.delete_object_annotation(
                        test_bucket, test_object, name
                    )
                except Exception:
                    pass

    def test_list_annotations_with_prefix(self, s3_wrapper, test_bucket, test_object):
        """Tests listing annotations with a prefix filter."""
        names_with_prefix = ["prefix-filter.one", "prefix-filter.two"]
        other_name = "other-annotation"
        payload = json.dumps({"test": True})

        try:
            for name in names_with_prefix + [other_name]:
                s3_wrapper.put_object_annotation(
                    test_bucket, test_object, name, payload
                )

            # Filter by prefix
            filtered = s3_wrapper.list_object_annotations(
                test_bucket, test_object, annotation_prefix="prefix-filter."
            )
            filtered_names = [a["AnnotationName"] for a in filtered]
            assert len(filtered_names) == 2
            for name in names_with_prefix:
                assert name in filtered_names
            assert other_name not in filtered_names
        finally:
            for name in names_with_prefix + [other_name]:
                try:
                    s3_wrapper.delete_object_annotation(
                        test_bucket, test_object, name
                    )
                except Exception:
                    pass

    def test_update_annotation(self, s3_wrapper, test_bucket, test_object):
        """Tests updating an existing annotation (overwriting the payload)."""
        annotation_name = "update-test"
        original_payload = json.dumps({"version": 1})
        updated_payload = json.dumps({"version": 2, "updated": True})

        try:
            # Create annotation
            original_response = s3_wrapper.put_object_annotation(
                test_bucket, test_object, annotation_name, original_payload
            )
            original_etag = original_response.get("ETag", "")

            # Update annotation
            s3_wrapper.put_object_annotation(
                test_bucket, test_object, annotation_name, updated_payload
            )

            # Verify the update
            get_response = s3_wrapper.get_object_annotation(
                test_bucket, test_object, annotation_name
            )
            assert get_response["AnnotationPayload"] == updated_payload
            assert get_response["ETag"] != original_etag
        finally:
            try:
                s3_wrapper.delete_object_annotation(
                    test_bucket, test_object, annotation_name
                )
            except Exception:
                pass

    def test_delete_annotation(self, s3_wrapper, test_bucket, test_object):
        """Tests deleting an annotation and verifying removal."""
        annotation_name = "delete-test"
        payload = json.dumps({"to_delete": True})

        try:
            # Create annotation
            s3_wrapper.put_object_annotation(
                test_bucket, test_object, annotation_name, payload
            )

            # Delete annotation
            s3_wrapper.delete_object_annotation(
                test_bucket, test_object, annotation_name
            )

            # Verify it's gone by listing
            annotations = s3_wrapper.list_object_annotations(
                test_bucket, test_object
            )
            found_names = [a["AnnotationName"] for a in annotations]
            assert annotation_name not in found_names
        finally:
            # Ensure cleanup even if test assertion failed before delete
            try:
                s3_wrapper.delete_object_annotation(
                    test_bucket, test_object, annotation_name
                )
            except Exception:
                pass

    def test_get_nonexistent_annotation_raises(
        self, s3_wrapper, test_bucket, test_object
    ):
        """Tests that getting a nonexistent annotation raises ClientError."""
        from botocore.exceptions import ClientError

        with pytest.raises(ClientError) as exc_info:
            s3_wrapper.get_object_annotation(
                test_bucket, test_object, "nonexistent-annotation"
            )
        assert exc_info.value.response["Error"]["Code"] == "NoSuchAnnotation"


@pytest.mark.integ
class TestS3BucketAndObject:
    """Integration tests for basic S3 bucket and object operations."""

    def test_create_and_delete_bucket(self, s3_wrapper):
        """Tests creating and deleting an S3 bucket."""
        unique_suffix = str(uuid.uuid4())[:8]
        bucket_name = f"s3-integ-test-bucket-{unique_suffix}"

        try:
            result = s3_wrapper.create_bucket(bucket_name)
            assert result == bucket_name
        finally:
            try:
                s3_wrapper.delete_bucket(bucket_name)
            except Exception:
                pass

    def test_put_and_delete_object(self, s3_wrapper, test_bucket):
        """Tests uploading and deleting an object."""
        object_key = "test-put-delete.txt"
        content = "Test content for put and delete."

        try:
            response = s3_wrapper.put_object(test_bucket, object_key, content)
            assert response is not None
        finally:
            try:
                s3_wrapper.delete_object(test_bucket, object_key)
            except Exception:
                pass
