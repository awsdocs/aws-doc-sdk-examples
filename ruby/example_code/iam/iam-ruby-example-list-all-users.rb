# Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

require 'aws-sdk'

iam = Aws::IAM::Client.new(region: 'us-west-2')

iam.list_users.users.each do |user|
  name = user.user_name
  puts "For user #{name}"
  puts "  In groups:"
  
  iam.list_groups_for_user({user_name: name}).groups.each do |group|
    puts "    #{group.group_name}"
  end
  
  puts "  Policies:"
  
  iam.list_user_policies({user_name: name}).policy_names.each do |policy|
    puts "    #{policy}"
  end
  
  puts "  Access keys:"
  
  iam.list_access_keys({user_name: name}).access_key_metadata.each do |key|
    puts "    #{key.access_key_id}"
  end
end
