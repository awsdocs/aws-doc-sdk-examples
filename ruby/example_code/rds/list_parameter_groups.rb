# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[rds.ruby.listParameterGroups]
require "aws-sdk-rds"  # v2: require 'aws-sdk'

# List all parameter groups for an Amazon Relational Database Service (Amazon RDS) cluster
#
# @param rds_resource [Aws::RDS::Resource]: The resource containing SDK logic
# @return
def list_parameter_groups(rds_resource)
  parameter_groups = []
  rds_resource.db_parameter_groups.each do |p|
    parameter_groups.append({ "name": p.db_parameter_group_name, "description": p.description})
  end
  parameter_groups
rescue Aws::Errors::ServiceError => e
  puts "Couldn't list all parameter groups!:\n #{e.message}"
end
# snippet-end:[rds.ruby.listParameterGroups]

if __FILE__ == $0
  rds_resource = Aws::RDS::Resource.new
  list_parameter_groups(rds_resource)
end
