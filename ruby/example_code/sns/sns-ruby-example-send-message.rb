# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# sns-ruby-example-send-message.rb demonstrates how to send a message using an Amazon Simple Notification Service (SNS) topic using
# the AWS SDK for Ruby.

# Inputs:
# - REGION
# - SNS_TOPIC_ARN
# - MESSAGE

# snippet-start:[sns.Ruby.sendMessage]

require 'aws-sdk-sns'  # v2: require 'aws-sdk'

def message_sent?(sns_client, topic_arn, message)

  sns_client.publish(topic_arn: topic_arn, message: message)
rescue StandardError => e
  puts "Error while sending the message: #{e.message}"
  end

def run_me

  topic_arn = 'arn:aws:sns:us-east-1:260778392212:test_topic'
  region = 'us-east-1'
  message = 'MESSAGE' # The text of the message to send.

  sns_client = Aws::SNS::Client.new(region: region)

  puts "Message sending."

  if message_sent?(sns_client, topic_arn, message)
    puts 'The message was sent.'
  else
    puts 'The message was not sent. Stopping program.'
    exit 1
  end
  end

run_me if $PROGRAM_NAME == __FILE__

# snippet-end:[sns.Ruby.sendMessage]
