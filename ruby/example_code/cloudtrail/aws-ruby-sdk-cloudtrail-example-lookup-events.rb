# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-cloudtrail-example-lookup-events.rb demonstrates how to look up
#  AWS CloudTrail trail events using the AWS SDK for Ruby.


# snippet-start:[cloudtrail.Ruby.lookupEvents]

require "aws-sdk-cloudtrail"  # v2: require 'aws-sdk'

def show_event(event)
  puts "\nEvent name:   " + event.event_name.to_s
  puts "Event ID:     " + event.event_id.to_s
  puts "Event time:   #{event.event_time}"
  puts "User name:    " + event.username.to_s

  puts "Resources:"

  event.resources.each do |r|
    puts "  Name:       " + r.resource_name
    puts "  Type:       " + r.resource_type
    puts ""
  end
end

# Create client in us-west-2.
# Replace us-west-2 with the AWS Region you're using for AWS CloudTrail.
client = Aws::CloudTrail::Client.new(region: "us-west-2")

resp = client.lookup_events

puts
puts "Found #{resp.events.count} events in us-west-2:"
puts

resp.events.each do |e|
  show_event(e)
end
# snippet-end:[cloudtrail.Ruby.lookupEvents]
