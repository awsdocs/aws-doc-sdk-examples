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
  sqs = Aws::SQS::Resource.new(region: 'us-west-2')

  module Aws
    describe SimpleQueueServices do
      let(:simplequeueservices) { SimpleQueueServices.simplequeueservices }
# testing to confirm whether the Message Attributes options include: Message Identifier, MD5 of Body, and
# Sequence Number
      describe '#attributes' do
        it 'returns a list of supported attributes' do
          attributes = simplequeueservices.simplequeueservice('aws').queue.map(&:name)
          expect(attributes).to include('Name')
          expect(attributes).to include('Type')
          expect(attributes).to include('Value')
          expect(attributes).to include('Add Attribute')
        end
      end

# testing to confirm whether the Message Body options include: the Body Text, Message Group ID, and Message Deduplication ID
      describe '#body' do
        it 'returns a list of supported attributes' do
          body = simplequeueservices.simplequeueservice('aws').queue.map(&:name)
          expect(body).to include('Message Group ID')
          expect(body).to include('Message Deduplication ID')
          expect(body).to include('Body')
        end
      end


# testing to make sure the queue creation API saves and displays the desired queue
    describe 'Send Message API' do
      it 'records submitted queue parameters' do
        sendmessage = {
            'message identifier' => 'af84f2eb-fdbf-4983-95da-42421d12aaf7',
            'md5 of body' => '63346d9b3c29ca03e984693c7200f3b7',
            'sequence number' => '18853452305076463877'
        }
        post '/messageconfig', JSON.generate(sendmessage)
        expect(last_response.status).to eq(200)
      end