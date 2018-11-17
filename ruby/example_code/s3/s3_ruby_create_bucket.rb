#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Lists your S3 buckets, creates a bucket, add an item to the bucket, copies and item to the bucket, and deletes items from a bucket.]
#snippet-keyword:[Amazon Simple Storage Service]
#snippet-keyword:[list_buckets method]
#snippet-keyword:[create_bucket method]
#snippet-keyword:[put_object method]
#snippet-keyword:[list_objects_v2 method]
#snippet-keyword:[copy_object method]
#snippet-keyword:[delete_objects method]
#snippet-keyword:[Ruby]
#snippet-service:[s3]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-03-16]
# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

require 'aws-sdk-s3'  # v2: require 'aws-sdk'
require 'json'

profile_name = 'david'
region = "us-east-1"
bucket = 'doc-sample-bucket'
my_bucket = 'david-cloud'

# S3

# Configure SDK
s3 = Aws::S3::Client.new(profile: profile_name, region: region)

# Display a List of Amazon S3 Buckets
resp = s3.list_buckets
resp.buckets.each do |bucket|
  puts bucket.name
end

# Create a S3 bucket from S3::client
s3.create_bucket(bucket: bucket)

# Upload a file to s3 bucket, directly putting string data
s3.put_object(bucket: bucket, key: "file1", body: "My first s3 object")

# Check the file exists
resp = s3.list_objects_v2(bucket: bucket)
resp.contents.each do |obj|
  puts obj.key
end

# Copy files from bucket to bucket
s3.copy_object(bucket: bucket,
               copy_source: "#{my_bucket}/test_file",
               key: 'file2')
s3.copy_object(bucket: bucket,
               copy_source: "#{my_bucket}/test_file1",
               key: 'file3')

# Delete multiple objects in a single HTTP request
s3.delete_objects(
  bucket: 'doc-sample-bucket',
  delete: {
    objects: [
      {
        key: 'file2'
      },
      {
        key: 'file3'
      }
    ]
  }
)

# Verify objects now have been deleted
resp = s3.list_objects_v2(bucket: bucket)
resp.contents.each do |obj|
  puts obj.key
end
