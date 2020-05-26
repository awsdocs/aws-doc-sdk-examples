# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative 'spec_helper'
SNS = Aws::SNS::Resource.new(region: 'us-west-2')

 RSpec.describe ShowTopics do
   let(:showtopic_client || Aws::ShowTopic::Client.new(stub_responses: true) }
   let(:showtopics) do
     ShowTopics.new(
         showtopic_client: showtopic_client
     )
   end

   describe '#showtopics'do
     it 'lists the ARNs of your Amazon SNS topics in each respective AWS Region' do
       showtopic_client.stub_resposnes(
           :show_topics, :topics => [
           {  :topic_name => "ExampleTopic",
              :topic_arn => "arn:aws:sns:us-west-2:851363017509:MyTopic" },
           {  :topic_name => "SampleTopic",
              :topic_arn => "arn:aws:sns:us-west-2:351363017509:MyTopic" }
       ]
       )
       showtopic.showtopics()
     end


