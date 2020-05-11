/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

   http://aws.amazon.com/apache2.0/

    This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
    CONDITIONS OF ANY KIND, either express or implied. See the License for the
    pecific language governing permissions and limitations under the License.
*/

require_relative 'spec_helper'
sqs = Aws::SQS::Resource.new(region: 'us-west-2')

module Aws
describe SimpleQueueServices do
let(:simplequeueservices) { SimpleQueueServices.simplequeueservices }
# testing to confirm whether the attribute selection options include the selections: Default Visbility Timeout,
# Message Retention Period, Maximum Message Size, Delivery Delay, Receive Message Wait Time, Content-Based Deduplication
          describe '#attributes' do
            it 'returns a list of supported attributes' do
              attributes = simplequeueservices.simplequeueservice('aws').queue.map(&:name)
              expect(attributes).to include('Default Visibility Timeout')
              expect(attributes).to include('Message Retention Period')
              expect(attributes).to include('Maximum Message Size')
              expect(attributes).to include('Delivery Delay')
              expect(attributes).to include('Receive Message Wait Time')
              expect(attributes).to include('Content-Based Deduplication')
            end
          end

# testing to confirm whether a valid queue name inputted matches the one returned to the user
              describe '#name' do
                it 'returns the name as inputted' do
                  name = simplequeueervices.simplequeueservice('aws').arn('MyQueue.fifo')
                  expect(name).to be_kind_of(SimpleQueueServices::Name) &
                                          expect(queue.name).to eq('MyQueue.fifo')

# testing to make sure a fitting error message is out putted for an invalid queue name entered
                  it 'returns an error message for an invalid queue name' do
                    expect do
                      simplequeueservices.simplequeueservice('aws').name('9999.jpeg')
                    end.to raise_error(ArgumentError, /Enter a valid queue name(for example, MyQueue.fifo/)
                  end
                end
              end
            end

# testing to make sure the queue creation API saves and displays the desired queue
            describe 'Create Queue API' do
              it 'records submitted queue parameters' do
                createqueue = {
                    'name'      => 'MyQueue.fifo',
                    'url' => 'https://sqs.us-west-2.amazonaws.com/751363017507/MyQueue.fifo',
                    'arn' => 'arn:aws:sqs:us-west-2:751363017507:MyQueue.fifo'
                }
                post '/queueconfig', JSON.generate(createqueue)
                expect(last_response.status).to eq(200)
              end