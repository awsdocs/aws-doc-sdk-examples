require_relative 'spec_helper'
sns = Aws::SNS::Resource.new(region: 'us-west-2')

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

