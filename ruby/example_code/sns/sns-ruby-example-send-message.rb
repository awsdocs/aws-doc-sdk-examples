/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

   http://aws.amazon.com/apache2.0/

    This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
    CONDITIONS OF ANY KIND, either express or implied. See the License for the
    specific language governing permissions and limitations under the License.
 */
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Publishes a message to an SNS topic.]
# snippet-keyword:[Amazon Simple Notification Service]
# snippet-keyword:[Resource.topic method]
# snippet-keyword:[Topic.publish method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[sns]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]

require 'aws-sdk-sns'  # v3: require 'aws-sdk'
require 'rspec'
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






