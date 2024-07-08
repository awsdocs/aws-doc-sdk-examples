import boto3
import logging
import coloredlogs
from prettytable import PrettyTable

# Configure logging
logger = logging.getLogger(__name__)
coloredlogs.install(level='DEBUG', logger=logger, fmt='%(asctime)s [%(levelname)s] %(message)s')


def remove_object_locks(s3_client, bucket, key, version_id):
    try:
        logger.debug("Removing retention for %s in bucket %s, version %s", key, bucket, version_id)
        s3_client.put_object_retention(
            Bucket=bucket,
            Key=key,
            VersionId=version_id,
            Retention={
                'Mode': 'NONE'
            },
            BypassGovernanceRetention=True
        )
    except Exception as e:
        logger.error("Error removing retention for %s in %s: %s", key, bucket, str(e))
        return f"Error removing retention: {str(e)}"

    try:
        logger.debug("Removing legal hold for %s in bucket %s, version %s", key, bucket, version_id)
        s3_client.put_object_legal_hold(
            Bucket=bucket,
            Key=key,
            VersionId=version_id,
            LegalHold={
                'Status': 'OFF'
            }
        )
    except Exception as e:
        logger.error("Error removing legal hold for %s in %s: %s", key, bucket, str(e))
        return f"Error removing legal hold: {str(e)}"

    return "Success"


def clean_s3_object_locking():
    s3_client = boto3.client('s3')
    bucket_prefix = "py-object-locking"

    # Read bucket names from file
    buckets = []
    try:
        with open('buckets.txt', 'r') as f:
            for line in f:
                name, bucket = line.strip().split('=')
                buckets.append(bucket)
    except Exception as e:
        logger.error("Error reading bucket names from file: %s", str(e))
        return

    summary_table = PrettyTable()
    summary_table.field_names = ["Bucket", "Object", "Version ID", "Action", "Status"]

    error_table = PrettyTable()
    error_table.field_names = ["Bucket", "Object", "Version ID", "Error"]

    logger.info("Starting S3 Object Locking Cleanup")

    for bucket in buckets:
        try:
            logger.debug("Cleaning bucket: %s", bucket)
            objects = s3_client.list_object_versions(Bucket=bucket)
            for obj in objects.get('Versions', []):
                result = remove_object_locks(s3_client, bucket, obj['Key'], obj['VersionId'])
                if result == "Success":
                    s3_client.delete_object(Bucket=bucket, Key=obj['Key'], VersionId=obj['VersionId'])
                    summary_table.add_row([bucket, obj['Key'], obj['VersionId'], "Delete Object", "Success"])
                else:
                    error_table.add_row([bucket, obj['Key'], obj['VersionId'], result])
            for obj in objects.get('DeleteMarkers', []):
                try:
                    s3_client.delete_object(Bucket=bucket, Key=obj['Key'], VersionId=obj['VersionId'])
                    summary_table.add_row([bucket, obj['Key'], obj['VersionId'], "Delete Marker", "Success"])
                except Exception as e:
                    error_table.add_row([bucket, obj['Key'], obj['VersionId'], f"Error deleting marker: {str(e)}"])

            s3_client.delete_bucket(Bucket=bucket)
            logger.info("Deleted bucket: %s", bucket)
            summary_table.add_row([bucket, "-", "-", "Delete Bucket", "Success"])
        except Exception as e:
            logger.error("Error cleaning bucket %s: %s", bucket, str(e))
            error_table.add_row([bucket, "-", "-", f"Error: {str(e)}"])

    logger.info("Cleanup completed successfully!")
    print("\nSummary of Cleanup Actions:")
    print(summary_table)
    if len(error_table.rows) > 0:
        print("\nErrors Encountered During Cleanup:")
        print(error_table)


if __name__ == "__main__":
    clean_s3_object_locking()
