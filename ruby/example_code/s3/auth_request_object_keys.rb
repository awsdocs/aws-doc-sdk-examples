# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# snippet-start:[s3.ruby.auth_request_object_keys.rb]
require 'aws-sdk-s3'

# Prints the list of objects in the specified Amazon S3 bucket.
# @param bucket_name [String] The bucket's name.
# @param region [String] The bucket's AWS Region.
# @return [Boolean] true if all operations succeed; otherwise, false.
# @example
#   list_bucket_objects('my-bucket', 'us-east-1')
def list_bucket_objects(bucket_name, region)
  outcome = false
  begin
    s3 = Aws::S3::Resource.new(region: region)
    puts "Accessing the bucket named '#{bucket_name}'..."
    objects = s3.bucket(bucket_name).objects

    if objects.count.positive?
      puts 'The object keys in this bucket are (first 50 objects):'
      objects.limit(50).each do |object|
        puts object.key
      end
    else
      puts 'No objects found in this bucket.'
    end

    outcome = true
  rescue StandardError => e
    puts "Error while accessing the bucket named '#{bucket_name}': #{e.message}"
  end
  return outcome
end
# snippet-end:[s3.ruby.auth_request_object_keys.rb]
