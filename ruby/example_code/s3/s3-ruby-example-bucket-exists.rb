# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# s3-ruby-example-bucket-exists.rb demonstrates how to check whether an Amazon Simple Storage Service
# (Amazon S3) bucket exists.

# snippet-start:[s3.ruby.s3-ruby-example-bucket-exists]

require 'aws-sdk-s3'

# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param bucket_name [String] The name of the bucket.
# @return [Boolean] true if the bucket exists; otherwise, false.
# @example
#   exit 1 unless bucket_exists?(
#     Aws::S3::Client.new(region: 'us-west-2'),
#     'doc-example-bucket'
#   )
def bucket_exists?(s3_client, bucket_name)
  response = s3_client.list_buckets
  response.buckets.each do |bucket|
    return true if bucket.name == bucket_name
  end
  return false
rescue StandardError => e
  puts "Error listing buckets: #{e.message}"
  return false
end

# Full example call:
# Replace us-west-2 with the AWS Region you're using for Amazon S3.
def run_me
  bucket_name = 'doc-example-bucket'
  region = 'us-west-2'
  s3_client = Aws::S3::Client.new(region: region)

  if bucket_exists?(s3_client, bucket_name)
    puts 'Bucket exists.'
  else
    puts 'Bucket does not exist or is not accessible to you.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[s3.ruby.s3-ruby-example-bucket-exists]
