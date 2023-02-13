# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to poll for messages on an Amazon Simple Queue Service (Amazon SQS) queue
# that are not visible after being received.

# snippet-start:[s3.sqs-ruby-example-poll-messages.rb]

require "aws-sdk-sqs"  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon SQS.
Aws.config.update({region: "us-west-2"})

poller = Aws::SQS::QueuePoller.new(URL)

poller.poll(idle_timeout: 15) do |msg|
  puts msg.body
end
# snippet-end:[s3.sqs-ruby-example-poll-messages.rb]
