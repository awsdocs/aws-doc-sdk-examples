# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../sqs-ruby-example-create-queue"

describe "#queue_created?" do
  let(:queue_name) { "my-queue" }
  let(:queue_url) { "https://sqs.us-west-2.amazonaws.com/111111111111/" + queue_name }
  let(:sqs_client) do
    Aws::SQS::Client.new(
      stub_responses: {
        create_queue: {
          queue_url: queue_url
        }
      }
    )
  end

  it "creates a queue" do
    expect(queue_created?(sqs_client, queue_name)).to be(true)
  end
end
