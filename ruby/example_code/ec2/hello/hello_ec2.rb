# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[ec2.ruby.describe_instances.rb]
require 'aws-sdk-ec2'

# Creates an EC2 resource
ec2 = Aws::EC2::Resource.new(region: 'us-west-2')

puts 'Listing instances'
instances = ec2.instances.map(&:to_h)

if instances.count.zero?
  puts 'You have no instances'
else
  instances.each do |instance|
    puts "Instance ID: #{instance[:instance_id]}"
    puts "Instance Type: #{instance[:instance_type]}"
    puts "Public IP: #{instance[:public_ip_address]}"
    puts "Public DNS Name: #{instance[:public_dns_name]}"
    puts "\n"
  end
end
# snippet-end:[ec2.ruby.describe_instances.rb]

