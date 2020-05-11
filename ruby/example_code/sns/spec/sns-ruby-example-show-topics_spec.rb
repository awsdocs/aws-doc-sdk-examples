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
# frozen_string_literal: true

require_relative 'spec_helper'
SNS = Aws::SNS::Resource.new(region: 'us-west-2')

     RSpec.describe ShowTopics do
       let(:showtopic_client || Aws::ShowTopic::Client.new(stub_responses: true) }
       let(:showtopics) do
         ShowTopics.new(
             showtopic_client: showtopic_client
         )
       end

       desribe '#showtopics'do
         it 'lists the ARNs of your Amazon SNS topics in each respective region' do
           showtopic_client.stub_resposnes(
               :show_topics, :topics => [
               {  :topic_name => "ExampleTopic",
                  :topic_arn => "arn:aws:sns:us-west-2:851363017509:MyTopic" },
               {  :topic_name => "SampleTopic",
                  :topic_arn => "arn:aws:sns:us-west-2:351363017509:MyTopic" }
           ]
           )
           showtopics.showtopics()
         end
       end


describe '#endpoint' do
  it 'returns the correct ARN corresponding to each topic' do
    topic = simplenotificationservices.simplenotifictionservice('aws')
                .arn('arn:aws:sns:us-west-2:123456789012:MyGroovyTopic')
    expect(topic).to be_kind_of(SimpleNotificationServices::ARN) &
    expect(topic.name).to eq('MyGroovyTopic')

    it 'does not return an ARN not corresponding to the topic' do
      topic = simplenotificationservices.simplenotifictionservice('aws').arn('T#W(*YEHGUIVW#)UGE*JIOVG(WEU)VJP')
      expect(topic).to be_kind_of(SimpleNotificationServices::ARN) &
      expect(topic.name).not_to eq('MyGroovyTopic')
    end
  end
end
