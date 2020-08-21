# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# snippet-start:[s3.ruby.auth_request_object_keys.rb]
require 'aws-sdk-s3'

# Prints the list of objects in the specified Amazon S3 bucket.
#
# @param s3 [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The bucket's name.
# @return [Boolean] true if all operations succeed; otherwise, false.
# @example
#   s3 = Aws::S3::Client.new(region: 'us-east-1')
#   unless can_list_bucket_objects?(s3, 'my-bucket')
#     exit 1
#   end
def can_list_bucket_objects?(s3, bucket_name)
  puts "Accessing the bucket named '#{bucket_name}'..."
  objects = s3.list_objects_v2(
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

# Full example:
=begin
region = 'us-east-1'
bucket_name = 'my-bucket'
s3 = Aws::S3::Client.new(region: region)

unless can_list_bucket_objects?(s3, bucket_name)
  exit 1
end
=end
