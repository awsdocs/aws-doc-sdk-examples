import boto3
import random
import logging
import coloredlogs
from prettytable import PrettyTable

bucket_prefix = "py-object-locking"
file_content = "This is a test file for S3 Object Locking."

# Configure logging
logger = logging.getLogger(__name__)
coloredlogs.install(level='DEBUG', logger=logger, fmt='%(asctime)s [%(levelname)s] %(message)s')

random_suffix = str(random.randint(100, 999))

def create_buckets(s3_client):
    buckets = {
        "no_lock": f"{bucket_prefix}-no-lock-{random_suffix}",
        "lock_enabled": f"{bucket_prefix}-lock-enabled-{random_suffix}",
        "retention": f"{bucket_prefix}-retention-after-creation-{random_suffix}"
    }

    logger.info("Creating buckets with random suffix: %s", random_suffix)
    for bucket in buckets.values():
        logger.debug("Creating bucket: %s", bucket)
        s3_client.create_bucket(Bucket=bucket)

    logger.info("Enabling versioning on necessary buckets.")
    for bucket in ["lock_enabled", "retention"]:
        logger.debug("Enabling versioning for bucket: %s", buckets[bucket])
        s3_client.put_bucket_versioning(
            Bucket=buckets[bucket],
            VersioningConfiguration={
                'Status': 'Enabled'
            }
        )

    logger.info("Enabling object lock configuration on the lock-enabled bucket.")
    s3_client.put_object_lock_configuration(
        Bucket=buckets["lock_enabled"],
        ObjectLockConfiguration={
            'ObjectLockEnabled': 'Enabled'
        }
    )
    logger.info("Buckets created and configured successfully.")

    # Save the bucket names to a file
    with open('buckets.txt', 'w') as f:
        for name, bucket in buckets.items():
            f.write(f"{name}={bucket}\n")

    return buckets


def populate_buckets(s3_client, buckets):
    logger.info("Populating buckets with test files.")
    file_table = PrettyTable()
    file_table.field_names = ["Bucket", "File Name", "Content"]

    for bucket in buckets.values():
        for i in range(2):
            key = f"file{i}.txt"
            logger.debug("Uploading %s to bucket %s", key, bucket)
            s3_client.put_object(Bucket=bucket, Key=key, Body=file_content)
            file_table.add_row([bucket, key, file_content])

    logger.info("Buckets populated with test files successfully.")
    print(file_table)


def update_retention_policy(s3_client, bucket):
    s3_client.put_object_lock_configuration(
        Bucket=bucket,
        ObjectLockConfiguration={
            'ObjectLockEnabled': 'Enabled',
            'Rule': {
                'DefaultRetention': {
                    'Mode': 'GOVERNANCE',
                    'Years': 1
                }
            }
        }
    )
    logger.info("Retention policy updated successfully for bucket: %s", bucket)


def set_legal_hold(s3_client, bucket, key):
    logger.info("Setting legal hold on object %s in bucket %s", key, bucket)
    s3_client.put_object_legal_hold(
        Bucket=bucket,
        Key=key,
        LegalHold={
            'Status': 'ON'
        }
    )
    logger.info("Legal hold set successfully on object %s in bucket %s", key, bucket)


def set_retention(s3_client, bucket, key, days):
    from datetime import datetime, timedelta
    retain_until = (datetime.now() + timedelta(days=days)).strftime('%Y-%m-%dT%H:%M:%SZ')
    logger.info("Setting retention policy on object %s in bucket %s for %d days", key, bucket, days)
    logger.debug("Retention date: %s", retain_until)
    s3_client.put_object_retention(
        Bucket=bucket,
        Key=key,
        Retention={
            'Mode': 'GOVERNANCE',
            'RetainUntilDate': retain_until
        },
        BypassGovernanceRetention=True
    )
    logger.info("Retention policy set successfully on object %s in bucket %s", key, bucket)


def print_bucket_details(buckets):
    bucket_table = PrettyTable()
    bucket_table.field_names = ["Bucket Name", "Configuration"]
    bucket_table.add_row([buckets["no_lock"], "No Lock"])
    bucket_table.add_row([buckets["lock_enabled"], "Lock Enabled"])
    bucket_table.add_row([buckets["retention"], "Retention After Creation"])
    print(bucket_table)


# Example usage
if __name__ == "__main__":
    s3_client = boto3.client('s3')
    buckets = create_buckets(s3_client)
    print_bucket_details(buckets)
    populate_buckets(s3_client, buckets)
    update_retention_policy(s3_client, buckets["retention"])
    set_legal_hold(s3_client, buckets["lock_enabled"], "file0.txt")
    set_retention(s3_client, buckets["lock_enabled"], "file1.txt", 30)
