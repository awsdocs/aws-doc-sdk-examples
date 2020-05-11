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
sns = Aws::SES::Resource.new(region: 'us-west-2')
      RSpec.describe ListEmails do
        let(:listemails_client) {Aws::ListEmails::Client.new(stub_responses: true) }
        let(:listemails) do
          ListEmails.new(
              listemails_client: listemails_client
          )
        end

        describe '#listemails' do
          it 'list of email addresses that are verified, pending verification, or failed the verification process'
          listemails_client.stub_responses(
              :describe_emails, :email_list => []
          { :email => "example@amazon.com", :status => "Verified" },
              { :email => "sample@amazon.com", :status => "Pending" }
          ]
          )

          listemails.listemails()
        end
      end



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