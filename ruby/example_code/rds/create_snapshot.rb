# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[rds.ruby.createDatabaseSnapshot]

require "aws-sdk-rds"  # v2: require 'aws-sdk'

# Create a snapshot for an Amazon Relational Database Service (Amazon RDS) cluster
#
# @param rds_resource [Aws::RDS::Resource]: The resource containing SDK logic
# @return [Aws::RDS::DBSnapshot] The snapshot created
def create_snapshot(rds_resource, instance_name)
  id = "snapshot-#{rand(10**6)}"
  instance = rds_resource.db_instance(instance_name)
  instance.create_snapshot({db_snapshot_identifier: id})
rescue Aws::Errors::ServiceError => e
  puts "Couldn't create instance snapshot: #{id}. Here's why:\n #{e.message}"
  raise
end
# snippet-end:[rds.ruby.createDatabaseSnapshot]
if __FILE__ == $0
  rds_resource = Aws::RDS::Resource.new
  instance_name = "rds-#{rand(10**4)}-instance"
  create_snapshot(rds_resource, instance_name)
end
