#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Ruby]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon S3]
#snippet-service:[s3]
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

profile_name = 'david'
region = 'us-east-1'
bucket = 'doc-sample-bucket'

# S3 - Configuring an S3 Bucket

# Create a S3 client
s3 = Aws::S3::Client.new(profile: profile_name, region:region)

# Setting a Bucket CORS Configuration

# Create array of allowed methods parameter based on command line parameters
allowed_methods = []
ARGV.each do |arg|
  case arg.upcase
  when "POST"
    allowed_methods << "POST"
  when "GET"
    allowed_methods << "GET"
  when "PUT"
    allowed_methods << "PUT"
  when "PATCH"
    allowed_methods << "PATCH"
  when "DELETE"
    allowed_methods << "DELETE"
  when "HEAD"
    allowed_methods << "HEAD"
  else
    puts "#{arg} is not a valid HTTP method"
  end
end

# Create CORS configuration hash
cors_configuration = {
  cors_rules: [
    {
      allowed_methods: allowed_methods,
      allowed_origins: ["*"],
      expose_headers: ["ExposeHeader"],
    },
  ]
}

# Set the new CORS configuration on the selected bucket
s3.put_bucket_cors(
  bucket: bucket,
  cors_configuration: cors_configuration
)

# Retrieving a Bucket CORS Configuration
resp = s3.get_bucket_cors(bucket: bucket)
puts resp.cors_rules

# To run the example, type the following at the command line including one or more HTTP methods as shown
# ruby doc_sample_code_s3_bucket_cors.rb get post
