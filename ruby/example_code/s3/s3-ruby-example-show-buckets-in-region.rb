# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Checks to see which Amazon Simple Storage Service (Amazon S3)
#   buckets accessible to you and were initially created with the target
#   AWS Region specified.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param region [String] The Region to check.
# @example
#   list_accessible_buckets_in_region(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'us-east-1'
#   )
def list_accessible_buckets_in_region(s3_client, region)
  buckets = s3_client.list_buckets.buckets
  buckets_in_region = []
  buckets.each do |bucket|
    bucket_region = s3_client.get_bucket_location(
      bucket: bucket.name
    ).location_constraint
    if bucket_region == region
      buckets_in_region << bucket.name
    end
  end
  if buckets_in_region.count.zero?
    puts "No buckets accessible to you and also set to region '#{region}' " \
      'when initially created.'
    exit 1
  else
    puts "Buckets accessible to you and also set to region '#{region}' " \
      'when initially created:'
    buckets_in_region.each do |bucket_name|
      puts bucket_name
    end
  end
rescue StandardError => e
  puts "Error getting information about buckets: #{e.message}"
  exit 1
end

# Full example call:
def run_me
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  list_accessible_buckets_in_region(s3_client, region)
end

run_me if $PROGRAM_NAME == __FILE__
