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
# snippet-sourcedescription:[Creates an SNS topic.]
# snippet-keyword:[Amazon Simple Notification Service]
# snippet-keyword:[create_topic method]
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
         class CreateTopic
           def initialize(*args)
             @client = opts[:createtopic_client || Aws::CreateTopic::Client.new]
           end

           def create_topic()
             resp = @simplenotificationservice.create_topic
             puts
             puts "Found #{resp.topic.count} topic(s)."
             puts

             resp.topics.each do |topics|
               return false unless (topic =~ ^ [A - Za - z0 - 9)_ -] + $)
             end
           end
         end


         private

         def show_topic(topic)
           puts 'Details:'

           if !subscription.details.nil?
             subscription.details.each do |d|
               puts "  ARN:  #{d.details_arn}"
               puts "  Topic name: #{d.details_topicname}"
               puts "  Display name: #{d.details.displayname}"
               puts "  AWS account ID: #{d.details.awsaccountid}"
             end
           end
           puts
         end
       end









