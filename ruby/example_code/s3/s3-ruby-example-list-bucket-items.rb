# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Lists the objects in an Amazon Simple Storage Service (Amazon S3) bucket.
#
# Prerequisites:
#
# - An S3 bucket, preferrably containing at least one object.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param bucket_name [String] The name of the bucket.
# @param max_objects [Integer] The maximum number of objects to list. The
#   number must be between 1 and 1,000. If not specified, only up to the
#   first 50 objects will be listed.
# @example
#   list_bucket_objects(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     100
#   )
def list_bucket_objects(s3_client, bucket_name, max_objects = 50)
  if max_objects < 1 || max_objects > 1000
    puts 'Maximum number of objects to request must be between 1 and 1,000.'
    return
  end

  objects = s3_client.list_objects_v2(
    bucket: bucket_name,
    max_keys: max_objects
  ).contents

  if objects.count.zero?
    puts "No objects in bucket '#{bucket_name}'."
    return
  else
    if objects.count == max_objects
      puts "First #{objects.count} objects in bucket '#{bucket_name}':"
    else
      puts "Objects in bucket '#{bucket_name}':"
    end
    objects.each do |object|
      puts object.key
    end
  end
rescue StandardError => e
  puts "Error accessing bucket '#{bucket_name}' " \
    "or listing its objects: #{e.message}"
end

# Full example call:
def run_me
  bucket_name = 'doc-example-bucket'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  list_bucket_objects(s3_client, bucket_name)
end

run_me if $PROGRAM_NAME == __FILE__
