# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import logging
from datetime import datetime, timedelta

import boto3
import coloredlogs
from prettytable import PrettyTable

# Configure logging
logger = logging.getLogger(__name__)
coloredlogs.install(
    level="DEBUG", logger=logger, fmt="%(asctime)s [%(levelname)s] %(message)s"
)


# snippet-start:[python.s3-object-locking.s3_GetObjectLockConfiguration]
def is_object_lock_enabled(s3_client, bucket: str) -> bool:
    """
    Check if object lock is enabled for a bucket.

    Args:
        s3_client: Boto3 S3 client.
        bucket: The name of the bucket to check.

    Returns:
        True if object lock is enabled, False otherwise.
    """
    try:
        response = s3_client.get_object_lock_configuration(Bucket=bucket)
        return (
            "ObjectLockConfiguration" in response
            and response["ObjectLockConfiguration"]["ObjectLockEnabled"] == "Enabled"
        )
    except s3_client.exceptions.ClientError as e:
        if e.response["Error"]["Code"] == "ObjectLockConfigurationNotFoundError":
            return False
        else:
            raise


# snippet-end:[python.s3-object-locking.s3_GetObjectLockConfiguration]


def set_retention_for_deletion(
    s3_client, bucket: str, key: str, version_id: str
) -> str:
    """
    Set a future retention date to allow deletion if Object Lock is enabled.

    Args:
        s3_client: Boto3 S3 client.
        bucket: The name of the bucket containing the object.
        key: The key of the object to set the retention policy for.
        version_id: The version ID of the object.

    Returns:
        "Success" if the operation is successful, otherwise an error message.
    """
    try:
        logger.debug("Checking if object lock is enabled for bucket: %s", bucket)
        object_lock_enabled = is_object_lock_enabled(s3_client, bucket)
        if object_lock_enabled:
            logger.info(
                "Setting retention to a far future date for %s in bucket %s, version %s",
                key,
                bucket,
                version_id,
            )
            far_future_date = (datetime.now() + timedelta(days=365)).strftime(
                "%Y-%m-%dT%H:%M:%SZ"
            )
            # snippet-start:[python.example_code.s3.PutObjectRetention]
            s3_client.put_object_retention(
                Bucket=bucket,
                Key=key,
                VersionId=version_id,
                Retention={"Mode": "GOVERNANCE", "RetainUntilDate": far_future_date},
                BypassGovernanceRetention=True,
            )
            # snippet-end:[python.example_code.s3.PutObjectRetention]
    except Exception as e:
        logger.error("Error setting retention for %s in %s: %s", key, bucket, str(e))
        return f"Error setting retention: {str(e)}"
    return "Success"


def remove_object_locks_and_delete(
    s3_client, bucket: str, key: str, version_id: str
) -> str:
    """
    Remove object locks and delete the object.

    Args:
        s3_client: Boto3 S3 client.
        bucket: The name of the bucket containing the object.
        key: The key of the object to remove locks and delete.
        version_id: The version ID of the object.

    Returns:
        "Success" if the operation is successful, otherwise an error message.
    """
    result = set_retention_for_deletion(s3_client, bucket, key, version_id)
    if result != "Success":
        return result

    try:
        logger.debug("Checking if object lock is enabled for bucket: %s", bucket)
        object_lock_enabled = is_object_lock_enabled(s3_client, bucket)

        if object_lock_enabled:
            logger.info(
                "Removing legal hold for %s in bucket %s, version %s",
                key,
                bucket,
                version_id,
            )
            # snippet-start:[python.s3-object-locking.s3_PutObjectLegalHold]
            s3_client.put_object_legal_hold(
                Bucket=bucket,
                Key=key,
                VersionId=version_id,
                LegalHold={"Status": "OFF"},
            )
            # snippet-end:[python.s3-object-locking.s3_PutObjectLegalHold]
    except Exception as e:
        logger.error("Error removing legal hold for %s in %s: %s", key, bucket, str(e))
        return f"Error removing legal hold: {str(e)}"

    try:
        logger.info(
            "Deleting object %s in bucket %s, version %s", key, bucket, version_id
        )
        if object_lock_enabled:
            # snippet-start:[python.s3-object-locking.s3_Scenario_ObjectLock]
            s3_client.delete_object(
                Bucket=bucket,
                Key=key,
                VersionId=version_id,
                BypassGovernanceRetention=True,
            )
            # snippet-end:[python.s3-object-locking.s3_Scenario_ObjectLock]
        else:
            s3_client.delete_object(Bucket=bucket, Key=key, VersionId=version_id)
    except Exception as e:
        logger.error("Error deleting object %s in %s: %s", key, bucket, str(e))
        return f"Error deleting object: {str(e)}"

    return "Success"


