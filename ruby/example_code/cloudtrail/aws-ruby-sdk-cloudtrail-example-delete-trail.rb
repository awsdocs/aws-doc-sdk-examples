# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-cloudtrail-example-delete-trail.rb demonstrates how to delete
# an AWS CloudTrail trail using the AWS SDK for Ruby.

# snippet-start:[cloudtrail.Ruby.deleteTrail]

require 'aws-sdk-cloudtrail'  # v2: require 'aws-sdk'

if ARGV.length != 1
  puts 'You must supply the name of the trail to delete'
  exit 1
end

name = ARGV[0]

# Create client in us-west-2
# Replace us-west-2 with the AWS Region you're using for AWS CloudTrail.
client = Aws::CloudTrail::Client.new(region: 'us-west-2')

begin
  client.delete_trail({
    name: name, # required
  })

  puts 'Successfully deleted CloudTrail ' + name + ' in us-west-2'
rescue StandardError => err
  puts 'Got error trying to delete trail ' + name + ':'
  puts err
  exit 1
end
# snippet-end:[cloudtrail.Ruby.deleteTrail]
