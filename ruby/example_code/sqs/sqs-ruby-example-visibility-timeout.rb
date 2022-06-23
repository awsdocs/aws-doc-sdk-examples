# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to poll for messages on an Amazon Simple Queue Service (Amazon SQS) queue.


# snippet-start:[s3.sqs-ruby-example-visibility-timeout.rb]
require 'aws-sdk-sqs'  # v2: require 'aws-sdk'

# Process the message
def do_something(msg)
  puts msg.body
end
# Replace us-west-2 with the AWS Region you're using for Amazon SQS.
Aws.config.update({region: 'us-west-2'})

poller = Aws::SQS::QueuePoller.new(URL)

poller.poll(visibility_timeout: timeout, idle_timeout: timeout + 1) do |msg|
  do_something(msg)
end
# snippet-end:[s3.sqs-ruby-example-visibility-timeout.rb]
