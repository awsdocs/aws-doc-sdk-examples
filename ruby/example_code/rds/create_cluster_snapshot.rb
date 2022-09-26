# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[rds.ruby.createClusterSnapshot]

require "aws-sdk-rds"  # v2: require 'aws-sdk'

# Create a snapshot of an Amazon Relational Database Service (Amazon RDS) cluster
#
# @param cluster_name [String]: The name of the cluster you want to back up
# @return db_cluster_snapshot [DBClusterSnapshot] The snapshot created
def create_cluster_snapshot(rds_resource, cluster_name)
  cluster = rds_resource.db_cluster(cluster_name)
  date = Time.new
  date_time =  date.year.to_s +  "-" +  date.month.to_s +  "-" +  date.day.to_s +  "-" +  date.hour.to_s +  "-" +  date.min.to_s
  id = cluster_name + "-" + date_time
  db_cluster_snapshot = cluster.create_snapshot({db_cluster_snapshot_identifier: id})
  db_cluster_snapshot
rescue Aws::Errors::ServiceError => e
  puts "Couldn't create cluster snapshot: #{id}. Here's why: #{e.message}"
end
# snippet-end:[rds.ruby.createClusterSnapshot]

if __FILE__ == $0
  rds_resource = Aws::RDS::Resource.new
  cluster_name = "rds-#{rand(10**4)}-cluster"
  create_cluster_snapshot(rds_resource, cluster_name)
end
