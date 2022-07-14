# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-lambda-example-run-function.rb demonstrates how to
# list your AWS Lambda functions using the AWS SDK for Ruby.

# snippet-start:[lambda.ruby.listFunctions]

require "aws-sdk-lambda"  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Lambda.
client = Aws::Lambda::Client.new(region: "us-west-2")

client.list_functions.functions.each do |function|
  puts "Name: " + function.function_name
  puts "ARN:  " + function.function_arn
  puts "Role: " + function.role
  puts
end
# snippet-end:[lambda.ruby.listFunctions]
