# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# rds-ruby-example-create-cluster-snapshot.rb demonstrates how to list your
# Amazon Relational Database Service (Amazon RDS) instance snapshots using the AWS SDK for Ruby.

# snippet-start:[rds.ruby.listInstanceSnapshots]

require "aws-sdk-rds"  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon RDS.
rds = Aws::RDS::Resource.new(region: "us-west-2")

rds.db_snapshots.each do |s|
  puts "Name (ID): #{s.snapshot_id}"
  puts "Status:    #{s.status}"
end
# snippet-end:[rds.ruby.listInstanceSnapshots]
