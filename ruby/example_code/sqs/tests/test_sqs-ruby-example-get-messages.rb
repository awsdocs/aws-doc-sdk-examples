# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative '../sqs-ruby-example-get-messages'

describe '#receive_messages' do
  let(:queue_name) { 'my-queue' }
  let(:queue_url) { 'https://sqs.us-west-2.amazonaws.com/111111111111/' + queue_name }
  let(:max_number_of_messages) { 10 }
  let(:sqs_client) do
    Aws::SQS::Client.new(
      stub_responses: {
        receive_message: {
          messages: [
            {
              body: 'This is my first message.'
            },
            {
              body: 'This is my second message.'
            }
          ]
        }
      }
    )
  end

  it 'receives messages' do
    expect{ receive_messages(sqs_client, queue_url, max_number_of_messages) }.to_not raise_error
  end
end
