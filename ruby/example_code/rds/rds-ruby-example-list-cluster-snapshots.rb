# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# rds-ruby-example-create-cluster-snapshot.rb demonstrates how to list your
# Amazon Relational Database Service (RDS) cluster using the AWS SKD for Ruby.

# snippet-start:[rds.ruby.listClusterSnapshots]

require 'aws-sdk-rds'  # v2: require 'aws-sdk'

rds = Aws::RDS::Resource.new(region: 'us-west-2')

rds.db_clusters.each do |c|
  puts "Name (ID): #{c.id}"
  puts "Status:    #{c.status}"

  c.snapshots.each do |s|
    puts "  Snapshot: #{s.snapshot_id}"
    puts "  Status:   #{s.status}"
  end
end
# snippet-end:[rds.ruby.listClusterSnapshots]
