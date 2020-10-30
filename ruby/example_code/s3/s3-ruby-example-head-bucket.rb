# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-s3'

# Determines whether an Amazon Simple Storage Service (Amazon S3)
#   bucket exists and you have permission to access it.
#
# Prerequisites:
#
# - An S3 bucket.
#
# @param
# @param
# @return [Boolean] true if the bucket exists and you have permission to
#   access it; otherwise, false.
# @example
#   exit 1 unless bucket_exists_and_accessible?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket'
#   )
def bucket_exists_and_accessible?(s3_client, bucket_name)
  s3_client.head_bucket(bucket: bucket_name)
  return true
rescue StandardError
  return false
end

# Full example call:
def run_me
  bucket_name = 'doc-example-bucket'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  if bucket_exists_and_accessible?(s3_client, bucket_name)
    puts "Bucket '#{bucket_name}' exists and is accessible to you."
  else
    puts "Bucket '#{bucket_name}' does not exist " \
      'or is not accessible to you.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
