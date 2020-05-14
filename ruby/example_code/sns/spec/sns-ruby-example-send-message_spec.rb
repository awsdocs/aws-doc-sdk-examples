# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative 'spec_helper'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

 RSpec.describe SendMessage do
   let(:sendmessage_client) {Aws::SendMessage::Client.new(stub_responses: true) }
   let(:sendmessage) do
     SendMessage.new
     sendmessage_client: sendmessage_client
     )
   end

   describe '#send_message' do
     it 'sends a message to all subscribers to the Amaon SNS topic and corresponding ARN' do
       :sendmessage_client.stub_responses(
          :send_messages, :messages => [
          :message_id => "ff52998e-d6b7-4f20-b656-bd1dee5b5a52"
          :message_endpoint => "example@amazon.com"
          :message_status => "confirmed"
          :message_protocol => "email"
       }
       {  :message_id => "rf52998e-d6b7-4f20-b656-bd1dee5b5a52"
          :message_endpoint => "sample@amazon.com"
          :message_status => "confirmed"
          :message_protocol => "email"
       }
       ]
       sendmessage.send_messages()
     end
   end
 end





