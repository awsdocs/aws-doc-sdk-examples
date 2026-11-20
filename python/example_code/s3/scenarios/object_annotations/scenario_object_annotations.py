# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Demonstrates the full lifecycle of Amazon S3 object annotations using the
    AWS SDK for Python (Boto3).

    This scenario walks through:
    1. Creating an S3 bucket and uploading a test object.
    2. Attaching structured JSON annotations to the object.
    3. Retrieving and listing annotations.
    4. Updating an existing annotation.
    5. Filtering annotations by prefix.
    6. Deleting annotations and cleaning up all resources.
"""

# snippet-start:[python.example_code.s3.ObjectAnnotationsScenario]
import json
import logging
import sys
import uuid

import boto3
from botocore.exceptions import ClientError

from s3_wrapper import S3AnnotationsWrapper

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append("../../../..")
import demo_tools.question as q  # noqa

logger = logging.getLogger(__name__)


class ObjectAnnotationsScenario:
    """Runs an interactive scenario demonstrating S3 object annotations."""

    def __init__(self, s3_wrapper: S3AnnotationsWrapper):
        """
        Initializes the scenario.

        :param s3_wrapper: An S3AnnotationsWrapper instance for service operations.
        """
        self.s3_wrapper = s3_wrapper
        self.bucket_name = None
        self.object_key = None

    def run_scenario(self) -> None:
        """Runs the complete object annotations scenario."""
        print("=" * 68)
        print("Welcome to the Amazon S3 Object Annotations Scenario!")
        print("=" * 68)
        print(
            "\nThis scenario demonstrates the full lifecycle of S3 object\n"
            "annotations: creating, retrieving, listing, updating, filtering,\n"
            "and deleting annotations on S3 objects.\n"
        )
        q.ask("Press Enter to begin...")

        try:
            self.setup()
            self.add_annotations()
            self.retrieve_and_inspect_annotations()
            self.update_annotation()
            self.delete_and_verify()
        finally:
            self.cleanup()

        print("\n" + "=" * 68)
        print("S3 Object Annotations scenario complete!")
        print("=" * 68)

    def setup(self) -> None:
        """Creates the S3 bucket and uploads a test object."""
        print("\n--- Setup ---\n")

        # Step 1: Create an S3 bucket
        unique_suffix = str(uuid.uuid4())[:8]
        self.bucket_name = f"s3-annotations-demo-{unique_suffix}"
        print(f"Creating S3 bucket: {self.bucket_name}")
        self.s3_wrapper.create_bucket(self.bucket_name)
        print(f"  Bucket created: {self.bucket_name}\n")

        # Step 2: Upload a test object
        self.object_key = "sample-document.txt"
        content = (
            "This is a sample document for demonstrating S3 object annotations."
        )
        print(f"Uploading test object: {self.object_key}")
        self.s3_wrapper.put_object(self.bucket_name, self.object_key, content)
        print(f"  Object uploaded: {self.object_key}\n")
        q.ask("Press Enter to continue...")

    def add_annotations(self) -> None:
        """Attaches multiple annotations to the test object."""
        print("\n--- Add Annotations ---\n")

        # Step 3: Add a classification annotation
        classification_payload = json.dumps(
            {
                "category": "financial",
                "subcategory": "quarterly-report",
                "confidence": 0.95,
                "classified_by": "ml-pipeline-v2",
            }
        )
        print("Adding 'classification' annotation...")
        response = self.s3_wrapper.put_object_annotation(
            self.bucket_name, self.object_key, "classification", classification_payload
        )
        print(f"  Annotation: classification")
        print(f"  ETag: {response.get('ETag', 'N/A')}\n")

        # Step 4: Add a compliance annotation (using periods for namespace)
        pii_scan_payload = json.dumps(
            {
                "contains_pii": False,
                "scan_date": "2024-10-13",
                "scanner_version": "3.1.0",
                "policy": "GDPR",
            }
        )
        print("Adding 'compliance.pii-scan' annotation...")
        print("  (Using periods in annotation names creates a logical namespace.)")
        response = self.s3_wrapper.put_object_annotation(
            self.bucket_name,
            self.object_key,
            "compliance.pii-scan",
            pii_scan_payload,
        )
        print(f"  Annotation: compliance.pii-scan")
        print(f"  ETag: {response.get('ETag', 'N/A')}\n")

        # Step 5: Add a retention annotation under compliance prefix
        retention_payload = json.dumps(
            {
                "retention_days": 365,
                "retention_policy": "regulatory",
                "applied_date": "2024-10-13",
            }
        )
        print("Adding 'compliance.retention' annotation...")
        response = self.s3_wrapper.put_object_annotation(
            self.bucket_name,
            self.object_key,
            "compliance.retention",
            retention_payload,
        )
        print(f"  Annotation: compliance.retention")
        print(f"  ETag: {response.get('ETag', 'N/A')}\n")

        print("All three annotations have been attached to the object.")
        q.ask("Press Enter to continue...")

    def retrieve_and_inspect_annotations(self) -> None:
        """Retrieves and lists annotations on the test object."""
        print("\n--- Retrieve and Inspect Annotations ---\n")

        # Step 6: Get a specific annotation
        print("Retrieving 'classification' annotation by name...")
        annotation = self.s3_wrapper.get_object_annotation(
            self.bucket_name, self.object_key, "classification"
        )
        print(f"  Payload: {annotation['AnnotationPayload']}")
        print(f"  ETag: {annotation['ETag']}")
        print(f"  Last Modified: {annotation['LastModified']}")
        print(f"  Content Length: {annotation['ContentLength']} bytes\n")

        # Save the original ETag for comparison after update
        self.original_classification_etag = annotation["ETag"]

        # Step 7: List all annotations
        print("Listing all annotations on the object...")
        annotations = self.s3_wrapper.list_object_annotations(
            self.bucket_name, self.object_key
        )
        print(f"  Found {len(annotations)} annotation(s):")
        for ann in annotations:
            print(
                f"    - {ann['AnnotationName']}: "
                f"size={ann.get('Size', 'N/A')} bytes, "
                f"ETag={ann.get('ETag', 'N/A')}, "
                f"LastModified={ann.get('LastModified', 'N/A')}"
            )
        print()

        # Step 8: List annotations with prefix filter
        print("Listing annotations with prefix 'compliance.'...")
        filtered = self.s3_wrapper.list_object_annotations(
            self.bucket_name, self.object_key, annotation_prefix="compliance."
        )
        print(f"  Found {len(filtered)} annotation(s) matching prefix 'compliance.':")
        for ann in filtered:
            print(
                f"    - {ann['AnnotationName']}: "
                f"size={ann.get('Size', 'N/A')} bytes, "
                f"ETag={ann.get('ETag', 'N/A')}"
            )
        print(
            "\n  Prefix filtering lets you query logical groups of annotations."
        )
        q.ask("Press Enter to continue...")

    def update_annotation(self) -> None:
        """Updates an existing annotation and verifies the change."""
        print("\n--- Update an Existing Annotation ---\n")

        # Step 9: Update the classification annotation
        updated_payload = json.dumps(
            {
                "category": "financial",
                "subcategory": "quarterly-report",
                "confidence": 0.98,
                "classified_by": "ml-pipeline-v3",
                "review_status": "approved",
            }
        )
        print("Updating 'classification' annotation with new payload...")
        print(
            "  (Calling PutObjectAnnotation with an existing annotation name\n"
            "   overwrites the previous value.)"
        )
        self.s3_wrapper.put_object_annotation(
            self.bucket_name, self.object_key, "classification", updated_payload
        )
        print("  Annotation updated.\n")

        # Step 10: Verify the update
        print("Verifying the update by retrieving 'classification' again...")
        updated_annotation = self.s3_wrapper.get_object_annotation(
            self.bucket_name, self.object_key, "classification"
        )
        print(f"  Updated Payload: {updated_annotation['AnnotationPayload']}")
        print(f"  New ETag: {updated_annotation['ETag']}")
        if updated_annotation["ETag"] != self.original_classification_etag:
            print(
                "  The annotation ETag has changed, confirming the content was updated."
            )
        else:
            print("  Note: The ETag appears unchanged.")
        q.ask("\nPress Enter to continue...")

    def delete_and_verify(self) -> None:
        """Deletes an annotation and verifies removal."""
        print("\n--- Delete Annotations and Verify Removal ---\n")

        # Step 11: Delete an annotation
        print("Deleting 'compliance.pii-scan' annotation...")
        self.s3_wrapper.delete_object_annotation(
            self.bucket_name, self.object_key, "compliance.pii-scan"
        )
        print("  Deleted. (Annotation deletion is permanent and irreversible.)\n")

        # Step 12: Verify deletion
        print("Verifying deletion by listing remaining annotations...")
        remaining = self.s3_wrapper.list_object_annotations(
            self.bucket_name, self.object_key
        )
        print(f"  {len(remaining)} annotation(s) remain:")
        for ann in remaining:
            print(f"    - {ann['AnnotationName']}")
        q.ask("\nPress Enter to proceed to cleanup...")

    def cleanup(self) -> None:
        """Cleans up all resources created during the scenario."""
        print("\n--- Cleanup ---\n")

        if self.bucket_name is None:
            print("No resources to clean up.")
            return

        # Step 13: Delete remaining annotations
        try:
            annotations = self.s3_wrapper.list_object_annotations(
                self.bucket_name, self.object_key
            )
            for ann in annotations:
                name = ann["AnnotationName"]
                print(f"Deleting annotation '{name}'...")
                self.s3_wrapper.delete_object_annotation(
                    self.bucket_name, self.object_key, name
                )
                print(f"  Deleted annotation '{name}'.")
        except ClientError:
            logger.info("Could not list/delete annotations during cleanup.")

        # Step 14: Delete the test object
        try:
            print(f"Deleting object '{self.object_key}'...")
            self.s3_wrapper.delete_object(self.bucket_name, self.object_key)
            print(f"  Deleted object '{self.object_key}'.")
        except ClientError:
            logger.info("Could not delete object during cleanup.")

        # Step 15: Delete the bucket
        try:
            print(f"Deleting bucket '{self.bucket_name}'...")
            self.s3_wrapper.delete_bucket(self.bucket_name)
            print(f"  Deleted bucket '{self.bucket_name}'.")
        except ClientError:
            logger.info("Could not delete bucket during cleanup.")

        # Step 16: Confirm cleanup
        print("\nAll resources have been cleaned up successfully.")


def main():
    """Entry point for the S3 object annotations scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    s3_client = boto3.client("s3")
    s3_wrapper = S3AnnotationsWrapper(s3_client)
    scenario = ObjectAnnotationsScenario(s3_wrapper)
    scenario.run_scenario()


if __name__ == "__main__":
    main()
# snippet-end:[python.example_code.s3.ObjectAnnotationsScenario]
