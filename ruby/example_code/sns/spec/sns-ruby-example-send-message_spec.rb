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
  describe SimpleNotificationServices do
    let(:simplenotificationservices) { SimpleNotificationServices.simplenotificationservices }
# testing to confirm the attribute type dropdown under Message attributes includes the correct selections: String,
# String.Array, Number, and Binary
    describe '#type' do
      it 'returns a list of supported attribute types' do
        type = simplenotificationservices.simplenotificationservice('aws').type.map(&:name)
      expect(type).to include('String') &
      expect(type).to include('String.Array') &
      expect(type).to include('Number') &
      expect(type).to include('Binary')
    end
  end

# testing to make sure a valid subject inputted matches the one returned to the user
  describe SimpleNotificationServices:SimpleNotificationService do
    describe '#subject' do
      it 'returns the subject name as inputted' do
        subject = simplenotificationservices.simplenotificationservice('aws').subject('Re: Onboarding information')
        expect(subject).to be_kind_of(SimpleNotificationServices::Subject) &
        expect(subject.name).to eq('Re: Onboarding information')
# testing to check whether an appropriate error message is returned for an invalid subject name entered
        it 'returns an error message for an invalid subject names' do
          expect do
            simplenotificationservices.simplenotificationservice('aws').subject
            ('!#%(*#UIHFJ$#)*HUEKGJNR$EGIURJKNW#(TIEOJgnv23498ytweufgihjdR&#*()(I*#()@QOIJNBEIJVNBNEHUG(#(*HUGVENJHU
              ERHUIGJKWEG*UWO#IJGKW*U$OGIJK#()IKG()#I$WPOKG#$W_)IPGOK$)_GIPOK:L$)_OP{GK:$L<)O$PGIK:L$)OPIGK:$)IOPKG:L')
          end.to raise_error(ArgumentError, /Enter a valid subject./)
        end
      end

# testing to make sure the message body is not left blank in the message structure
      describe SimpleNotificationServices:SimpleNotificationService do
        describe '#messagebody' do
          it 'returns an error message for a blank message body' do
            expect do
              simplenotificationservices.simplenotificationservice('aws').messagebody
              (' ')
            end.to raise_error(ArgumentError, /The message body must not be blank/)

# testing to ensure the send message API saves and shows the desired message parameters
        describe 'Send Message API' do
           it 'records submitted message parameters' do
             sendmessage = {
                'subject' => 'Re: Onboarding information',
                'timetolive' => '30',
                'messagebody' => 'Hello, Peccy!',
                'type' => 'Binary',
                'name' => '1000',
                'value'=> 'value 1',
                'addanotherattribute' => ''
                }
# Message attributes are sent only when the message structure is String, not JSON.
                post '/messageconfig', String.generate(sendmessage)
                expect(last_response.status).to eq(200)
                end

              end
            end

          end
        end
      end
      end

