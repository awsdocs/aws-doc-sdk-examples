# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# ec2-ruby-example-elastic-ips.rb demonstrates how to
# check whether the specified Amazon Elastic Compute Cloud
# (Amazon EC2) instance exists.


# snippet-start:[ec2.Ruby.elasticIps]

# This code example does the following:
# 1. Displays information about any addresses associated with an
#    Amazon Elastic Compute Cloud (Amazon EC2) instance.
# 2. Creates an Elastic IP address in Amazon Virtual Private Cloud (Amazon VPC).
# 3. Associates the address with the instance.
# 4. Displays information again about addresses associated with the instance.
#    This time, the new address association should display.
# 5. Releases the address.
# 6. Displays information again about addresses associated with the instance.
#    This time, the released address should not display.

require "aws-sdk-ec2"

# Checks whether the specified Amazon Elastic Compute Cloud
# (Amazon EC2) instance exists.
#
# Prerequisites:
#
# - The Amazon EC2 instance.
#
# @param ec2_client [Aws::EC2::Client] An initialized EC2 client.
# @param instance_id [String] The ID of the instance.
# @return [Boolean] true if the instance exists; otherwise, false.
# @example
#   exit 1 unless instance_exists?(
#     Aws::EC2::Client.new(region: 'us-west-2'),
#     'i-033c48ef067af3dEX'
#   )
def instance_exists?(ec2_client, instance_id)
  ec2_client.describe_instances(instance_ids: [instance_id])
  return true
rescue StandardError
  return false
end

# Creates an Elastic IP address in Amazon Virtual Private Cloud (Amazon VPC).
#
# @param ec2_client [Aws::EC2::Client] An initialized EC2 client.
# @return [String] The allocation ID corresponding to the Elastic IP address.
# @example
#   puts allocate_elastic_ip_address(Aws::EC2::Client.new(region: 'us-west-2'))
def allocate_elastic_ip_address(ec2_client)
  response = ec2_client.allocate_address(domain: "vpc")
  return response.allocation_id
rescue StandardError => e
  puts "Error allocating Elastic IP address: #{e.message}"
  return "Error"
end

# Associates an Elastic IP address with an Amazon Elastic Compute Cloud
# (Amazon EC2) instance.
#
# Prerequisites:
#
# - The allocation ID corresponding to the Elastic IP address.
# - The Amazon EC2 instance.
#
# @param ec2_client [Aws::EC2::Client] An initialized EC2 client.
# @param allocation_id [String] The ID of the allocation corresponding to
#   the Elastic IP address.
# @param instance_id [String] The ID of the instance.
# @return [String] The assocation ID corresponding to the association of the
#   Elastic IP address to the instance.
# @example
#   puts allocate_elastic_ip_address(
#     Aws::EC2::Client.new(region: 'us-west-2'),
#     'eipalloc-04452e528a66279EX',
#     'i-033c48ef067af3dEX')
def associate_elastic_ip_address_with_instance(
  ec2_client,
  allocation_id,
  instance_id
)
  response = ec2_client.associate_address(
    allocation_id: allocation_id,
    instance_id: instance_id,
  )
  return response.association_id
rescue StandardError => e
  puts "Error associating Elastic IP address with instance: #{e.message}"
  return "Error"
end

# Gets information about addresses associated with an
# Amazon Elastic Compute Cloud (Amazon EC2) instance.
#
# Prerequisites:
#
# - The Amazon EC2 instance.
#
# @param ec2_client [Aws::EC2::Client] An initialized EC2 client.
# @param instance_id [String] The ID of the instance.
# @example
#   describe_addresses_for_instance(
#     Aws::EC2::Client.new(region: 'us-west-2'),
#     'i-033c48ef067af3dEX'
#   )
def describe_addresses_for_instance(ec2_client, instance_id)
  response = ec2_client.describe_addresses(
    filters: [
      {
        name: "instance-id",
        values: [instance_id]
      }
    ]
  )
  addresses = response.addresses
  if addresses.count.zero?
    puts "No addresses."
  else
    addresses.each do |address|
      puts "-" * 20
      puts "Public IP:  #{address.public_ip}"
      puts "Private IP: #{address.private_ip_address}"
    end
  end
rescue StandardError => e
  puts "Error getting address information for instance: #{e.message}"
end

# Releases an Elastic IP address from an
# Amazon Elastic Compute Cloud (Amazon EC2) instance.
#
# Prerequisites:
#
# - An Amazon EC2 instance with an associated Elastic IP address.
#
# @param ec2_client [Aws::EC2::Client] An initialized EC2 client.
# @param allocation_id [String] The ID of the allocation corresponding to
#   the Elastic IP address.
# @return [Boolean] true if the Elastic IP address was released;
#   otherwise, false.
# @example
#   exit 1 unless elastic_ip_address_released?(
#     Aws::EC2::Client.new(region: 'us-west-2'),
#     'eipalloc-04452e528a66279EX'
#   )
def elastic_ip_address_released?(ec2_client, allocation_id)
  ec2_client.release_address(allocation_id: allocation_id)
  return true
rescue StandardError => e
  return "Error releasing Elastic IP address: #{e.message}"
end

# Full example call:
def run_me
  instance_id = ""
  region = ""
  # Print usage information and then stop.
  if ARGV[0] == "--help" || ARGV[0] == "-h"
    puts "Usage:   ruby ec2-ruby-example-elastic-ips.rb " \
      "INSTANCE_ID REGION"
    # Replace us-west-2 with the AWS Region you're using for Amazon EC2.
    puts "Example: ruby ec2-ruby-example-elastic-ips.rb " \
      "i-033c48ef067af3dEX us-west-2"
    exit 1
  # If no values are specified at the command prompt, use these default values.
  elsif ARGV.count.zero?
    instance_id = "i-033c48ef067af3dEX"
     # Replace us-west-2 with the AWS Region you're using for Amazon EC2.
    region = "us-west-2"
  # Otherwise, use the values as specified at the command prompt.
  else
    instance_id = ARGV[0]
    region = ARGV[1]
  end

  ec2_client = Aws::EC2::Client.new(region: region)

  unless instance_exists?(ec2_client, instance_id)
    puts "Cannot find instance with ID '#{instance_id}'. Stopping program."
    exit 1
  end

  puts "Addresses for instance with ID '#{instance_id}' before allocating " \
    "Elastic IP address:"
  describe_addresses_for_instance(ec2_client, instance_id)

  puts "Allocating Elastic IP address..."
  allocation_id = allocate_elastic_ip_address(ec2_client)
  if allocation_id.start_with?("Error")
    puts "Stopping program."
    exit 1
  else
    puts "Elastic IP address created with allocation ID '#{allocation_id}'."
  end

  puts "Associating Elastic IP address with instance..."
  association_id = associate_elastic_ip_address_with_instance(
    ec2_client,
    allocation_id,
    instance_id
  )
  if association_id.start_with?("Error")
    puts "Stopping program. You must associate the Elastic IP address yourself."
    exit 1
  else
    puts "Elastic IP address associated with instance with association ID " \
      "'#{association_id}'."
  end

  puts "Addresses for instance after allocating Elastic IP address:"
  describe_addresses_for_instance(ec2_client, instance_id)

  puts "Releasing the Elastic IP address from the instance..."
  if elastic_ip_address_released?(ec2_client, allocation_id) == false
    puts "Stopping program. You must release the Elastic IP address yourself."
    exit 1
  else
    puts "Address released."
  end

  puts "Addresses for instance after releasing Elastic IP address:"
  describe_addresses_for_instance(ec2_client, instance_id)
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[ec2.Ruby.elasticIps]
