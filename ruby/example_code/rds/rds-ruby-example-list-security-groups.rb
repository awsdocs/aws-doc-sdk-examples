# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# rds-ruby-example-list-security-groups.rb demonstrates how to list the security groups
# of your Amazon Relational Database Service (Amazon RDS) instances using the AWS SDK for Ruby.

# snippet-start:[rds.ruby.listSecurityGroups]

require "aws-sdk-rds"  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon RDS.
rds = Aws::RDS::Resource.new(region: "us-west-2")

rds.db_instances.each do |i|
  # Show any security group IDs and descriptions
  puts "Security Groups:"

  i.db_security_groups.each do |sg|
    puts sg.db_security_group_name
    puts "  " + sg.db_security_group_description
    puts
  end

  # Show any VPC security group IDs and their status
  puts "VPC Security Groups:"

  i.vpc_security_groups.each do |vsg|
    puts vsg.vpc_security_group_id
    puts "  " + vsg.status
    puts
  end
end
# snippet-end:[rds.ruby.listSecurityGroups]
