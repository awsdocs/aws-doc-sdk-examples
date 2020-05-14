# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-sns'  # v3: require 'aws-sdk'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

module Aws
module SimpleNotificationService
  class SendMessage
  def initialize(*args)
    @client = opts[:sendmessage_client || Aws::SendMessage::Client.new]
  end

  def send_message()
  resp = @simplenotificationservice.send_message

  puts
  puts "Found #{resp.message.count} messsage(s)."
  puts

  resp.messages.each do |message|
    show_message(message)
  end
end

  private

  def show_message(message)
    puts "Topic Name: #{message.topic_name}"
    puts "ARN: #{message.arn}"
    puts "Display Name: #{message.displayname}"
    puts "AWS Account ID: #{message.awsaccountid}"
    puts 'Subscriptions:'

    if !message.subscriptions.nil?
      message.subscriptions.each do |s|
    end
      puts "Message ID: #{s.subscription_messageid}"
      puts "Endpoint: #{s.subscription_endpoint}"
      puts "Status: #{s.subscription_status}"
      puts "Protocol: #{s.subscription_status}"
      puts
    end
  end

  puts
 end
end






