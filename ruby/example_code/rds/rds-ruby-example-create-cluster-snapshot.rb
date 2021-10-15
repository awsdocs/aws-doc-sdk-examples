# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# rds-ruby-example-create-cluster-snapshot.rb demonstrates how to create a snapshot of an
# Amazon Relational Database Service (Amazon RDS) cluster using the AWS SDK for Ruby.

# snippet-start:[rds.ruby.createClusterSnapshot]

require 'aws-sdk-rds'  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon RDS.
rds = Aws::RDS::Resource.new(region: 'us-west-2')

cluster = rds.db_cluster(cluster_name)

date =  Time.new
date_time =  date.year.to_s +  '-' +  date.month.to_s +  '-' +  date.day.to_s +  '-' +  date.hour.to_s +  '-' +  date.min.to_s

id = cluster_name + '-' + date_time

cluster.create_snapshot({db_cluster_snapshot_identifier: id})

puts "Created cluster snapshot #{id}"
# snippet-end:[rds.ruby.createClusterSnapshot]
