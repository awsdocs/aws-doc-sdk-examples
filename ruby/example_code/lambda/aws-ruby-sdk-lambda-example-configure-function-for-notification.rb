# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-lambda-example-configure-function-for-notification.rb demonstrates how to
# configure a Lambda function accept notifications
# using Amazon Key Management Services (KMS) using the AWS SKD for Ruby.

# snippet-start:[lambda.ruby.createKey]
require 'aws-sdk-lambda'  # v2: require 'aws-sdk'

client = Aws::Lambda::Client.new(region: 'us-west-2')

args = {}
args[:function_name] = 'my-notification-function'
args[:statement_id] = 'lambda_s3_notification'
args[:action] = 'lambda:InvokeFunction'
args[:principal] = 's3.amazonaws.com'
args[:source_arn] = 'my-resource-arn'

client.add_permission(args)
# snippet-end:[lambda.ruby.createKey]
