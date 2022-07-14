# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-cloudtrail-example-describe-trails.rb demonstrates how to
# retrieve details about AWS CloudTrail trails using the AWS SDK for Ruby.


# snippet-start:[cloudtrail.Ruby.describeTrails]
require "aws-sdk-cloudtrail"  # v2: require 'aws-sdk'

# Create client in us-west-2.
# Replace us-west-2 with the AWS Region you're using for AWS CloudTrail.

client = Aws::CloudTrail::Client.new(region: "REGION")

resp = client.describe_trails({})

puts
puts "Found #{resp.trail_list.count} trail(s) in REGION:"
puts

resp.trail_list.each do |trail|
  puts "Name:           " + trail.name
  puts "S3 bucket name: " + trail.s3_bucket_name
  puts
end
# snippet-end:[cloudtrail.Ruby.describeTrails]
