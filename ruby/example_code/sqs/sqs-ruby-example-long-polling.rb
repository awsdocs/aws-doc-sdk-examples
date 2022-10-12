# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to poll (wait) for new messages in an
# Amazon Simple Queue Service (Amazon SQS) queue.

# snippet-start:[s3.ruby.sqs-ruby-example-long-polling.rb]
require "aws-sdk-sqs"  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon SQS.
Aws.config.update({region: "us-west-2"})

poller = Aws::SQS::QueuePoller.new(URL)

poller.poll(wait_time_seconds: duration, idle_timeout: duration + 1) do |msg|
  puts msg.body
end
# snippet-end:[s3.ruby.sqs-ruby-example-long-polling.rb]
