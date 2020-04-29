# frozen_string_literal: true

require_relative 'spec_helper'
SNS = Aws::SNS::Resource.new(region: 'us-west-2')

# testing to confirm a valid email address inputted matches the one returned to the user
describe '#endpoint' do
  it 'returns the email address as inputted' do
    endpoint = simplenotificationservices.simplenotifictionservice('aws').arn('peccy@amazon.com')
    expect(endpoint).to be_kind_of(SimpleNotificationServices::Endpoint) &
    expect(endpoint.name).to eq('endpoint:peccy@amazon.com')

   it 'does not return an email address other than the one inputted' do
      endpoint = simplenotificationservices.simplenotifictionservice('aws').arn('peccy@amazon.com')
      expect(endpoint).to be_kind_of(SimpleNotificationServices::Endpoint) &
      expect(endpoint.name).not_to eq('endpoint:notpeccy@smazon.com')
    end
  end
end
