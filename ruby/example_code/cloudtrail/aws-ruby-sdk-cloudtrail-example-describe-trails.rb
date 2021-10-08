# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-cloudtrail-example-describe-trails.rb demonstrates how to
# retrieve details about your an Amazon CloudTrail trails using the AWS SDK for Ruby.

# Inputs:
# - REGION - The AWS Region.

# snippet-start:[cloudtrail.Ruby.createTrail]
require 'aws-sdk-cloudtrail'  # v2: require 'aws-sdk'

# Create client in us-west-2
client = Aws::CloudTrail::Client.new(region: 'REGION')

resp = client.describe_trails({})

puts
puts "Found #{resp.trail_list.count} trail(s) in REGION:"
puts

resp.trail_list.each do |trail|
  puts 'Name:           ' + trail.name
  puts 'S3 bucket name: ' + trail.s3_bucket_name
  puts
end
# snippet-start:[cloudtrail.Ruby.createTrail]
