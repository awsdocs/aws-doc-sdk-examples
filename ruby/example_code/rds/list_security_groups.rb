# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[rds.ruby.listSecurityGroups]
require "aws-sdk-rds"  # v2: require 'aws-sdk'

# List all Amazon Relational Database Service (Amazon RDS) security groups.
#
# @param rds_resource [Aws::RDS::Resource] An SDK for Ruby Amazon RDS resource.
# @return security_groups [List, nil] A list of security groups, or nil if error.
def list_security_groups(rds_resource)
  security_groups = []
  rds_resource.db_instances.each do |i|
    # Show any security group IDs and descriptions
    i.db_security_groups.each do |sg|
      security_groups.append({
                               "name": sg.db_security_group_name,
                               "description": db_security_group_description,
                               "type": "db"
                             })
    end

    i.vpc_security_groups.each do |vsg|
      security_groups.append({
                               "name": vsg.vpc_security_group_id,
                               "description": vsg.status,
                               "type": "vpc"
                             })
    end
  end
  security_groups
rescue Aws::Errors::ServiceError => e
  puts "Couldn't list security groups:\n#{e.message}"
end
# snippet-end:[rds.ruby.listSecurityGroups]

if __FILE__ == $0
  rds_resource = Aws::RDS::Resource.new
  list_security_groups(rds_resource)
end
