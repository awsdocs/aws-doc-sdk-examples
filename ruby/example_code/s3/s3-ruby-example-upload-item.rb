# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to upload an object to a bucket
# in Amazon Simple Storage Service (Amazon S3).

# snippet-start:[s3.ruby.s3-ruby-example-upload-item]

require 'aws-sdk-s3'

# Prerequisites:
#
# - An S3 bucket.
# - An object to upload to the bucket.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name of the object.
# @return [Boolean] true if the object was uploaded; otherwise, false.
# @example
#   exit 1 unless object_uploaded?(
#     Aws::S3::Client.new(region: 'us-west-2'),
#     'doc-example-bucket',
#     'my-file.txt'
#   )
def object_uploaded?(s3_client, bucket_name, object_key)
  response = s3_client.put_object(
    bucket: bucket_name,
    body: "c:\\my-file.txt",
    key: object_key
  )
  if response.etag
    return true
  else
    return false
  end
rescue StandardError => e
  puts "Error uploading object: #{e.message}"
  return false
end

# Full example call:
# Replace us-west-2 with the AWS Region you're using for Amazon S3.

def run_me
  bucket_name = 'doc-example-bucket'
  object_key = 'my-file.txt'
  region = 'us-west-2'
  s3_client = Aws::S3::Client.new(region: region)

  if object_uploaded?(s3_client, bucket_name, object_key)
    puts "Object '#{object_key}' uploaded to bucket '#{bucket_name}'."
  else
    puts "Object '#{object_key}' not uploaded to bucket '#{bucket_name}'."
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[s3.ruby.s3-ruby-example-upload-item]
