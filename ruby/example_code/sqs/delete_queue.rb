# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to delete an Amazon Simple Queue Service (Amazon SQS) service.

# snippet-start:[ruby.example_code.sqs.DeleteQueue]
require "aws-sdk-sqs"  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon SQS.
sqs = Aws::SQS::Client.new(region: "us-west-2")

sqs.delete_queue(queue_url: URL)
# snippet-end:[ruby.example_code.sqs.DeleteQueue]
