# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[s3.py demonstrates how to list, create, and delete a bucket in Amazon S3.]
# snippet-service:[s3]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[list_buckets]
# snippet-keyword:[create_bucket]
# snippet-keyword:[delete_bucket]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-12-04]
# snippet-sourceauthor:[AWS-C9]

# ABOUT THIS PYTHON SAMPLE: This sample is part of the SDK for Cloud9 User Guide topic at
# https://docs.aws.amazon.com/cloud9/latest/user-guide/sample-python.html

# snippet-start:[s3.python.bucket_operations.list_create_delete]
import boto3
import sys
from botocore.exceptions import ClientError


def list_my_buckets(s3):
    print('Buckets:\n\t', *[b.name for b in s3.buckets.all()], sep="\n\t")


def create_and_delete_my_bucket(bucket_name, region, keep_bucket):
    s3 = boto3.resource('s3', region_name=region)

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
    parser.add_argument('--keep_bucket', help='Keeps the created bucket. When not specified, the bucket is deleted.',
                        action='store_true')

    args = parser.parse_args()

    create_and_delete_my_bucket(args.bucket_name, args.region, args.keep_bucket)


if __name__ == '__main__':
    main()
# snippet-end:[s3.python.bucket_operations.list_create_delete]
