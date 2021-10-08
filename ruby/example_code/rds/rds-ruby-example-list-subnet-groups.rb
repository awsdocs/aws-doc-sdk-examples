# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# rds-ruby-example-create-cluster-snapshot.rb demonstrates how to list your
# Amazon Relational Database Service (RDS) subnet groups using the AWS SKD for Ruby.

# snippet-start:[rds.ruby.listSubnetGroups]
require 'aws-sdk-rds'  # v2: require 'aws-sdk'

rds = Aws::RDS::Resource.new(region: 'us-west-2')

rds.db_subnet_groups.each do |s|
  puts s.name
  puts '  ' + s.subnet_group_status
end
# snippet-end:[rds.ruby.listSubnetGroups]
