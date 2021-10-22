# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# rds-ruby-example-list-instance-snapshots.rb demonstrates how to list all
# Amazon Relational Database Service (Amazon RDS) instances using the AWS SDK for Ruby.

# snippet-start:[rds.ruby.listAllInstances]

require 'aws-sdk-rds'  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon RDS.
rds = Aws::RDS::Resource.new(region: 'us-west-2')

rds.db_instances.each do |i|
  puts "Name (ID): #{i.id}"
  puts "Status   : #{i.db_instance_status}"
  puts
end
# snippet-end:[rds.ruby.listAllInstances]
