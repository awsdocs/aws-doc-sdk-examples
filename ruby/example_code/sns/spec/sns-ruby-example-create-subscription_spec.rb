# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative 'spec_helper'
sns = Aws::SNS::Resource.new(region: 'us-west-2')
     RSpec.describe CreateSubscription do
       let(:createsubscription_client) { Aws::CreateSubscription::Client.new(stub_responses: true) }
       let(:createsubscription) do
         CreateSubscription.new(
             createsubscription_client: createsubscription_client
         )
       end

       describe '#createsubscription' do
         it 'creates a subscription for a topic and displays the resulting ARN' do
           sns_client.stub_responses(
               create_subscription, :subscriptions => [
               { :subscription_protocol => "emailone"
                 :subscription_endpoint => "MyGroovyUser@MyGroovy.com"},
               { :subscription_protocol => "emailtwo",
                 :subscription_endpoint => "TheirGroovyUser@TheirGroovy.com" }
            ]
           )
         end
         create_subscription.create_subscriptions()
       end



