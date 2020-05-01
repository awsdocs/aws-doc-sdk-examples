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

module Aws
  describe SimpleEmailServices do
    let(:simpleemailservices) { SimpleEmailServices.simpleemailservices }
# testing to confirm the aws region inputted are among the allowable regions:
    describe '#awsregion' do
      it 'returns a list of supported regions' do
      awsregion = simpleemailservices.simpleemailservice('aws').awsregion.map(&:name)
      expect(awsregion).to include('us-east-2') &
      expect(awsregion).to include('us-east-1') &
      expect(awsregion).to include('us-west-1') &
      expect(awsregion).to include('us-west-2') &
      expect(awsregion).to include('ap-east-1') &
      expect(awsregion).to include('ap-south-1') &
      expect(awsregion).to include('ap-northeast-3') &
      expect(awsregion).to include('ap-northeast-2') &
      expect(awsregion).to include('ap-southeast-1') &
      expect(awsregion).to include('ap-southeast-2') &
      expect(awsregion).to include('ap-northeast-1') &
      expect(awsregion).to include('ca-central-1') &
      expect(awsregion).to include('cn-north-1') &
      expect(awsregion).to include('cn-northeast-1') &
      expect(awsregion).to include('eu-central-1') &
      expect(awsregion).to include('eu-west-1') &
      expect(awsregion).to include('eu-west-2') &
      expect(awsregion).to include('eu-west-3') &
      expect(awsregion).to include('eu-north-1') &
      expect(awsregion).to include('me-south-1') &
      expect(awsregion).to include('sa-east-1') &
      expect(awsregion).to include('us-gov-east-1') &
      expect(awsregion).to include('us-gov-west-1')
    end

# testing to make sure a fitting error message is outputted for an unverified sender email address
    describe '#sender' do
      it 'returns an error message for an unverified sender address' do
        expect do
          simpleemailservices.simpleemailservice('aws').sender('peccy@@@amazon.com')
        end.to raise_error(ArgumentError, /Email address is not verified. The following identifies failed the check)
      in region US-WEST-2:peccy@@@amazon.com/)

# testing to confirm that an appropriate error message is given for an unverified recipient email address
      describe '#recipient' do
        it 'returns an error message for an unverified recipient address' do
          expect do
            simpleemailservices.simpleemailservice('aws').recipient('peccysfriend@@@amazon.com')
          end.to raise_error(ArgumentError, /Email address is not verified. The following identifies failed the check
          in region US-WEST-2:peccysfriend@@@amazon.com/)

# testing to make sure the intended subject of the email is sent
          describe '#subject' do
            it 'returns the subject as inputted' do
              subject = simpleemailservices.simpleemailservice('aws').subject('Re: Onboarding information')
              expect(subject).to be_kind_of(SimpleEmailServices::SendEmail) &
              expect(subject.name).to eq('Re: Onboarding information')
            end

# testing to make sure the HTML content is returned as inputted
            describe '#htmlbody' do
              it 'returns the HTML content as entered' do
                htmlbody = simpleemailservices.simpleemailservice('aws').htmlbody
                ('<h1>Amazon SES test (AWS SDK for Ruby)</h1>')
            end
            expect(page).to have_content("Amazon SES test (AWS SDK for Ruby)")
          end

# making sure the correct error message is returned if there is an error sending the email
          describe 'an error is returned if the email cannot be sent' do
            it 'returns the correct service error and message if we are missing the sender email address' do
              sendemail.delete('sender')
              result = ses.record(sendemail)
              expect(result).not_to be_success &
              expect(result.sendemail_id).to eq(nil) &
              expect(result.error_message).to include('sender email address is required')
            end

         describe 'Send email API' do
            it 'records submitted email configurations' do
              sendemail = {
                  'sender'  => 'peccy@amazon.com',
                  'recipient' => 'peccysfriend@amazon.com',
                  'configset' => 'ConfigSet',
                  'awsregion' => 'us-west-2',
                  'subject' => 'Re: Onboarding information',
                  'htmlbody' => "'<h1>Amazon SES test (AWS SDK for Ruby)</h1>'\
                                  '<p>This email was sent with <a href="https://aws.amazon.com/ses/">'\
                                  'Amazon SES</a> using the <a href="https://aws.amazon.com/sdk-for-ruby/">'\
                                    'AWS SDK for Ruby</a>.'",
                  'textbody' => 'This email was sent with Amazon using the AWS SDK for Ruby.',
                  'encoding' => 'UTF-8',
                  'resource' => 'ses = Aws::SES::Client.new(region: 'us-west-2')',
                  'contents' => 'ses.send_email(
                                  destination: {
                                    to_addresses: [
                                      recipient
                                     ]
                                   },
                                  message: {
                                    body: {
                                      html: {
                                        charset: encoding,
                                        data: htmlbody
                                       },
                                      text: {
                                        charset: encoding,
                                        data: textbody
                                      }
                                    },
                                      subject: {
                                        charset: encoding,
                                        data: subject
                                      }
                                    },
                                      source: sender,'
                                    )
                                  }
              post '/emailconfig', JSON.generate(sendemail)
            end
          end
          end




















