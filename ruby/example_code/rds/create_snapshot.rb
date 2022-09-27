# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[rds.ruby.createDatabaseSnapshot]
require "aws-sdk-rds"  # v2: require 'aws-sdk'

# Create a snapshot for an Amazon Relational Database Service (Amazon RDS)
# DB instance.
#
# @param rds_resource [Aws::RDS::Resource] The resource containing SDK logic
# @param db_instance_name [String] The name of the Amazon RDS DB instance
# @return [Aws::RDS::DBSnapshot, nil] The snapshot created or nil if error
def create_snapshot(rds_resource, db_instance_name)
  id = "snapshot-#{rand(10**6)}"
  db_instance = rds_resource.db_instance(db_instance_name)
  db_instance.create_snapshot({
                                db_snapshot_identifier: id
                              })
rescue Aws::Errors::ServiceError => e
  puts "Couldn't create DB instance snapshot #{id}:\n #{e.message}"
end
# snippet-end:[rds.ruby.createDatabaseSnapshot]
if __FILE__ == $0
  rds_resource = Aws::RDS::Resource.new
  db_instance_name = "rds-#{rand(10**4)}-instance"
  create_snapshot(rds_resource, db_instance_name)
end
