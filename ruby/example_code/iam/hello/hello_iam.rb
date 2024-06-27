# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[ruby.example_code.iam.HelloIAM]

require 'aws-sdk-iam'

# Create an IAM client
iam = Aws::IAM::Client.new

# List IAM policies
policies = []
params = {}
loop do
  resp = iam.list_policies(params)
  policies.concat(resp.policies)
  break if !resp.is_truncated
  params[:marker] = resp.marker
end

# Print the policy names
puts "Here are the IAM policies in your account:"
policies.each do |policy|
  puts "- #{policy.policy_name}"
end

# snippet-end:[ruby.example_code.iam.HelloIAM]
