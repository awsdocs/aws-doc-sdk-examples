# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../sqs-ruby-example-send-message"

describe "#message_sent?" do
  let(:queue_name) { "my-queue" }
  let(:queue_url) { "https://sqs.us-west-2.amazonaws.com/111111111111/" + queue_name }
  let(:message_body) { "This is my message." }
  let(:sqs_client) do
    Aws::SQS::Client.new(
      stub_responses: {
        send_message: {
          message_id: "3fd53020-3da2-4a7e-afef-d36dfEXAMPLE"
        }
      }
    )
  end

  it "sends a message" do
    expect(message_sent?(sqs_client, queue_url, message_body)).to be(true)
  end
end
