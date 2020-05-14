# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
#
require_relative 'spec_helper'
SNS = Aws::SNS::Resource.new(region: 'us-west-2')

RSpec.describe ShowSubscription do
 let(:showsubscription_client) {Aws::ShowSubscription::Client.new(stub_responses: true) }
 let(:showsubscriptions) do
   ShowSubscriptions.new
   showsubscription_client: showsubscription_client
   )
 end

 describe '#show_subcriptions' do
   it 'lists the email addresses of the Amazon SNS subscriptions for a topic with an associated ARN' do
     showsubscription_client.stub_responses(
         :show_subscriptions, :subscriptions => [
         { :subscription_endpoint => "example@amazon.com",
           :subscription_id => "ff52998e-d6b7-4f20-b656-bd1dee5b5a52",
           :subscription_status => "Confirmed",
           :subscription_protocol => "Email" },
         { :subscription_endpoint => "sample@amazon.com",
           :subscription_id => "zf52998e-d6b7-4f20-b656-bd1dee5b5a52",
           :subscription_status => "Confirmed",
           :subscription_protocol => "Email" }
     ]
     )
   end
   show_subscription.show_subscriptions()
 end
end
                                                                      end
