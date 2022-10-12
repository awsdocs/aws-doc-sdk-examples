# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# ec2-ruby-example-reboot-instance-i-123abc.rb demonstrates how to reboot
# an Amazon Elastic Compute Cloud (Amazon EC2) instance using the AWS SDK for Ruby.

# snippet-start:[ec2.Ruby.rebootInstances]

require "aws-sdk-ec2"

# Prerequisites:
#
# - An Amazon EC2 instance.
#
# @param ec2_client [Aws::EC2::Client] An initialized EC2 client.
# @param instance_id [String] The ID of the instance.
# @example
#   request_instance_reboot(
#     Aws::EC2::Resource.new(region: 'us-west-2'),
#     'i-123abc'
#   )
def request_instance_reboot(ec2_client, instance_id)
  response = ec2_client.describe_instances(instance_ids: [instance_id])
  if response.count.zero?
    puts "Error requesting reboot: no matching instance found."
  else
    instance = response.reservations[0].instances[0]
    if instance.state.name == "terminated"
      puts "Error requesting reboot: the instance is already terminated."
    else
      ec2_client.reboot_instances(instance_ids: [instance_id])
      puts "Reboot request sent."
    end
  end
rescue StandardError => e
  puts "Error requesting reboot: #{e.message}"
end

# Full example call:
def run_me
  instance_id = ""
  region = ""
  # Print usage information and then stop.
  if ARGV[0] == "--help" || ARGV[0] == "-h"
    puts "Usage:   ruby ec2-ruby-example-reboot-instance-i-123abc.rb " \
      "INSTANCE_ID REGION"
     # Replace us-west-2 with the AWS Region you're using for Amazon EC2.
    puts "Example: ruby ec2-ruby-example-reboot-instance-i-123abc.rb " \
      "i-123abc us-west-2"
    exit 1
  # If no values are specified at the command prompt, use these default values.
  # Replace us-west-2 with the AWS Region you're using for Amazon EC2.
  elsif ARGV.count.zero?
    instance_id = "i-123abc"
    region = "us-west-2"
  # Otherwise, use the values as specified at the command prompt.
  else
    instance_id = ARGV[0]
    region = ARGV[1]
  end

  ec2_client = Aws::EC2::Client.new(region: region)
  request_instance_reboot(ec2_client, instance_id)
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[ec2.Ruby.rebootInstances]
