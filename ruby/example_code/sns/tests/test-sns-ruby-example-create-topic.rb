# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../sns-ruby-example-create-topic"

describe "#topic_created?" do
  let(:sns_client) { Aws::SNS::Client.new(stub_responses: true) }
  let(:name) { "doc-example-topic" }

  it "confirms the topic was created" do
    topic_data = sns_client.stub_data(
      :create_topic
    )
    sns_client.stub_responses(:create_topic, topic_data)
    expect(topic_created?(sns_client, name)).to be
  end
end
