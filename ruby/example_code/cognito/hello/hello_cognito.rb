# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[ruby.hello_cognito.list_user_pools]
require "aws-sdk-cognitoidentityprovider"

# Creates a Cognito Identity Provider client
cognito_client = Aws::CognitoIdentityProvider::Client.new

# Lists all user pools associated with the AWS account
resp = cognito_client.list_user_pools(max_results: 10)

# Prints the user pool information
if resp.user_pools.count.zero?
  puts "No Cognito user pools found."
else
  resp.user_pools.each do |user_pool|
    puts "User pool ID: #{user_pool.id}"
    puts "User pool name: #{user_pool.name}"
    puts "User pool status: #{user_pool.status}"
    puts "---"
  end
end
# snippet-end:[ruby.hello_cognito.list_user_pools]

