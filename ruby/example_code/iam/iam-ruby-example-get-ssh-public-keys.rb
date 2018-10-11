#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Ruby]
#snippet-keyword:[Code Sample]
#snippet-service:[AWS Identity and Access Management (IAM)]
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

user_name = 'your-name'
iam = Aws::IAM::Client.new(region: 'us-west-2')

begin
  ssh_public_keys_response = iam.list_ssh_public_keys({
    user_name: user_name,
    max_items: 10,
  })

  ssh_public_keys_response.ssh_public_keys.each do |ssh_public_key|
    ssh_public_key_response = iam.get_ssh_public_key({
      user_name: user_name,
      ssh_public_key_id: ssh_public_key.ssh_public_key_id,
      encoding: "SSH",
    })
    puts ssh_public_key_response.ssh_public_key.ssh_public_key_body
  end

rescue Aws::IAM::Errors::NoSuchEntity => ex
  puts 'User does not exist'
end
