# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-cloudtrail-example-describe-trails.rb demonstrates how to
# retrieve details about AWS CloudTrail trails using the AWS SDK for Ruby.


# snippet-start:[cloudtrail.Ruby.describeTrails]
require "aws-sdk-cloudtrail"  # v2: require 'aws-sdk'

client = Aws::CloudTrail::Client.new
resp = client.describe_trails({})
puts "Found #{resp.trail_list.count} trail(s) in REGION:"

resp.trail_list.each do |trail|
  puts "Name:           " + trail.name
  puts "S3 bucket name: " + trail.s3_bucket_name
  puts
end
# snippet-end:[cloudtrail.Ruby.describeTrails]
