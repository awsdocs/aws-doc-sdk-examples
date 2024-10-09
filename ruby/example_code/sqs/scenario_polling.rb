# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
# This code example demonstrates how to :
# 1. Create a queue and set it for long polling.
# 2. Set long polling for an existing queue.
# 3. Set long polling when receiving messages for a queue.

# snippet-start:[ruby.example_code.sqs.ScenarioPolling]

require 'aws-sdk-sqs' # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon SQS.

sqs = Aws::SQS::Client.new(region: 'us-west-2')

# Create a queue and set it for long polling.
new_queue_name = 'new-queue'

create_queue_result = sqs.create_queue({
                                         queue_name: new_queue_name,
                                         attributes: {
                                           'ReceiveMessageWaitTimeSeconds' => '20' # Wait 20 seconds to receive messages.
                                         }
                                       })

puts create_queue_result.queue_url

# Set long polling for an existing queue.
begin
  existing_queue_name = 'existing-queue'
  existing_queue_url = sqs.get_queue_url(queue_name: existing_queue_name).queue_url

  sqs.set_queue_attributes({
                             queue_url: existing_queue_url,
                             attributes: {
                               'ReceiveMessageWaitTimeSeconds' => '20' # Wait 20 seconds to receive messages.
                             }
                           })
rescue Aws::SQS::Errors::NonExistentQueue
  puts "Cannot set long polling for a queue named '#{existing_queue_name}', as it does not exist."
end

# Set long polling when receiving messages for a queue.

# 1. Using receive_message.
begin
  receive_queue_name = 'receive-queue'
  receive_queue_url = sqs.get_queue_url(queue_name: receive_queue_name).queue_url

  puts 'Begin receipt of any messages using receive_message...'
  receive_message_result = sqs.receive_message({
                                                 queue_url: receive_queue_url,
                                                 wait_time_seconds: 10,
                                                 # Poll messages for 10 seconds at a time. Default: 0.
                                                 max_number_of_messages: 10 # Receive up to 10 messages, if there are that many.
                                               })

  puts "Received #{receive_message_result.messages.count} message(s)."
rescue Aws::SQS::Errors::NonExistentQueue
  puts "Cannot receive messages using receive_message for a queue named '#{receive_queue_name}', as it does not exist."
end

# 2. Using Aws::SQS::QueuePoller
# NOTE: Using this class, messages are received using a "long poll" of 20 seconds.
# If you prefer to use settings configured in the queue, then pass a nil value for :wait_time_seconds.
begin
  puts 'Begin receipt of any messages using Aws::SQS::QueuePoller...'
  puts '(Will keep polling until no more messages available for at least 60 seconds.)'
  poller = Aws::SQS::QueuePoller.new(receive_queue_url)

  poller_stats = poller.poll({
                               wait_time_seconds: 30, # Poll messages for 30 seconds at a time. Default: 20.
                               max_number_of_messages: 10, # Return up to 10 messages at a time.
                               idle_timeout: 60 # Terminate polling loop after 60 seconds.
                             }) do |messages|
    messages.each do |message|
      puts "Message body: #{message.body}"
    end
  end
  # NOTE: If poller.poll is successful, all received messages are automatically deleted from the queue.

  puts 'Poller stats:'
  puts "  Polling started at: #{poller_stats.polling_started_at}"
  puts "  Polling stopped at: #{poller_stats.polling_stopped_at}"
  puts "  Last message received at: #{poller_stats.last_message_received_at}"
  puts "  Number of polling requests: #{poller_stats.request_count}"
  puts "  Number of received messages: #{poller_stats.received_message_count}"
rescue Aws::SQS::Errors::NonExistentQueue
  puts "Cannot receive messages using Aws::SQS::QueuePoller for a queue named '#{receive_queue_name}', as it does not exist."
end
# snippet-end:[ruby.example_code.sqs.ScenarioPolling]
