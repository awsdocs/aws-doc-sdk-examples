import boto3
import logging
import coloredlogs
from prettytable import PrettyTable
from setup import set_legal_hold, set_retention

# Configure logging
logger = logging.getLogger(__name__)
coloredlogs.install(level='DEBUG', logger=logger, fmt='%(asctime)s [%(levelname)s] %(message)s')

def read_bucket_names():
    buckets = {}
    with open('buckets.txt', 'r') as f:
        for line in f:
            name, bucket = line.strip().split('=')
            buckets[name] = bucket
    return buckets

def demo_s3_object_locking():
    s3_client = boto3.client('s3')

    # Read bucket names from file
    buckets = read_bucket_names()
    lock_enabled_bucket = buckets["lock_enabled"]
    retention_bucket = buckets["retention"]

    logger.info("Starting S3 Object Locking Demo")

    # Set legal hold on an object in the lock-enabled bucket
    logger.info("Setting legal hold on file0.txt in bucket: %s", lock_enabled_bucket)
    set_legal_hold(s3_client, lock_enabled_bucket, "file0.txt")
    logger.info("Legal hold set on file0.txt in %s.", lock_enabled_bucket)

    # Set retention period on an object in the lock-enabled bucket
    logger.info("Setting retention period on file1.txt in bucket: %s for 1 day", lock_enabled_bucket)
    set_retention(s3_client, lock_enabled_bucket, "file1.txt", 1)
    logger.info("Retention period set on file1.txt in %s for 1 day.", lock_enabled_bucket)

    # Set legal hold on an object in the retention bucket
    logger.info("Setting legal hold on file0.txt in bucket: %s", retention_bucket)
    set_legal_hold(s3_client, retention_bucket, "file0.txt")
    logger.info("Legal hold set on file0.txt in %s.", retention_bucket)

    # Set retention period on an object in the retention bucket
    logger.info("Setting retention period on file1.txt in bucket: %s for 1 day", retention_bucket)
    set_retention(s3_client, retention_bucket, "file1.txt", 1)
    logger.info("Retention period set on file1.txt in %s for 1 day.", retention_bucket)

    # Create and print summary table
    summary_table = PrettyTable()
    summary_table.field_names = ["Bucket", "File Name", "Action", "Details"]
    summary_table.add_row([lock_enabled_bucket, "file0.txt", "Legal Hold", "Status: ON"])
    summary_table.add_row([lock_enabled_bucket, "file1.txt", "Retention", "Days: 1"])
    summary_table.add_row([retention_bucket, "file0.txt", "Legal Hold", "Status: ON"])
    summary_table.add_row([retention_bucket, "file1.txt", "Retention", "Days: 1"])

    print("\nSummary of Actions:")
    print(summary_table)

if __name__ == "__main__":
    demo_s3_object_locking()
