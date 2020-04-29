require_relative 'spec_helper'
sns = Aws::SES::Resource.new(region: 'us-west-2')

module Aws
  describe SimpleEmailServices do
    let(:simpleemailservices) { SimpleEmailServices.simpleemailservices }

# testing to make sure a valid email address is returned after being inputted
    describe '#email' do
    it 'returns a valid email address as inputted' do
      email = simpleemailservices.simpleemailservice('aws').email('peccy@amazon.com')
      expect(email).to be_kind_of(SimpleEmailServices::Email) &
      expect(email.name).to eq('peccy@amazon.com')
# testing to make sure a fitting error message is returned for an invalid email address
    it 'returns an error message for an invalid email address' do
      expect do simpleemailservices.simpleemailservice('aws').email('peccy@@@amazon.com')
        end.to raise_error(ArgumentError, The email address you entered does not appear to be valid.
            Please try entering the email address again/)
# testing to ensure that the service receives a client and an email address
      it 'ensures the client receives an email and accompanying user'
        client.should_receive(:email).with(/.*User/)
      end
    end
  end

end