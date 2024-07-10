import boto3
from setup import create_buckets, populate_buckets, update_retention_policy

def deploy_s3_object_locking():
    s3_client = boto3.client('s3')
    buckets = create_buckets(s3_client)
    update_retention_policy(s3_client, buckets["retention"])
    populate_buckets(s3_client, buckets)
    print("Buckets created and populated successfully!")
    return buckets
