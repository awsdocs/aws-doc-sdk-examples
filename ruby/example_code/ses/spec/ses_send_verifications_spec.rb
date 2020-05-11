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

module Aws
  describe SimpleEmailServices do
    let(:simpleemailservices) { SimpleEmailServices.simpleemailservices }

    RSpec.describe ShowVerification do
      let (:sendverification_client) {Aws::SendVerification::Client.new(stub_responses: true) }
      let (:sendverification) do
        SendVerification.new(
            :sendverification_client: sendverification_client
        )
      end

      describe '#sendverification' do
        it 'verifies one or more identities - email addresses or domain names, before allowing the sending of emails' do
          sendverification_client.stub_responses(
              :sendverifications, :verifications => [
              { :verification_emailaddressidentities => "example@amazon.com",
                :verification_verificationstatus => "Verified" },
              {  :verification_emailaddressidentities => "sample@amazon.com",
                 :verification_verificationstatus => "Pending Verification (Resend)" }
          ]
          )
          sendverification.sendverification()
        end
      end
    end


# testing to confirm the protocol selection dropdown covers the correct scenarios: successful delivery, bounce,
# automatic responses, complaint, recipient address on suppression list
    describe '#email' do
      it 'returns a list of supported scenarios' do
        email = simpleemailservices.simpleemailservice('aws').email.map(&:name)
      expect(email).to include('Successful delivery') &
      expect(email).to include('Bounce') &
      expect(email).to include('Automatic responses') &
      expect(email).to include('Complaint') &
      expect(email).to include('Recipient address on suppression list')
      end
      # testing to ensure that the service receives a client and an email address
      it 'ensures the client receives an email and accompanying user'
      client.should_receive(:email).with(/.*User/)

      #testing to make sure an error message is given if sending is not verified
      describe 'an error is returned if a successful send verification cannot be returned' do
        it 'returns a fitting service error message if we are unable to verify the email identity of the recipient' do
          sendverification.delete('email')
          result = ses.record(sendverification)
          expect(result).not_to be_success &
          expect(result.sendverification_id).to eq(nil) &
          expect(result.error_message).to include('recipient email identity verification is required')
        end
    end
