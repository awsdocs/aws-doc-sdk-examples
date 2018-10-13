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

require 'aws-sdk-iam'  # v2: require 'aws-sdk'

# Policy ARNs start with:
prefix = 'arn:aws:iam::aws:policy/'

policy_arn = prefix + 'AmazonS3FullAccess'

# In case the policy or user does not exist
begin
  client.attach_user_policy({user_name: 'my_groovy_user', policy_arn: policy_arn})
rescue Aws::IAM::Errors::NoSuchEntity => ex
  puts "Error attaching policy '#{policy_arn}'"
  puts ex.message
end
