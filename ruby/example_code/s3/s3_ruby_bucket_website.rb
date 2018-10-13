#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Ruby]
#snippet-keyword:[Code Sample]
#snippet-service:[Amazon S3]
#snippet-sourcetype:[<<snippet or full-example>>]
#snippet-sourcedate:[]
#snippet-sourceauthor:[AWS]
# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

require 'aws-sdk-s3'  # v2: require 'aws-sdk'

# Using Random UUIDs to Avoid Collisions when Testing
require 'securerandom'
bucket = "example-test-bucket-#{SecureRandom.uuid}"

# Setup
s3 = Aws::S3::Client.new(region: "us-west-2")
s3.create_bucket(bucket: bucket)

# When Bucket Has No Website Configuration
begin
  s3.get_bucket_website(bucket: bucket)
rescue Aws::S3::Errors::NoSuchWebsiteConfiguration
  puts "No bucket website configuration present."
end

# Adding Simple Pages & Website Configuration
s3.put_object(
  bucket: bucket,
  key: "index.html",
  body: "Hello, Amazon S3!",
  acl: "public-read"
)
s3.put_object(
  bucket: bucket,
  key: "error.html",
  body: "Page not found!",
  acl: "public-read"
)
s3.put_bucket_website(
  bucket: bucket,
  website_configuration: {
    index_document: {
      suffix: "index.html"
    },
    error_document: {
      key: "error.html"
    }
  }
)

# Accessing as a Website
index_path = "http://#{bucket}.s3-website-us-west-2.amazonaws.com/"
error_path = "http://#{bucket}.s3-website-us-west-2.amazonaws.com/nonexistant.html"

puts "Index Page Contents:\n#{Net::HTTP.get(URI(index_path))}\n\n"
puts "Error Page Contents:\n#{Net::HTTP.get(URI(error_path))}\n\n"

# Removing Website Configuration
s3.delete_bucket_website(bucket: bucket)

# Cleanup
b = Aws::S3::Resource.new(region: "us-west-2").bucket(bucket)
b.delete! # Recursively deletes objects as well.
