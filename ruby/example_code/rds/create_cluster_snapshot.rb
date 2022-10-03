# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[rds.ruby.createClusterSnapshot]
require "aws-sdk-rds"  # v2: require 'aws-sdk'

# Create a snapshot of an Amazon Relational Database Service (Amazon RDS)
# Multi-AZ DB cluster.
#
# @param rds_resource [Aws::RDS::Resource] An SDK for Ruby Amazon RDS resource.
# @param multi_az_db_cluster [String] The Multi-AZ DB cluster to snapshot.
# @return [DBClusterSnapshot, nil] The snapshot created, or nil if error.
def create_cluster_snapshot(rds_resource, multi_az_db_cluster)
  cluster = rds_resource.db_cluster(multi_az_db_cluster)
  id = "snapshot-#{rand(10**6)}"
  cluster.create_snapshot({
                            db_cluster_snapshot_identifier: id
                          })
rescue Aws::Errors::ServiceError => e
  puts "Couldn't create Multi-AZ DB cluster snapshot #{id}:\n#{e.message}"
end
# snippet-end:[rds.ruby.createClusterSnapshot]

if __FILE__ == $0
  rds_resource = Aws::RDS::Resource.new
  multi_az_db_cluster_name = "rds-#{rand(10**4)}-cluster"
  create_cluster_snapshot(rds_resource, multi_az_db_cluster_name)
end
