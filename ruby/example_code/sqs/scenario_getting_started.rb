# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to:
# 1. Get a list of your queues.
# 2. Create a queue.
# 3. Get the queue's URL.
# 4. Delete the queue.

# snippet-start:[ruby.example_code.sqs.ScenarioGettingStarted]

require "aws-sdk-sqs"  # v2: require 'aws-sdk'
# Replace us-west-2 with the AWS Region you're using for Amazon SQS.
sqs = Aws::SQS::Client.new(region: "us-west-2")

# Get a list of your queues.
sqs.list_queues.queue_urls.each do |queue_url|
  puts queue_url
end

# Create a queue.
queue_name = "my-queue"

begin
  sqs.create_queue({
    queue_name: queue_name,
    attributes: {
      "DelaySeconds" => "60", # Delay message delivery for 1 minute (60 seconds).
      "MessageRetentionPeriod" => "86400" # Delete message after 1 day (24 hours * 60 minutes * 60 seconds).
    }
  })
rescue Aws::SQS::Errors::QueueDeletedRecently
  puts "A queue with the name '#{queue_name}' was recently deleted. Wait at least 60 seconds and try again."
  exit(false)
end

# Get the queue's URL.
queue_url = sqs.get_queue_url(queue_name: queue_name).queue_url
puts queue_url

begin
  # Create a message with three custom attributes: Title, Author, and WeeksOn.
  send_message_result = sqs.send_message({
                                           queue_url: queue_url,
                                           message_body: "Information about current NY Times fiction bestseller for week of 2016-12-11.",
                                           message_attributes: {
                                             "Title" => {
                                               string_value: "The Whistler",
                                               data_type: "String"
                                             },
                                             "Author" => {
                                               string_value: "John Grisham",
                                               data_type: "String"
                                             },
                                             "WeeksOn" => {
                                               string_value: "6",
                                               data_type: "Number"
                                             }
                                           }
                                         })
  puts send_message_result.message_id
rescue Aws::SQS::Errors::NonExistentQueue
  puts "A queue named '#{queue_name}' does not exist."
  exit(false)
end

# Receive the message in the queue.
receive_message_result = sqs.receive_message({
                                               queue_url: queue_url,
                                               message_attribute_names: ["All"], # Receive all custom attributes.
                                               max_number_of_messages: 1, # Receive at most one message.
                                               wait_time_seconds: 0 # Do not wait to check for the message.
                                             })

# Display information about the message.
# Display the message's body and each custom attribute value.
receive_message_result.messages.each do |message|
  puts message.body
  puts "Title: #{message.message_attributes["Title"]["string_value"]}"
  puts "Author: #{message.message_attributes["Author"]["string_value"]}"
  puts "WeeksOn: #{message.message_attributes["WeeksOn"]["string_value"]}"

  # Delete the message from the queue.
  sqs.delete_message({
                       queue_url: queue_url,
                       receipt_handle: message.receipt_handle
                     })
end

# Delete the queue.
sqs.delete_queue(queue_url: queue_url)
# snippet-end:[ruby.example_code.sqs.ScenarioGettingStarted]
