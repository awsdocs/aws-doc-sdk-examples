# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Demonstrates how to delete an AWS CloudTrail trail

# snippet-start:[cloudtrail.Ruby.deleteTrail]

require "aws-sdk-cloudtrail"  # v2: require 'aws-sdk'

client = Aws::CloudTrail::Client.new

trail_name = "example-code-trail-9830"

begin
  client.delete_trail({
                        name: trail_name # required
                      })
  puts "Successfully deleted trail: " + trail_name
rescue StandardError => err
  puts "Got error trying to delete trail: " + trail_name + ":"
  puts err
  exit 1
end
# snippet-end:[cloudtrail.Ruby.deleteTrail]
