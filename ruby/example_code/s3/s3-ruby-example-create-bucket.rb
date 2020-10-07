# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'
require 'securerandom'

# Creates an Amazon Simple Storage Service (Amazon S3) bucket.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param bucket_name [String] The bucket's name.
# @return [Boolean] true if the bucket was created; otherwise, false.
# @example
#   exit 1 unless bucket_created?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket'
#   )
def bucket_created?(s3_client, bucket_name)
  s3_client.create_bucket(bucket: bucket_name)
  return true
rescue StandardError => e
  puts "Error creating bucket: #{e.message}"
  return false
end

# Full example call:
def run_me
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)
  bucket_name = 'bucket-' + SecureRandom.uuid

  if bucket_created?(s3_client, bucket_name)
    puts "Bucket '#{bucket_name}' created."
  else
    puts "Bucket '#{bucket_name}' not created."
    exit 1
  end
end

run_me if $PROGRAM_NAME == __FILE__
