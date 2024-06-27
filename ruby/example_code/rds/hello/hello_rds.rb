# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[ruby.hello-rds.list_instances]

require "aws-sdk-rds"

# Create an RDS client
rds = Aws::RDS::Client.new(region: "us-west-2")

# List all DB instances
instances = []
batch = rds.describe_db_instances.db_instances
instances.concat(batch)

loop do
  break if batch.empty? || batch.last.db_instance_arn.nil?
  batch = rds.describe_db_instances(marker: batch.last.db_instance_identifier).db_instances
  instances.concat(batch)
end

# Print the list of DB instances
if instances.count.zero?
  puts "No instances found."
else
  puts "Found #{instances.count} instance(s):"
  instances.each do |instance|
    puts " * #{instance.db_instance_identifier} (#{instance.db_instance_status})"
  end
end

# snippet-end:[ruby.hello-rds.list_instances]
