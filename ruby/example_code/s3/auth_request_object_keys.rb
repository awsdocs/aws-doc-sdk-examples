# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Prerequisites:
#   - An Amazon S3 bucket.

# snippet-start:[s3.ruby.auth_request_object_keys.rb]
require 'aws-sdk-s3'

# Prints the list of objects in an Amazon S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The bucket's name.
# @return [Boolean] true if all operations succeed; otherwise, false.
# @example
#   s3_client = Aws::S3::Client.new(region: 'us-east-1')
#   exit 1 unless list_bucket_objects?(s3_client, 'my-bucket')
def list_bucket_objects?(s3_client, bucket_name)
  puts "Accessing the bucket named '#{bucket_name}'..."
  objects = s3_client.list_objects_v2(
    bucket: bucket_name,
    max_keys: 50
  )

  if objects.count.positive?
    puts 'The object keys in this bucket are (first 50 objects):'
    objects.contents.each do |object|
      puts object.key
    end
  else
    puts 'No objects found in this bucket.'
  end

  return true
rescue StandardError => e
  puts "Error while accessing the bucket named '#{bucket_name}': #{e.message}"
  return false
end
# snippet-end:[s3.ruby.auth_request_object_keys.rb]

# Full example call:
=begin
region = 'us-east-1'
bucket_name = 'my-bucket'
s3_client = Aws::S3::Client.new(region: region)

exit 1 unless list_bucket_objects?(s3_client, bucket_name)
=end
