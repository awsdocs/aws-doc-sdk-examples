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

require_relative 'spec_helper'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

module Aws
  describe SimpleNotificationServices do
    let(:simplenotificationservices) { SimpleNotificationServices.simplenotificationservices }

# testing to make sure a valid topic name inputted matches the one returned to the user
    describe SimpleNotificationServices:SimpleNotificationService do
      describe '#topicname' do
        it 'returns the topic name as inputted' do
          topicname = simplenotificationservices.simplenotificationservice('aws').topicname('MyGroovyTopic')
          expect(topicname).to be_kind_of(SimpleNotificationServices::TopicName) &
          expect(topicname.name).to eq('MyGroovyTopic')
# testing to check whether an appropriate error message is returned for an invalid topic name entered
          it 'returns an error message for an invalid topic names' do
            expect do
              simplenotificationservices.simplenotificationservice('aws').topicname('TheTopicName?')
            end.to raise_error(ArgumentError, /The topic name must be maximum 256 characters long, including hyphens (-)
            and underscores (_)./)
          end
        end

# testing to make sure whether the topic API saves the desired topic creation parameters
        describe 'Create Topic API' do
          it 'records submitted topic parameters' do
          createtopic = {
              'topicname' => 'MyGroovyTopic',
              'arn' => 'arn:aws:sns:us-west-2:123456789012:MyGroovyTopic',
              'displayname' => 'MyGroovyTopic',
              'accountid' => '123456789012'
          }
          post '/topicconfig', JSON.generate(createtopic)
        end