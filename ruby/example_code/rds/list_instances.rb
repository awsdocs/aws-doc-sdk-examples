# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# list_instance_snapshots.rb demonstrates how to list all
# Amazon Relational Database Service (Amazon RDS) instances using the AWS SDK for Ruby.

# snippet-start:[rds.ruby.listAllInstances]
require "aws-sdk-rds"  # v2: require 'aws-sdk'

# List instances within an Amazon Relational Database Service (Amazon RDS) cluster
#
# @param rds_resource [Aws::RDS::Resource]: The resource containing SDK logic
# @return [Array] List of all DB instances
def list_instances(rds_resource)
  db_instances = []
  rds_resource.db_instances.each do |i|
    db_instances.append({"name": i.id, "status": i.db_instance_status})
  end
  db_instances
rescue Aws::Errors::ServiceError => e
  puts "Couldn't list instances: #{id}. Here's why: #{e.message}"
end
# snippet-end:[rds.ruby.listAllInstances]

if __FILE__ == $0
  rds_resource = Aws::RDS::Client.new
  list_instances(rds_resource)
end
