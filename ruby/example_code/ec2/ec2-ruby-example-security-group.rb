#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Creates a security group, adds rules to the security group, gets information about security groups, and deletes the security group.]
#snippet-keyword:[Amazon Elastic Compute Cloud]
#snippet-keyword:[authorize_security_group_ingress method]
#snippet-keyword:[create_security_group method]
#snippet-keyword:[delete_security_group method]
#snippet-keyword:[describe_security_groups method]
#snippet-keyword:[Ruby]
#snippet-service:[ec2]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-03-16]
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
# 1. Create a security group.
# 2. Add rules to the security group.
# 3. Get information about security groups.
# 4. Delete the security group.

require 'aws-sdk-ec2'  # v2: require 'aws-sdk'

ec2 = Aws::EC2::Client.new(region: 'us-east-1')

security_group_name = "my-security-group"
vpc_id = "VPC-ID" # For example, "vpc-1234ab56".
security_group_created = false # Used later to determine whether it's okay to delete the security group.

# Create a security group.
begin
  create_security_group_result = ec2.create_security_group({
    group_name: security_group_name,
    description: "An example description for my security group.",
    vpc_id: vpc_id
  })

  # Add rules to the security group.
  # For example, allow all inbound HTTP and SSH traffic.
  ec2.authorize_security_group_ingress({
    group_id: create_security_group_result.group_id,
    ip_permissions: [
      {
        ip_protocol: "tcp",
        from_port: 80,
        to_port: 80,
        ip_ranges: [
          {
            cidr_ip: "0.0.0.0/0",
          }
        ]
      },
      {
        ip_protocol: "tcp",
        from_port: 22,
        to_port: 22,
        ip_ranges: [
          {
            cidr_ip: "0.0.0.0/0",
          }
        ]
      }
    ]
  })

  security_group_created = true
rescue Aws::EC2::Errors::InvalidGroupDuplicate
  puts "A security group with the name '#{security_group_name}' already exists."
end

# Get information about your security groups.

# This method gets information about an individual IP permission.
# The code is identical for calling ip_permissions and ip_permissions_egress later,
#   so making a method out of it to reduce duplicated code.
def describe_ip_permission(ip_permission)
  puts "-" * 22
  puts "IP Protocol: #{ip_permission.ip_protocol}"
  puts "From Port: #{ip_permission.from_port.to_s}"
  puts "To Port: #{ip_permission.to_port.to_s}"
  if ip_permission.ip_ranges.count > 0
    puts "IP Ranges:"
    ip_permission.ip_ranges.each do |ip_range|
      puts "  #{ip_range.cidr_ip}"
    end
  end
  if ip_permission.ipv_6_ranges.count > 0
    puts "IPv6 Ranges:"
    ip_permission.ipv_6_ranges.each do |ipv_6_range|
      puts "  #{ipv_6_range.cidr_ipv_6}"
    end
  end
  if ip_permission.prefix_list_ids.count > 0
    puts "Prefix List IDs:"
    ip_permission.prefix_list_ids.each do |prefix_list_id|
      puts "  #{prefix_list_id.prefix_list_id}"
    end
  end
  if ip_permission.user_id_group_pairs.count > 0
    puts "User ID Group Pairs:"
    ip_permission.user_id_group_pairs.each do |user_id_group_pair|
      puts "  ." * 7
      puts "  Group ID: #{user_id_group_pair.group_id}"
      puts "  Group Name: #{user_id_group_pair.group_name}"
      puts "  Peering Status: #{user_id_group_pair.peering_status}"
      puts "  User ID: #{user_id_group_pair.user_id}"
      puts "  VPC ID: #{user_id_group_pair.vpc_id}"
      puts "  VPC Peering Connection ID: #{user_id_group_pair.vpc_peering_connection_id}"
    end
  end
end

describe_security_groups_result = ec2.describe_security_groups

describe_security_groups_result.security_groups.each do |security_group|
  puts "\n"
  puts "*" * (security_group.group_name.length + 12)
  puts "Group Name: #{security_group.group_name}"
  puts "Group ID: #{security_group.group_id}"
  puts "Description: #{security_group.description}"
  puts "VPC ID: #{security_group.vpc_id}"
  puts "Owner ID: #{security_group.owner_id}"
  if security_group.ip_permissions.count > 0
    puts "=" * 22
    puts "IP Permissions:"
    security_group.ip_permissions.each do |ip_permission|
      describe_ip_permission(ip_permission)
    end
  end
  if security_group.ip_permissions_egress.count > 0
    puts "=" * 22
    puts "IP Permissions Egress:"
    security_group.ip_permissions_egress.each do |ip_permission|
      describe_ip_permission(ip_permission)
    end
  end
  if security_group.tags.count > 0
    puts "=" * 22
    puts "Tags:"
    security_group.tags.each do |tag|
      puts "  #{tag.key} = #{tag.value}"
    end
  end   
end

# Delete the security group if it was created earlier.
if security_group_created
  ec2.delete_security_group({ group_id: create_security_group_result.group_id })
end
