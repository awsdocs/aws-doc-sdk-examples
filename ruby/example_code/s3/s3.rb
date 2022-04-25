# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
# s3.rb demonstrates how to list, create, and delete a bucket in Amazon Simple Storage Service (Amazon S3).

# snippet-start:[s3.ruby.bucket_operations.list_create_delete]
require "aws-sdk"
# Replace us-west-2 with the AWS Region you're using for Amazon S3.
if ARGV.length < 2
  puts "Usage: ruby s3.rb <the bucket name> <the AWS Region to use>\n" +
    "Example: ruby s3.rb my-test-bucket us-west-2"
end

bucket_name = ARGV[0]
region = ARGV[1]
s3 = Aws::S3::Client.new(region: region)

# Lists all of your available buckets in this AWS Region.
def list_my_buckets(s3)
  resp = s3.list_buckets
  puts "My buckets now are:\n\n"

  resp.buckets.each do |bucket|
    puts bucket.name
  end

end

list_my_buckets(s3)

# Create a new bucket.
begin
  puts "\nCreating a new bucket named '#{bucket_name}'...\n\n"
  s3.create_bucket({
    bucket: bucket_name,
    create_bucket_configuration: {
      location_constraint: region
    }
  })

  s3.wait_until(:bucket_exists, {bucket: bucket_name, })
rescue Aws::S3::Errors::BucketAlreadyExists
  puts "Cannot create the bucket. " +
    "A bucket with the name '#{bucket_name}' already exists. Exiting."
  exit(false)
end

list_my_buckets(s3)

# Delete the bucket you just created.
puts "\nDeleting the bucket named '#{bucket_name}'...\n\n"
s3.delete_bucket(bucket: bucket_name)

s3.wait_until(:bucket_not_exists, {bucket: bucket_name, })

list_my_buckets(s3)
# snippet-end:[s3.ruby.bucket_operations.list_create_delete]
