# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[]
# snippet-sourcedescription:[Creates a queue for handling notifications for an Elastic Transcoder job.]
# snippet-keyword:[Amazon Elastic Transcoder]
# snippet-keyword:[Ruby]
# snippet-service:[elastictranscoder]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.
require 'aws-sdk'
require 'Thread'

# Class which will poll SQS for job state notifications in a separate thread.
# This class is intended for batch-processing.  If you are using a ruby-on-rails
# server, then a better implementation would be to subscribe your server
# directly to the notification topic and process the SNS messages directly.
class SqsQueueNotificationWorker
  
  def initialize(region, queue_url, options = {})
    options = { max_messages: 5, visibility_timeout: 15, wait_time_seconds: 5 }.merge(options)
    @region = region
    @queue_url = queue_url
    @max_messages = options[:max_messages]
    @visibility_timeout = options[:visibility_timeout]
    @wait_time_seconds = options[:wait_time_seconds]
    @handlers = []
  end
  
  def start()
    @shutdown = false
    @thread = Thread.new {poll_and_handle_messages}
  end
  
  def stop()
    @shutdown = true
  end
  
  def add_handler(handler)
    @handlers << handler
  end
  
  def remove_handler(handler)
    @handlers -= [handler]
  end
  
  
  def poll_and_handle_messages()
    sqs_client = AWS::SQS::Client.new(region: @region)
  
    while not @shutdown do
      sqs_messages = sqs_client.receive_message(
        queue_url: @queue_url,
        max_number_of_messages: @max_messages,
        wait_time_seconds: @wait_time_seconds).data[:messages]
      
      next if sqs_messages == nil
      
      sqs_messages.each do |sqs_message|
        notification = JSON.parse(JSON.parse(sqs_message[:body])['Message'])
        @handlers.each do |handler|
          handler.call(notification)
          sqs_client.delete_message(queue_url: @queue_url, receipt_handle: sqs_message[:receipt_handle])
        end
      end
    end
  end
end
