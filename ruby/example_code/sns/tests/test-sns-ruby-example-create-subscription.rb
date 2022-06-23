# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../sns-ruby-example-create-subscription'

describe '#subscription_created?' do
  let(:sns_client) { Aws::SNS::Client.new(stub_responses: true) }
  let(:endpoint) { 'example@server.com' }

  it 'confirms the subscriptions was created' do
    subscriptions_data = sns_client.stub_data(
      :create_subscriptions
    )
    sns_client.stub_responses(:create_subscriptions, subscriptions_data)
    expect(subscription_created?(sns_client, endpoint)).to be
  end
end