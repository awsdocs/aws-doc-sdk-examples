# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Allocates an Elastic IP address, associates the address with an Amazon EC2 instance, gets information about addresses associated with the instance, and release the address.]
# snippet-keyword:[Amazon Elastic Compute Cloud]
# snippet-keyword:[allocate_address method]
# snippet-keyword:[associate_address method]
# snippet-keyword:[describe_addresses method]
# snippet-keyword:[release_address method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[ec2]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

# Demonstrates how to:
# 1. Allocate an Elastic IP address.
# 2. Associate the address with an Amazon EC2 instance.
# 2. Get information about addresses associated with the instance. 
# 4. Release the address.

require 'aws-sdk-ec2'  # v2: require 'aws-sdk'

ec2 = Aws::EC2::Client.new(region: 'us-east-1')

instance_id = "INSTANCE-ID" # For example, "i-0a123456b7c8defg9"

def display_addresses(ec2, instance_id)
  describe_addresses_result = ec2.describe_addresses({
    filters: [
      {
        name: "instance-id",
        values: [ instance_id ]
      },
    ]
  })
  if describe_addresses_result.addresses.count == 0
    puts "No addresses currently associated with the instance."
  else
    describe_addresses_result.addresses.each do |address|
      puts "=" * 10
      puts "Allocation ID: #{address.allocation_id}"
      puts "Association ID: #{address.association_id}"
      puts "Instance ID: #{address.instance_id}"
      puts "Public IP: #{address.public_ip}"
      puts "Private IP Address: #{address.private_ip_address}"
    end
  end
end

puts "Before allocating the address for the instance...."
display_addresses(ec2, instance_id)

puts "\nAllocating the address for the instance..."
ec2.allocate_address({
  domain: "vpc" 
})

puts "\nAfter allocating the address for instance, but before associating the address with the instance..."
display_addresses(ec2, instance_id)

puts "\nAssociating the address with the instance..."
ec2.associate_address({
  allocation_id: allocate_address_result.allocation_id, 
  instance_id: instance_id, 
})

puts "\nAfter associating the address with the instance, but before releasing the address from the instance..."
display_addresses(ec2, instance_id)

puts "\nReleasing the address from the instance..."
ec2.release_address({
  allocation_id: allocate_address_result.allocation_id, 
})

puts "\nAfter releasing the address from the instance..."
display_addresses(ec2, instance_id)
