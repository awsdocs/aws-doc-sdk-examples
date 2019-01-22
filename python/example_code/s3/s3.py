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
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[list_buckets]
# snippet-keyword:[create_bucket]
# snippet-keyword:[delete_bucket]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2017-01-19]
# snippet-sourceauthor:[AWS]
# snippet-start:[s3.python.bucket_operations.list_create_delete]
import boto3
import sys
import botocore

if len(sys.argv) < 3:
  print('Usage: python s3.py <the bucket name> <the AWS Region to use>\n' +
    'Example: python s3.py my-test-bucket us-east-2')
  sys.exit()

bucket_name = sys.argv[1]
region = sys.argv[2]

s3 = boto3.client(
  's3',
  region_name = region
)

# Lists all of your available buckets in this AWS Region.
def list_my_buckets(s3):
  resp = s3.list_buckets()

  print('My buckets now are:\n')

  for bucket in resp['Buckets']:
    print(bucket['Name'])

  return

list_my_buckets(s3)

# Create a new bucket.
try:
  print("\nCreating a new bucket named '" + bucket_name + "'...\n")
  s3.create_bucket(Bucket = bucket_name,
    CreateBucketConfiguration = {
      'LocationConstraint': region
    }
  )
except botocore.exceptions.ClientError as e:
  if e.response['Error']['Code'] == 'BucketAlreadyExists':
    print("Cannot create the bucket. A bucket with the name '" +
      bucket_name + "' already exists. Exiting.")
  sys.exit()

list_my_buckets(s3)

# Delete the bucket you just created.
print("\nDeleting the bucket named '" + bucket_name + "'...\n")
s3.delete_bucket(Bucket = bucket_name)

list_my_buckets(s3)
# snippet-end:[s3.python.bucket_operations.list_create_delete]