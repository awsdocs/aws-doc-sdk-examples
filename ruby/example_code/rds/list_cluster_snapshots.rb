# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[rds.ruby.listClusterSnapshots]

require "aws-sdk-rds"  # v2: require 'aws-sdk'

# List all snapshots for an Amazon Relational Database Service (Amazon RDS) cluster
#
# @param rds_resource [Aws::RDS::Resource]: The resource containing SDK logic
# @return
def list_cluster_snapshots(rds_resource, cluster_name)
  cluster_snapshots = []
  rds_resource.db_clusters.each do |c|
    c.snapshots.each do |s|
      cluster_snapshots.append({"cluster": c.id, "snapshot_id": s.snapshot_id, "snapshot_status": s.status})
    end
  end
  cluster_snapshots
rescue Aws::Errors::ServiceError => e
  puts "Couldn't list cluster snapshots!:\n #{e.message}"
end
# snippet-end:[rds.ruby.listClusterSnapshots]

if __FILE__ == $0
  rds_resource = Aws::RDS::Resource.new
  list_cluster_snapshots(rds_resource, cluster_name)
end
