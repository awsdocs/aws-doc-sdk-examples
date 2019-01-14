# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[s3.rb demonstrates how to list, create, and delete a bucket in Amazon S3.]
# snippet-service:[s3]
# snippet-keyword:[Ruby]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[list_buckets]
# snippet-keyword:[create_bucket]
# snippet-keyword:[delete_bucket]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2017-01-18]
# snippet-sourceauthor:[AWS]
# snippet-start:[s3.ruby.bucket_operations.list_create_delete]
require 'aws-sdk'

if ARGV.length < 2
  puts "Usage: ruby s3.rb <the bucket name> <the AWS Region to use>\n" +
    "Example: ruby s3.rb my-test-bucket us-east-2"
end

bucket_name = ARGV[0]
region = ARGV[1]
s3 = Aws::S3::Client.new(region: region)

# Lists all of your available buckets in this AWS Region.
def list_my_buckets(s3)
  resp = s3.list_buckets()
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
rescue Aws::S3::Errors::BucketAlreadyExists
  puts "Cannot create the bucket. " +
    "A bucket with the name '#{bucket_name}' already exists. Exiting."
  exit(false)
end

list_my_buckets(s3)

# Delete the bucket you just created.
puts "\nDeleting the bucket named '#{bucket_name}'...\n\n"
s3.delete_bucket(bucket: bucket_name)

list_my_buckets(s3)
# snippet-end:[s3.ruby.bucket_operations.list_create_delete]
