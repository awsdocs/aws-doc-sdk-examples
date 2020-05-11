/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

   http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
 */

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
                 :subscription_endpoint => "TheirGroovyUser@TheirGroovy.com"}
           ]
           )
         end
         create_subscription.create_subscription()
       end
     end
                                                                      end




module Aws
  describe SimpleNotificationServices do
    let(:simplenotificationservices) { SimpleNotificationServices.simplenotificationservices }
# testing to confirm whether the protocol selection dropdown includes the correct selections: HTTP, HTTPS, Email,
# Email-JSON, Amazon SQS, AWS Lambda, Platform application endpoint, and SMS
    describe '#protocol' do
      it 'returns a list of supported protocol' do
        protocol = simplenotificationservices.simplenotificationservice('aws').protocol.map(&:name)
        expect(protocol).to include('HTTP') &
        expect(protocol).to include('HTTPS') &
        expect(protocol).to include('Email') &
        expect(protocol).to include('Email-JSON') &
        expect(protocol).to include('Amazon SQS') &
        expect(protocol).to include('AWS Lambda') &
        expect(protocol).to include('Platform application endpoint') &
        expect(protocol).to include('SMS')
      end
    end

# testing to make sure a valid Topic ARN inputted matches the one returned to the user
    describe SimpleNotificationServices:SimpleNotificationService do
      describe '#arn' do
        it 'returns the Topic ARN as inputted' do
          arn = simplenotificationservices.simplenotificationservice('aws')
                    .arn('arn:aws:sns:us-west-2:123456789012:MyTopic')
          expect(arn).to be_kind_of(SimpleNotificationServices::ARN) &
          expect(arn.name).to eq('arn:aws:sns:us-west-2:123456789012:MyTopic')

# testing to check whether an appropriate error message is returned for an invalid Topic ARN entered
          it 'returns an error message for an invalid Topic ARNs' do
            expect do
              simplenotificationservices.simplenotificationservice('aws').arn('arn1234')
           end.to raise_error(ArgumentError, /Couldn't create subscription.Error code: InvalidParameter - Error message:
           Invalid parameter: TopicArn Reason: An ARN must have at least 6 elements, not 1/)
          end
        end

# testing to confirm whether a valid email address inputted matches the one returned to the user
        describe '#endpoint' do
          it 'returns the email address as inputted' do
            endpoint = simplenotificationservices.simplenotifictionservice('aws').arn('peccy@amazon.com')
            expect(endpoint).to be_kind_of(SimpleNotificationServices::Endpoint) &
            expect(endpoint.name).to eq('endpoint:peccy@amazon.com')

# testing to make sure a fitting error message is out putted for an invalid email address entered
            it 'returns an error message for an invalid email address' do
              expect do
                simplenotificationservices.simplenotificationservice('aws').endpoint('peccy@@@@amazon.com')
              end.to raise_error(ArgumentError, /Enter a valid email address(for example, test@example.com/)
            end
          end
          end
          end

# testing to make sure the subscription API saves and displays the desired subscription parameters
      describe 'Create Subscription API' do
        it 'records submitted subscription parameters' do
        createsubscription = {
            'arn'      => 'arn:aws:sns:us-west-2:123456789012:MyTopic',
            'protocol' => 'email',
            'endpoint' => 'peccy@amazon.com'
        }
        post '/subscriptionconfig', JSON.generate(createsubscription)
        expect(last_response.status).to eq(200)
      end