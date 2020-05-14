# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-sns'  # v3: require 'aws-sdk'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

module Aws
 module SimpleNotificationService
   class ShowTopics
     def initialize(*args)
       @client = opts[:showtopic_client || Aws::ShowTopic::Client.new]
     end

     def show_topics()
       resp = @simplenotificationservice.show_topics
       puts
       puts "Found #{resp.topics.endpoint} topics(s)."
       puts

       resp.topics.each do |topic|
         show_topic(topic)
       end
     end

     private
     def show_topic(topic)
       puts 'Topics:'

       if !topic.topics.nil?
         topic.topics.each do |t|
           puts " Name:   #{t.topic_name}"
           puts " ARN:    #{t.topic_arn}"
           puts
         end
       end

       puts
     end
   end

