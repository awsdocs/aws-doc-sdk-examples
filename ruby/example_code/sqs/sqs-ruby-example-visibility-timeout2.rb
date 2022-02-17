# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to perform an operation on a message from an Amazon Simple Queue Service (Amazon SQS) queue.

# snippet-start:[s3.sqs-ruby-example-visibility-timeout2.rb]
require 'aws-sdk-sqs'  # v2: require 'aws-sdk'

# Process the message
def do_something(_)
  true
end

# Do additional processing
def do_something2(msg)
  puts msg.body
end
# Replace us-west-2 with the AWS Region you're using for Amazon SQS.
Aws.config.update({region: 'us-west-2'})

poller = Aws::SQS::QueuePoller.new(URL)

poller.poll(idle_timeout: timeout + 1) do |msg|
  if do_something(msg)
    # need more time for processing
    poller.change_message_visibility_timeout(msg, timeout)

    do_something2(msg)
  end
end
# snippet-end:[s3.sqs-ruby-example-visibility-timeout2.rb]
