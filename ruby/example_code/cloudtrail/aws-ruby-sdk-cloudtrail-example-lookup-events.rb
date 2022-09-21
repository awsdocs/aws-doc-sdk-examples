# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Demonstrates how to look up AWS CloudTrail trail events

# snippet-start:[cloudtrail.Ruby.lookupEvents]
require "aws-sdk-cloudtrail"  # v2: require 'aws-sdk'

def show_event(event)
  puts "Event name:   " + event.event_name
  puts "Event ID:     " + event.event_id
  puts "Event time:   #{event.event_time}"
  puts "Resources:"

  event.resources.each do |r|
    puts "  Name:       " + r.resource_name
    puts "  Type:       " + r.resource_type
    puts ""
  end
end

client = Aws::CloudTrail::Client.new

resp = client.lookup_events
puts "Found #{resp.events.count} events:"
resp.events.each do |e|
  show_event(e)
end
# snippet-end:[cloudtrail.Ruby.lookupEvents]
