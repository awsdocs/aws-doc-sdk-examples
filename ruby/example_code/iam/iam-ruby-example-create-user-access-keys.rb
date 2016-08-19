# Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

begin
  user = iam.user(user_name: 'my_groovy_user')

  key_pair = user.create_access_key_pair

  puts "Access key: #{key_pair.access_key_id}"
  puts "Secret key: #{key_pair.secret}"
rescue Aws::IAM::Errors::NoSuchEntity => ex
  puts 'User does not exist'
end
