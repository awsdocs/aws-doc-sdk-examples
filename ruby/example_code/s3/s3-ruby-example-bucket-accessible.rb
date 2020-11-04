# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Checks whether an Amazon Simple Storage Service (Amazon S3) bucket
#   exists in an AWS Region.
#
# Prerequisites:
#
# - An S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param bucket_name [String] The name of the bucket.
# @param region [Array] The Region to check for.
# @return [Boolean] true if the bucket exists in the specified Region;
#   otherwise, false.
# @example
#   exit 1 unless bucket_in_region?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'us-east-1'
#   )
def bucket_in_region?(s3_client, bucket_name, region)
  location = s3_client.get_bucket_location(bucket: bucket_name).location_constraint
  if region == location
    return true
  else
    return false
  end
rescue StandardError => e
  puts "Error accessing bucket: #{e.message}"
  return false
end

# Full example call:
def run_me
  bucket_name = 'doc-example-bucket'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  if bucket_in_region?(s3_client, bucket_name, region)
    puts "Bucket '#{bucket_name}' exists in Region '#{region}'."
  else
    puts "Bucket '#{bucket_name}' does not exist, " \
      "does not exist in AWS Region '#{region}', " \
      'the Region was not specified when the bucket was initially created, ' \
      'or the bucket is not otherwise accessible to you.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
