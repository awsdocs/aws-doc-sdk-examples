# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to redirects dead-letters from one Amazon Simple Queue Service (Amazon SQS)
# queue to another.

# snippet-start:[s3.sqs-ruby-example-redirect-queue-deadletters.rb]
require 'aws-sdk-sqs'  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon SQS.
sqs = Aws::SQS::Client.new(region: 'us-west-2')

sqs.set_queue_attributes({
  queue_url: URL,
  attributes:
    {
      'RedrivePolicy' => "{\"maxReceiveCount\":\"5\", \"deadLetterTargetArn\":\"#{ARN}\"}"
    }
})
# snippet-end:[s3.sqs-ruby-example-redirect-queue-deadletters.rb]
