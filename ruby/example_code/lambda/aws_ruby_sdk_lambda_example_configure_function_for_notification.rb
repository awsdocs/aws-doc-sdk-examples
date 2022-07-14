# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-lambda-example-configure-function-for-notification.rb demonstrates how to
# configure an AWS Lambda function to accept notifications using the AWS SDK for Ruby.

# snippet-start:[lambda.ruby.createKey]
require "aws-sdk-lambda"  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Lambda.
client = Aws::Lambda::Client.new(region: "us-west-2")

args = {}
args[:function_name] = "my-notification-function"
args[:statement_id] = "lambda_s3_notification"
args[:action] = "lambda:InvokeFunction"
args[:principal] = "s3.amazonaws.com"
args[:source_arn] = "my-resource-arn"

client.add_permission(args)
# snippet-end:[lambda.ruby.createKey]
