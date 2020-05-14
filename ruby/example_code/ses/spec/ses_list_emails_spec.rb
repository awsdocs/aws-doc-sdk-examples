# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
#
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
    :describe_emails, :email_list => [
    { :email => "example@amazon.com", :status => "Verified" },
        { :email => "sample@amazon.com", :status => "Pending" }
     ]
    )
    listemail.listemails()
  end



