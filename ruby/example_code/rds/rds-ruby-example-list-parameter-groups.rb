# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# rds-ruby-example-list-parameter-groups.rb demonstrates how to list your
# Amazon Relational Database Service (Amazon RDS) parameter groups using the AWS SDK for Ruby.

# snippet-start:[rds.ruby.listParameterGroups]

require 'aws-sdk-rds'  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon RDS.
rds = Aws::RDS::Resource.new(region: 'us-west-2')

rds.db_parameter_groups.each do |p|
  puts p.db_parameter_group_name
  puts '  ' + p.description
end
# snippet-end:[rds.ruby.listParameterGroups]
