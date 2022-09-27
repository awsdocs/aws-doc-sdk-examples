# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[rds.ruby.listSubnetGroups]
require "aws-sdk-rds"  # v2: require 'aws-sdk'

# List all Amazon Relational Database Service (Amazon RDS) subnet groups.
#
# @param rds_resource [Aws::RDS::Resource] An SDK for Ruby Amazon RDS resource
# @return [Array, nil] All the groups discovered or nil if error
def list_subnet_groups(rds_resource)
  db_subnet_groups = []
  rds_resource.db_subnet_groups.each do |s|
    db_subnet_groups.append({
                              "name": s.name,
                              "status": s.subnet_group_status
                            })
  end
  db_subnet_groups
rescue Aws::Errors::ServiceError => e
  puts "Couldn't list subnet groups:\n#{e.message}"
end
# snippet-end:[rds.ruby.listSubnetGroups]

if __FILE__ == $0
  rds_resource = Aws::RDS::Resource.new
  list_subnet_groups(rds_resource)
end
