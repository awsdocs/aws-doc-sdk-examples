# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to display the body of up to 10 pending messages from an
# Amazon Simple Queue Service (Amazon SQS) queue.

# snippet-start:[s3.ruby.sqs-ruby-example-get-messages-with-long-polling.rb]

require 'aws-sdk-sqs'  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon SQS.
sqs = Aws::SQS::Client.new(region: 'us-west-2')

resp = sqs.receive_message(queue_url: URL, max_number_of_messages: 10, wait_time_seconds: 10)

resp.messages.each do |m|
  puts m.body
end
# snippet-end:[s3.ruby.sqs-ruby-example-get-messages-with-long-polling.rb]
