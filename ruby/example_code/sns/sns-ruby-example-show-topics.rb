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
# snippet-sourcedescription:[Lists your SNS topic ARNs.]
# snippet-keyword:[Amazon Simple Notification Service]
# snippet-keyword:[Resource.topics method]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-service:[sns]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
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

require 'aws-sdk-sns'  # v3: require 'aws-sdk'
require 'rspec'
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

