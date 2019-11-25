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
# snippet-sourcesyntax:[python]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[list_buckets]
# snippet-keyword:[create_bucket]
# snippet-keyword:[delete_bucket]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2017-01-19]
# snippet-sourceauthor:[AWS]
# snippet-start:[s3.python.bucket_operations.list_create_delete]
import argparse
import boto3
import sys
from botocore.exceptions import ClientError


def list_my_buckets(s3):
    '''
    Helper function to list all s3 buckets.
    '''
    print("Buckets:\n", *[b.name for b in s3.buckets.all()], sep="\n\t")


parser = argparse.ArgumentParser()
parser.add_argument('bucket_name', help='The name of the bucket to create.')
parser.add_argument('region', help='The region in which to create your bucket.')
parser.add_argument('--cleanup', help='Deletes the created bucket before exiting.', action='store_true')

args = parser.parse_args()

s3 = boto3.resource('s3', region_name=args.region)

list_my_buckets(s3)

try:
    print(f"\nCreating new bucket: {args.bucket_name}\n")
    bucket = s3.create_bucket(
        Bucket=args.bucket_name,
        CreateBucketConfiguration={
            'LocationConstraint': args.region
        }
    )
except ClientError as e:
    print(e)
    sys.exit('Exiting the script because bucket creation failed.')

bucket.wait_until_exists()
list_my_buckets(s3)

if args.cleanup:
    print(f"\nDeleting: {bucket.name}\n")
    bucket.delete()

    bucket.wait_until_not_exists()
    list_my_buckets(s3)
# snippet-end:[s3.python.bucket_operations.list_create_delete]