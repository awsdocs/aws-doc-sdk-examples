# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to list the values of your secrets.

# Inputs:
# - REGION - The AWS Region.

# snippet-start:[s3.ruby.sm_get_secrets.rb]

require 'aws-sdk-secretsmanager'

# Gets all secrets in us-west-2
# Replace us-west-2 with the AWS Region you're using for Amazon Secrets Manager.
sm = Aws::SecretsManager::Client.new(region: 'us-east-1')

resp = sm.list_secrets

puts 'Secrets:'

resp.secret_list.each do |s|
  puts '  name: ' + s.name
  puts '  key/value:'

  resp = sm.get_secret_value(secret_id: s.name)

  if resp.secret_string
    puts '    ' + resp.secret_string
  else
    # do something with resp.secret_binary
  end

  puts
end
# snippet-end:[s3.ruby.sm_get_secrets.rb]
