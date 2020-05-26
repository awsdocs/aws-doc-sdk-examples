# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative 'spec_helper'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

 RSpec.describe Createtopic do
   let(:createtopic_client) { Aws::CreateTopic::Client.new(stub_responses: true)}
   let(:createtopic) do
     CreateTopic.new(
         createtopic_client: createtopic_client
     )
   end

   describe '#createtopic' do
     it 'creates the topic with the desired topic name and AWS Region and displays the resulting topic ARN'
     createtopic_client.stub_responses(
         create_topic, :topics => [
         { :topic_arn => "arn:aws:sqs:*:444455556666:queue1",
           :topic_topicname => "ExampleTopic" },
         { :topic_arn => "arn:aws:sqs:*:444455556666:queue2",
           :topic_topicname => "AnotherExampleTopic" }
       ]
     )
   end
   create_topic.create_topics()
   )
 end




