# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# sns-ruby-example-create-subscription.rb demonstrates how to create an Amazon Simple Notification Service (SNS) subscription using
# the AWS SDK for Ruby.

# Inputs:
# - REGION
# - SNS_TOPIC_ARN
# - EMAIL_ADDRESS

# snippet-start:[sns.Ruby.createSubscription]

require "aws-sdk-sns"  # v2: require 'aws-sdk'

def subscription_created?(sns_client, topic_arn, protocol, endpoint)

  sns_client.subscribe(topic_arn: topic_arn, protocol: protocol, endpoint: endpoint)

rescue StandardError => e
  puts "Error while creating the subscription: #{e.message}"
end

# Full example call:
def run_me

  protocol = "email"
endpoint = "EMAIL_ADDRESS"
topic_arn = "TOPIC_ARN"
region = "REGION"

sns_client = Aws::SNS::Client.new(region: region)

puts "Creating the subscription."

  if subscription_created?(sns_client, topic_arn, protocol, endpoint)
    puts "The subscriptions was created."
  else
    puts "The subscription was not created. Stopping program."
    exit 1
  end
end

run_me if $PROGRAM_NAME == __FILE__

# snippet-end:[sns.Ruby.createSubscription]
