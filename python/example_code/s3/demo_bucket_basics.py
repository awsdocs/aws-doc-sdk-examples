# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose
    Demonstrate create, list, and delete buckets in Amazon S3.

    This example is part of the AWS Cloud9 User Guide topic at
    https://docs.aws.amazon.com/cloud9/latest/user-guide/sample-python.html

Running the code
    Run this demo from the command line. To get command line usage, run the
    following command.

        python -m demo_bucket_basics -h

Additional information
    Running this code might result in charges to your AWS account.
"""


# snippet-start:[s3.python.bucket_operations.list_create_delete]
import sys
import boto3
from botocore.exceptions import ClientError


def get_s3(region=None):
    return boto3.resource('s3', region_name=region) if region else boto3.resource('s3')


def list_my_buckets(s3):
    print('Buckets:\n\t', *[b.name for b in s3.buckets.all()], sep="\n\t")


def create_and_delete_my_bucket(bucket_name, region, keep_bucket):
    s3 = get_s3(region)

    list_my_buckets(s3)

    try:
        print('\nCreating new bucket:', bucket_name)
        bucket = s3.create_bucket(
            Bucket=bucket_name,
            CreateBucketConfiguration={
                'LocationConstraint': region
            }
        )
    except ClientError as e:
        print(e)
        sys.exit('Exiting the script because bucket creation failed.')

    bucket.wait_until_exists()
    list_my_buckets(s3)

    if not keep_bucket:
        print('\nDeleting bucket:', bucket.name)
        bucket.delete()

        bucket.wait_until_not_exists()
        list_my_buckets(s3)
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

    create_and_delete_my_bucket(args.bucket_name, args.region, args.keep_bucket)


if __name__ == '__main__':
    main()
# snippet-end:[s3.python.bucket_operations.list_create_delete]
