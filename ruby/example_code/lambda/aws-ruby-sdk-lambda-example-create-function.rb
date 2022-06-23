# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# aws-ruby-sdk-lambda-example-configure-function-for-notification.rb demonstrates how to
# create an AWS Lambda function using the AWS SDK for Ruby.

# snippet-start:[lambda.ruby.createFunctions]

require 'aws-sdk-lambda'  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Lambda.
client = Aws::Lambda::Client.new(region: 'us-west-2')

args = {}
args[:role] = 'my-resource-arn'
args[:function_name] = 'my-notification-function'
args[:handler] = 'my-package.my-class'

# Also accepts nodejs, nodejs4.3, and python2.7
args[:runtime] = 'java8'

code = {}
code[:zip_file] = 'my-zip-file.zip'
code[:s3_bucket] = 'my-notification-bucket'
code[:s3_key] = 'my-zip-file'

args[:code] = code

client.create_function(args)
# snippet-end:[lambda.ruby.createFunctions]
