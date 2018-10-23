#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Ruby]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS Identity and Access Management (IAM)]
#snippet-service:[iam]
#snippet-sourcetype:[<<snippet or full-example>>]
#snippet-sourcedate:[]
#snippet-sourceauthor:[AWS]
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

iam = Aws::IAM::Client.new(region: 'us-west-2')

begin
  user = iam.create_user(user_name: 'my_groovy_user')
  iam.wait_until(:user_exists, user_name: 'my_groovy_user')

  user.create_login_profile({password: 'REPLACE_ME'})

  arn_parts = user.arn.split(':')
  puts 'Account ID:        ' + arn_parts[4]
rescue Aws::IAM::Errors::EntityAlreadyExists
  puts 'User already exists'
end
