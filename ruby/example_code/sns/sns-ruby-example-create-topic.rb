# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose:
# sns-ruby-example-create-topic.rb demonstrates how to create an Amazon Simple Notification Service (SNS) topic using
# the AWS SDK for Ruby.

# Inputs:
# - REGION
# - TOPIC_NAME

# snippet-start:[sns.Ruby.createTopic]

require 'aws-sdk-sns'  # v2: require 'aws-sdk'

def topic_created?(sns_client, topic_name)

sns_client.create_topic(name: topic_name)
rescue StandardError => e
  puts "Error while creating the topic named '#{topic_name}': #{e.message}"
end

# Full example call:
def run_me
  topic_name = 'test_topic'
  region = 'us-east-1'

  sns_client = Aws::SNS::Client.new(region: region)

  puts "Creating the topic '#{topic_name}'..."

  if topic_created?(sns_client, topic_name)
    puts 'The topic was created.'
  else
    puts 'The topic was not created. Stopping program.'
    exit 1
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[sns.Ruby.createTopic]
