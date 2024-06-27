# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

# snippet-start:[auto-scaling.ruby.hello]

require "aws-sdk-autoscaling"

# Creates an Amazon AutoScaling client for the AWS Region
# specified in the environment variables or AWS credentials file.
autoscaling = Aws::AutoScaling::Client.new

# Gets a list of Auto Scaling groups for the account.
groups = autoscaling.describe_auto_scaling_groups.auto_scaling_groups

# If the account has no Auto Scaling groups, print a message.
if groups.count.zero?
  puts "No Auto Scaling groups found for this account."
# Otherwise, print information about each Auto Scaling group.
else
  groups.each do |group|
    puts "Auto Scaling group name: #{group.auto_scaling_group_name}"
    puts "  Group ARN:             #{group.auto_scaling_group_arn}"
    puts "  Min/max/desired:       #{group.min_size}/#{group.max_size}/#{group.desired_capacity}"
    puts "\n"
  end
end

# snippet-end:[auto-scaling.ruby.hello]

