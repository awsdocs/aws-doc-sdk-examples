# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon S3 Object Annotations — Feature Scenario

This scenario demonstrates the full lifecycle of S3 object annotations:
  1. Create a bucket.
  2. Upload a test object.
  3. Attach three annotations (plain text, JSON, classification).
  4. Retrieve a specific annotation.
  5. List all annotations.
  6. List annotations with a prefix filter.
  7. Update an annotation by overwriting it.
  8. Verify the update.
  9. Delete an annotation and confirm with NoSuchAnnotation error.
 10. List remaining annotations.
 11. Delete remaining annotations.
 12. Verify all annotations are removed.
 13. Clean up: delete object and bucket.
"""

# snippet-start:[python.example_code.s3.ObjectAnnotationsScenario]
import logging
import random
import string
import sys

from botocore.exceptions import ClientError

from s3_wrapper import S3AnnotationsWrapper

# Add relative path to include demo_tools without package setup.
sys.path.append("../../../..")
import demo_tools.question as q  # noqa

logger = logging.getLogger(__name__)

# Constants
OBJECT_KEY = "sample-data.txt"
OBJECT_CONTENT = (
    "This is a sample text file used to demonstrate " "Amazon S3 Object Annotations."
)
SEPARATOR = "-" * 80


class ObjectAnnotationsScenario:
    """Runs an interactive scenario demonstrating S3 Object Annotations."""

    def __init__(self, s3_wrapper: S3AnnotationsWrapper):
        """
        :param s3_wrapper: An S3AnnotationsWrapper instance.
        """
        self.s3_wrapper = s3_wrapper
        self.bucket_name = None

    def run_scenario(self) -> None:
        """Runs all phases of the Object Annotations scenario."""
        print(SEPARATOR)
        print(
            "Welcome to the Amazon S3 Object Annotations demo!\n\n"
            "S3 Object Annotations let you attach up to 1,000 named payloads "
            "(each up to 1 MiB)\nto any S3 object. Annotations can store rich "
            "metadata like JSON, XML, or plain text\nwithout modifying the "
            "original object."
        )
        print(SEPARATOR)

        try:
            self._setup()
            self._attach_annotations()
            self._retrieve_and_list_annotations()
            self._update_annotation()
            self._delete_annotations()
        finally:
            self._cleanup()

    # ------------------------------------------------------------------
    # Setup
    # ------------------------------------------------------------------
    def _setup(self) -> None:
        """Creates a bucket and uploads a test object.

        Prompts for a bucket name prefix and appends a random suffix. If the
        resulting bucket already exists and is owned by you
        (BucketAlreadyOwnedByYou), prompt for a different prefix and try again
        rather than terminating the scenario.
        """
        while True:
            prefix = q.ask(
                "Enter a bucket name prefix (or press Enter for 'annotations-demo'): "
            )
            if not prefix.strip():
                prefix = "annotations-demo"
            suffix = "".join(
                random.choices(string.ascii_lowercase + string.digits, k=8)
            )
            self.bucket_name = f"{prefix}-{suffix}"

            print(f"\nCreating bucket '{self.bucket_name}'...")
            try:
                self.s3_wrapper.create_bucket(self.bucket_name)
                print("Bucket created successfully.\n")
                break
            except ClientError as err:
                if err.response["Error"]["Code"] == "BucketAlreadyOwnedByYou":
                    print(
                        f"A bucket named '{self.bucket_name}' already exists and is "
                        "owned by you. Please enter a different prefix."
                    )
                    self.bucket_name = None
                    continue
                raise

        print(f"Uploading test object '{OBJECT_KEY}'...")
        response = self.s3_wrapper.put_object(
            self.bucket_name, OBJECT_KEY, OBJECT_CONTENT
        )
        print(f"Object uploaded. ETag: {response.get('ETag', 'N/A')}")
        print(SEPARATOR)
        q.ask("\nPress Enter to continue...")

    # ------------------------------------------------------------------
    # Attach annotations
    # ------------------------------------------------------------------
    def _attach_annotations(self) -> None:
        """Attaches three annotations to the test object."""
        print(SEPARATOR)
        print(f"Attaching annotations to '{OBJECT_KEY}'...\n")

        # Annotation names may contain Unicode letters, digits, underscores,
        # periods, and hyphens (1-512 bytes) and cannot start with "aws" or
        # "s3". A period is used here as a namespace delimiter (e.g. "ml.") so
        # the names stay valid while still demonstrating prefix filtering.
        annotations = [
            (
                "processing-status",
                '{"status": "pending", "submitted": "2026-09-16T10:00:00Z"}',
            ),
            (
                "ml.sentiment-analysis",
                '{"sentiment": "positive", "confidence": 0.95, "model": "v2.1"}',
            ),
            (
                "ml.content-classification",
                '{"category": "technical-documentation", "language": "en", '
                '"topics": ["cloud", "storage"]}',
            ),
        ]

        for name, payload in annotations:
            response = self.s3_wrapper.put_object_annotation(
                self.bucket_name, OBJECT_KEY, name, payload
            )
            print(f"  Added annotation '{name}' (ETag: {response.get('ETag', 'N/A')})")

        print(f"\n{len(annotations)} annotations attached successfully.")
        print(SEPARATOR)
        q.ask("\nPress Enter to continue...")

    # ------------------------------------------------------------------
    # Retrieve and list annotations
    # ------------------------------------------------------------------
    def _retrieve_and_list_annotations(self) -> None:
        """Retrieves a specific annotation and lists all/filtered annotations."""
        print(SEPARATOR)
        print("Retrieving annotation 'ml.sentiment-analysis'...\n")

        result = self.s3_wrapper.get_object_annotation(
            self.bucket_name, OBJECT_KEY, "ml.sentiment-analysis"
        )
        print(f"  Payload: {result['Payload']}")
        print(f"  Size: {result['ContentLength']} bytes")
        print(f"  ETag: {result['ETag']}")
        print(f"  Last Modified: {result['LastModified']}")

        print(f"\nListing all annotations on '{OBJECT_KEY}'...")
        all_annotations = self.s3_wrapper.list_object_annotations(
            self.bucket_name, OBJECT_KEY
        )
        print(f"  Found {len(all_annotations)} annotation(s):")
        for i, ann in enumerate(all_annotations, 1):
            print(f"    {i}. \"{ann['AnnotationName']}\" ({ann['Size']} bytes)")

        print("\nListing annotations with prefix 'ml.'...")
        ml_annotations = self.s3_wrapper.list_object_annotations(
            self.bucket_name, OBJECT_KEY, annotation_prefix="ml."
        )
        print(f"  Found {len(ml_annotations)} annotation(s):")
        for i, ann in enumerate(ml_annotations, 1):
            print(f"    {i}. \"{ann['AnnotationName']}\" ({ann['Size']} bytes)")

        print(SEPARATOR)
        q.ask("\nPress Enter to continue...")

    # ------------------------------------------------------------------
    # Update an annotation
    # ------------------------------------------------------------------
    def _update_annotation(self) -> None:
        """Overwrites an existing annotation with new content and verifies."""
        print(SEPARATOR)
        print("Updating annotation 'processing-status' with new content...")

        updated_payload = (
            '{"status": "completed", "submitted": "2026-09-16T10:00:00Z", '
            '"completed": "2026-09-16T10:05:00Z"}'
        )
        response = self.s3_wrapper.put_object_annotation(
            self.bucket_name, OBJECT_KEY, "processing-status", updated_payload
        )
        print(f"  Annotation updated. New ETag: {response.get('ETag', 'N/A')}")

        print("\nVerifying the update...")
        result = self.s3_wrapper.get_object_annotation(
            self.bucket_name, OBJECT_KEY, "processing-status"
        )
        print(f"  Payload: {result['Payload']}")
        print("  Update confirmed.")
        print(SEPARATOR)
        q.ask("\nPress Enter to continue...")

    # ------------------------------------------------------------------
    # Delete annotations
    # ------------------------------------------------------------------
    def _delete_annotations(self) -> None:
        """Deletes annotations, confirms deletion, and verifies cleanup."""
        print(SEPARATOR)
        # Delete a single annotation
        print("Deleting annotation 'processing-status'...")
        self.s3_wrapper.delete_object_annotation(
            self.bucket_name, OBJECT_KEY, "processing-status"
        )
        print("  Annotation deleted successfully.\n")

        # Attempt to retrieve the deleted annotation
        print("Attempting to retrieve deleted annotation 'processing-status'...")
        try:
            self.s3_wrapper.get_object_annotation(
                self.bucket_name, OBJECT_KEY, "processing-status"
            )
        except ClientError as err:
            if err.response["Error"]["Code"] == "NoSuchAnnotation":
                print(
                    "  Expected error: NoSuchAnnotation - "
                    "The annotation does not exist. Deletion confirmed!"
                )
            else:
                raise

        # List remaining
        print("\nListing remaining annotations...")
        remaining = self.s3_wrapper.list_object_annotations(
            self.bucket_name, OBJECT_KEY
        )
        print(f"  Found {len(remaining)} annotation(s):")
        for i, ann in enumerate(remaining, 1):
            print(f"    {i}. \"{ann['AnnotationName']}\"")

        # Delete remaining annotations
        print("\nDeleting remaining annotations...")
        for ann in remaining:
            name = ann["AnnotationName"]
            self.s3_wrapper.delete_object_annotation(self.bucket_name, OBJECT_KEY, name)
            print(f"  Deleted '{name}'.")

        # Verify all removed
        print("\nVerifying all annotations removed...")
        final = self.s3_wrapper.list_object_annotations(self.bucket_name, OBJECT_KEY)
        print(f"  {len(final)} annotations remaining. All annotations cleaned up.")
        print(SEPARATOR)

    # ------------------------------------------------------------------
    # Cleanup
    # ------------------------------------------------------------------
    def _cleanup(self) -> None:
        """Deletes the test object and bucket."""
        if self.bucket_name is None:
            return

        print(SEPARATOR)
        print("Cleaning up resources...")

        try:
            self.s3_wrapper.delete_object(self.bucket_name, OBJECT_KEY)
            print(f"  Deleted object '{OBJECT_KEY}'.")
        except ClientError:
            logger.warning("Could not delete object '%s'.", OBJECT_KEY)

        try:
            self.s3_wrapper.delete_bucket(self.bucket_name)
            print(f"  Deleted bucket '{self.bucket_name}'.")
        except ClientError:
            logger.warning("Could not delete bucket '%s'.", self.bucket_name)

        print("Cleanup complete!")
        print(SEPARATOR)


def main() -> None:
    """Entry point for the Object Annotations scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")
    wrapper = S3AnnotationsWrapper.from_client()
    scenario = ObjectAnnotationsScenario(wrapper)
    scenario.run_scenario()


if __name__ == "__main__":
    main()
# snippet-end:[python.example_code.s3.ObjectAnnotationsScenario]
