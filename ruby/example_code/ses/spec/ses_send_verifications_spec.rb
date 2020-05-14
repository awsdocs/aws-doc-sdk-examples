# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

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
          sendverification.sendverifications()
        end


