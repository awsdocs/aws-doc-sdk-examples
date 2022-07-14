# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# rds-ruby-example-list-subnet-groups.rb demonstrates how to list your
# Amazon Relational Database Service (Amazon RDS) subnet groups using the AWS SDK for Ruby.

# snippet-start:[rds.ruby.listSubnetGroups]
require "aws-sdk-rds"  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon RDS.
rds = Aws::RDS::Resource.new(region: "us-west-2")

rds.db_subnet_groups.each do |s|
  puts s.name
  puts "  " + s.subnet_group_status
end
# snippet-end:[rds.ruby.listSubnetGroups]
