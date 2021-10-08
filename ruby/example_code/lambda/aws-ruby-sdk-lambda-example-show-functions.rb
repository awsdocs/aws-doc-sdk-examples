# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-lambda-example-run-function.rb demonstrates how to
# list your Lambda functions using Amazon Key Management Services (KMS) using the AWS SKD for Ruby.

# snippet-start:[lambda.ruby.listFunctions]


require 'aws-sdk-lambda'  # v2: require 'aws-sdk'

client = Aws::Lambda::Client.new(region: 'us-west-2')

client.list_functions.functions.each do |function|
  puts 'Name: ' + function.function_name
  puts 'ARN:  ' + function.function_arn
  puts 'Role: ' + function.role
  puts
end
# snippet-end:[lambda.ruby.listFunctions]
