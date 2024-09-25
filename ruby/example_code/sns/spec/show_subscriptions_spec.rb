# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-sns'
require_relative '../show_subscriptions'

RSpec.describe SnsSubscriptionLister do
  let(:sns_client) { Aws::SNS::Client.new }
  let(:topic_arn) { 'SNS_TOPIC_ARN' } # Replace with your SNS topic ARN
  let(:lister) { SnsSubscriptionLister.new(sns_client) }

  describe '#list_subscriptions' do
    it 'lists subscriptions without raising an error' do
      expect { lister.list_subscriptions(topic_arn) }.not_to raise_error
    end
  end
end
