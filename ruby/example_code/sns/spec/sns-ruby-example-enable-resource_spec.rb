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

RSpec.describe EnableResource do
  let(:enableresource_client) { Aws::EnableResource::Client.new(stub_responses true)}
end
  let(:enableresource) do
  EnableResource.new(
  enableresource_client: enableresource_client
         )
  end

describe '#enableresource' do
   it 'enables the resource with the ARN to publish to the topic' do
     enableresource_client.stub_responses(
     :enable_resource, :resources => [
      { :attribute_name => "Policy",
       :attribute_value => "policy"},
       { :attribute_name => "AnotherPolicy",
       :attribute_value => "anotherapolicy"}
        ]
        )
    enableresource.enableresource()
     end
      end
     end



# testing to make sure a valid Topic ARN is inputted
describe SimpleNotificationServices:SimpleNotificationService do
  describe '#arn' do
    it 'returns the Topic ARN as inputted' do
      topic = simplenotificationservices.simplenotificationservice('aws').topic
                  ('arn:aws:sns:us-west-2:123456789012:MyTopic')
      expect(topic).to be_kind_of(SimpleNotificationServices::Topic) &
      expect(my-topic-arn).to eq('arn:aws:sns:us-west-2:123456789012:MyTopic')

# testing to check whether an appropriate error message is returned for an invalid Topic ARN entered
      it 'returns an error message for an invalid Topic ARNs' do
        expect do
          simplenotificationservices.simplenotificationservice('aws').topic('arn1234')
          end.to raise_error(ArgumentError, /Couldn't create subscription.Error code: InvalidParameter - Error message:
          Invalid parameter: TopicArn Reason: An ARN must have at least 6 elements, not 1/)
      end

# testing to make sure the enable resource API saves and displays the correct topic attributes
describe 'Enable Resource API' do
        it 'records specified topic attributes' do
          enableresource = {
              'attribute name' => "Policy",
              'attribute value' => policy
          }
          post '/resourceconfig', JSON.generate(enableresource)
        end
      end

