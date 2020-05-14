# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative 'spec_helper'
sns = Aws::SES::Resource.new(region: 'us-west-2')

RSpec.describe SendEmail do
 let (:sendemail_client) { Aws::SendEmail::Client.new(stub_responses: true) }
 let (:sendmeail) do
   SendEmail.new(
       sendemail_client: sendemail_client
   )
 end

 describe '#sendemail' do
   it 'send an email through Amazon SES' do
     :sendemails, :emails =>
         { :email_messageid => "EXAMPLE1234567f-7a5433e7-8edb-42ae-af10-f0181f34d6ee-000000" },
         { :email_messageid => "EXAMPLE1234567f-7a5433e7-8edb-42ae-af10-f0181f34d6ee-000000" }
      ]
     )
     sendemail.sendemails()
   end























