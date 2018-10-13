#snippet-sourceauthor: [Doug-AWS]

#snippet-sourcedescription:[Description]

#snippet-service:[AWSService]

#snippet-sourcetype:[full example]

#snippet-sourcedate:[N/A]

# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

# Demonstrates how to:
# 1. Create a policy.
# 2. Get information about the policy.
# 3. Attach the policy to a role.
# 4. List policies attached to the role.
# 5. Detach the policy from the role.

require 'aws-sdk-iam'  # v2: require 'aws-sdk'

iam = Aws::IAM::Client.new(region: 'us-east-1')

role_name = "my-role"
policy_name = "my-policy"
policy_document = {
  "Version" => "2012-10-17",
  "Statement" => [
    {
      "Effect" => "Allow",
      "Action" => "s3:ListAllMyBuckets",
      "Resource" => "arn:aws:s3:::*"
    }
  ]
}.to_json

# Create a policy.
puts "Creating policy..."

create_policy_response = iam.create_policy({
  policy_name: policy_name,
  policy_document: policy_document
})

policy_arn = create_policy_response.policy.arn

# Get information about the policy.
get_policy_response = iam.get_policy({ policy_arn: policy_arn })

puts "\nCreated policy, ID = #{get_policy_response.policy.policy_id}"

# Attach the policy to a role.
puts "\nAttaching policy to role..."
  
iam.attach_role_policy({
  role_name: role_name,
  policy_arn: policy_arn
})
    
# List policies attached to the role.
puts "\nAttached role policy ARNs..."

iam.list_attached_role_policies({ role_name: role_name }).attached_policies.each do |attached_policy|
  puts "  #{attached_policy.policy_arn}"
end

# Detach the policy from the role.
puts "\nDetaching role policy..."

iam.detach_role_policy({
  role_name: role_name,
  policy_arn: policy_arn
})
