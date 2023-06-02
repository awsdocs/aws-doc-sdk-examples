import boto3
from botocore.exceptions import ClientError

def delete_bucket(bucket_name):
    s3 = boto3.resource('s3')

    bucket = s3.Bucket(bucket_name)
    try:
        # Delete all objects within the bucket
        bucket.objects.all().delete()

        # Delete the bucket
        bucket.delete()
        print(f"Deleted bucket: {bucket_name}")
    except ClientError as e:
        if e.response['Error']['Code'] == 'BucketNotEmpty':
            print(f"Bucket: {bucket_name} is not empty. Deleting objects within the bucket...")
            delete_bucket_contents(bucket_name)

def delete_bucket_contents(bucket_name):
    s3 = boto3.resource('s3')

    bucket = s3.Bucket(bucket_name)
    bucket.objects.all().delete()

    delete_bucket(bucket_name)

def delete_buckets_with_prefix(prefix):
    s3 = boto3.client('s3')

    response = s3.list_buckets()

    for bucket in response['Buckets']:
        if bucket['Name'].startswith(prefix):
            try:
                delete_bucket(bucket['Name'])
            except ClientError as e:
                print(f"Error deleting bucket: {bucket['Name']}")
                print(e)

    print("Deletion completed.")

delete_buckets_with_prefix('doc-example-bucket-')
