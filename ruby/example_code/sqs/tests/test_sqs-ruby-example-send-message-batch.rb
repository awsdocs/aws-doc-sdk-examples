# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../sqs-ruby-example-send-message-batch"

describe "#messages_sent?" do
  let(:queue_name) { "my-queue" }
  let(:queue_url) { "https://sqs.us-west-2.amazonaws.com/111111111111/" + queue_name }
  let(:entries) do
    [
      {
        id: "Message1",
        message_body: "This is the first message."
      },
      {
        id: "Message2",
        message_body: "This is the second message."
      }
    ]
  end
  let(:sqs_client) do
    Aws::SQS::Client.new(
      stub_responses: {
        send_message_batch: {
          successful: [
            {
              id: "Message1",
              md5_of_message_body: "c49f3d613918add9690e54615EXAMPLE",
              message_id: "3fd53020-3da2-4a7e-afef-d36dfEXAMPLE"
            },
            {
              id: "Message2",
              md5_of_message_body: "8fa387fa05fc48179f1f052cbEXAMPLE",
              message_id: "7365097c-b104-4f41-b7b7-fa31eEXAMPLE"
            }
          ],
          failed: []
        }
      }
    )
  end

  it "sends messages in a batch" do
    expect(messages_sent?(sqs_client, queue_url, entries)).to be(true)
  end
end
