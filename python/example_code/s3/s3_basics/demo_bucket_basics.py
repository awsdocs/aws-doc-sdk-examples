# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Demonstrate create, list, and delete buckets in Amazon S3.

This example is part of the AWS Cloud9 User Guide topic at
https://docs.aws.amazon.com/cloud9/latest/user-guide/sample-python.html
"""


# snippet-start:[s3.python.bucket_operations.list_create_delete]
import sys
import boto3
from botocore.exceptions import ClientError


def list_my_buckets(s3_resource):
    print('Buckets:\n\t', *[b.name for b in s3_resource.buckets.all()], sep="\n\t")


def create_and_delete_my_bucket(s3_resource, bucket_name, keep_bucket):
    list_my_buckets(s3_resource)

    try:
        print('\nCreating new bucket:', bucket_name)
        bucket = s3_resource.create_bucket(
            Bucket=bucket_name,
            CreateBucketConfiguration={
                'LocationConstraint': s3_resource.meta.client.meta.region_name
            }
        )
    except ClientError as e:
        print(f"Couldn't create a bucket for the demo. Here's why: "
              f"{e.response['Error']['Message']}")
        raise

    bucket.wait_until_exists()
    list_my_buckets(s3_resource)

    if not keep_bucket:
        print('\nDeleting bucket:', bucket.name)
        bucket.delete()

        bucket.wait_until_not_exists()
        list_my_buckets(s3_resource)
    else:
        print('\nKeeping bucket:', bucket.name)


def main():
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('bucket_name', help='The name of the bucket to create.')
    parser.add_argument('region', help='The region in which to create your bucket.')
    parser.add_argument('--keep_bucket', help='Keeps the created bucket. When not '
                                              'specified, the bucket is deleted '
                                              'at the end of the demo.',
                        action='store_true')

    args = parser.parse_args()
    s3_resource = (
        boto3.resource('s3', region_name=args.region) if args.region
        else boto3.resource('s3'))
    try:
        create_and_delete_my_bucket(s3_resource, args.bucket_name, args.keep_bucket)
    except ClientError:
        print('Exiting the demo.')


if __name__ == '__main__':
    main()
# snippet-end:[s3.python.bucket_operations.list_create_delete]
