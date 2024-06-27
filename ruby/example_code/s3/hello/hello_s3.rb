# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[ruby.hello_s3]

require 'aws-sdk-s3'

# Create an S3 client using the default AWS SDK configuration
s3 = Aws::S3::Client.new

begin
  # List the S3 buckets in your account
  response = s3.list_buckets
  
  # Print the name of each bucket
  puts "Here are the buckets in your account:"
  response.buckets.each do |bucket|
    puts "- #{bucket.name}"
  end
  
  # If there are no buckets, let the user know
  if response.buckets.empty?
    puts "You don't have any S3 buckets yet."
  end
  
rescue Aws::Errors::ServiceError => e
  puts "Encountered an error while listing buckets: #{e.message}"
end

# snippet-end:[ruby.hello_s3]
