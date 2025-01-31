# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-sns'
require 'rspec'
require_relative '../create_subscription'

RSpec.describe SubscriptionService do
  let(:sns_client) { Aws::SNS::Client.new }
  let(:service) { SubscriptionService.new(sns_client) }
  let(:topic_arn) { 'your-test-topic-arn' } # Replace with your test topic ARN
  let(:protocol) { 'email' }
  let(:endpoint) { 'your-email@example.com' } # Replace with a valid email for testing

  describe '#create_subscription' do
    it 'creates a subscription successfully' do
      VCR.use_cassette('create_subscription') do # Using VCR to record AWS interactions
        expect(service.create_subscription(topic_arn, protocol, endpoint)).to eq(true)
      end
    end

    context 'when AWS service error occurs' do
      before do
        allow(sns_client).to receive(:subscribe).and_raise(Aws::SNS::Errors::ServiceError.new('', 'test error'))
      end

      it 'logs an error and returns false' do
        expect(service.create_subscription(topic_arn, protocol, endpoint)).to eq(false)
      end
    end
  end
end