def enable_versioning(s3_client, bucket: str) -> str:
    """
    Enable versioning for the bucket.

    Args:
        s3_client: Boto3 S3 client.
        bucket: The name of the bucket to enable versioning for.

    Returns:
        "Success" if the operation is successful, otherwise an error message.
    """
    try:
        s3_client.put_bucket_versioning(
            Bucket=bucket, VersioningConfiguration={"Status": "Enabled"}
        )
    except Exception as e:
        logger.error("Error enabling versioning for bucket %s: %s", bucket, str(e))
        return f"Error enabling versioning: {str(e)}"
    return "Success"


def disable_bucket_object_lock_configuration(s3_client, bucket: str) -> str:
    """
    Disable the object lock configuration for the bucket.

    Args:
        s3_client: Boto3 S3 client.
        bucket: The name of the bucket to disable the object lock configuration for.

    Returns:
        "Success" if the operation is successful, otherwise an error message.
    """
    try:
        logger.debug("Checking if object lock is enabled for bucket: %s", bucket)
        if not is_object_lock_enabled(s3_client, bucket):
            logger.debug("Object lock is not enabled for bucket: %s, skipping", bucket)
            return "Object lock not enabled"

        logger.info("Disabling object lock configuration for bucket: %s", bucket)
        enable_versioning(s3_client, bucket)  # Ensure versioning is enabled

        # snippet-start:[python.s3-object-locking.s3_PutObjectLockConfiguration]
        s3_client.put_object_lock_configuration(
            Bucket=bucket,
            ObjectLockConfiguration={"ObjectLockEnabled": "Disabled", "Rule": {}},
        )
        # snippet-end:[python.s3-object-locking.s3_PutObjectLockConfiguration]
    except Exception:
        logger.debug(
            "Unable to disable object lock configuration for bucket %s", bucket
        )
        return f"Unable to disable object lock configuration after bucket creation."
    return "Success"


def clean_s3_object_locking() -> None:
    """
    Clean up S3 object locking by removing locks and deleting objects and buckets.
    """
    s3_client = boto3.client("s3")

    # Read bucket names from file
    buckets = []
    try:
        with open("buckets.txt", "r") as f:
            for line in f:
                name, bucket = line.strip().split("=")
                buckets.append(bucket)
    except Exception as e:
        logger.error("Error reading bucket names from file: %s", str(e))
        return

    summary_table = PrettyTable()
    summary_table.field_names = ["Bucket", "Object", "Version ID", "Action", "Status"]

    error_table = PrettyTable()
    error_table.field_names = ["Bucket", "Object", "Version ID", "Action", "Error"]

    logger.info("Starting S3 Object Locking Cleanup")

    for bucket in buckets:
        try:
            print("\nCleaning bucket: %s", bucket)
            objects = s3_client.list_object_versions(Bucket=bucket)
            for obj in objects.get("Versions", []):
                result = remove_object_locks_and_delete(
                    s3_client, bucket, obj["Key"], obj["VersionId"]
                )
                if result == "Success":
                    summary_table.add_row(
                        [
                            bucket,
                            obj["Key"],
                            obj["VersionId"],
                            "Delete Object",
                            "Success",
                        ]
                    )
                else:
                    error_table.add_row(
                        [bucket, obj["Key"], obj["VersionId"], "Delete Bucket", result]
                    )
            for obj in objects.get("DeleteMarkers", []):
                try:
                    s3_client.delete_object(
                        Bucket=bucket, Key=obj["Key"], VersionId=obj["VersionId"]
                    )
                    summary_table.add_row(
                        [
                            bucket,
                            obj["Key"],
                            obj["VersionId"],
                            "Delete Marker",
                            "Success",
                        ]
                    )
                except Exception as e:
                    error_table.add_row(
                        [
                            bucket,
                            obj["Key"],
                            obj["VersionId"],
                            f"Error deleting marker: {str(e)}",
                        ]
                    )
            result = disable_bucket_object_lock_configuration(s3_client, bucket)
            if result != "Success" and result != "Object lock not enabled":
                error_table.add_row([bucket, "-", "-", "Delete Bucket", result])
            else:
                try:
                    logger.debug("Deleting bucket: %s", bucket)
                    s3_client.delete_bucket(Bucket=bucket)
                    logger.info("Deleted bucket: %s", bucket)
                    summary_table.add_row(
                        [bucket, "-", "-", "Delete Bucket", "Success"]
                    )
                except Exception as e:
                    logger.error("Error deleting bucket %s: %s", bucket, str(e))
                    error_table.add_row(
                        [bucket, "-", "-", "Delete Bucket", f"Error: {str(e)}"]
                    )

        except Exception as e:
            logger.error("Error cleaning bucket %s: %s", bucket, str(e))
            error_table.add_row([bucket, "-", "-", "Delete Bucket", f"Error: {str(e)}"])

    logger.info("Cleanup completed successfully!")
    print("\nSummary of Cleanup Actions:")
    print(summary_table)
    if len(error_table.rows) > 0:
        print("\nErrors Encountered During Cleanup (require manual cleanup):")
        print(error_table)
