# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam-ruby-example-construct-url-federated-users'

describe '#get_federated_user_console_sign_in_url' do
  let(:user_name) { 'my-user' }
  let(:duration_seconds) { 1_800 }
  let(:policy) { "{\"Version\":\"2012-10-17\",\"Statement\":{\"Effect\":\"Allow\",\"Action\":\"sns:*\",\"Resource\":\"*\"}}" }
  let(:issuer_url) { 'https://mysignin.internal.mycompany.com/' }
  let(:console_url) { 'https://console.aws.amazon.com/sns' }
  let(:signin_url) { 'https://signin.aws.amazon.com/federation' }
  let(:sts_client) do
    Aws::STS::Client.new(
      stub_responses: {
        get_federation_token: {
          credentials: {
            access_key_id: 'AKIAIOSFODNN7EXAMPLE',
            expiration: Time.now + duration_seconds,
            secret_access_key: 'wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY',
            session_token: 'AQoDYXdzEPT//////////wEXAMPLEtc764bNrC9SAPBSM22wDOk4x4HIZ8j4FZTwdQWLWsKWHGBuFqwAeMicRXmxfpSPfIeoIYRqTflfKD8YUuwthAx7mSEI/qkPpKPi/kMcGdQrmGdeehM4IC1NtBmUpp2wUE8phUZampKsburEDy0KPkyQDYwT7WZ0wq5VSXDvp75YU9HFvlRd8Tx6q6fE8YQcHNVXAkiY9q6d+xo0rKwT38xVqr7ZD0u0iPPkUL64lIZbqBAz+scqKmlzm8FDrypNC9Yjc8fPOLn9FX9KSYvKTr4rvx3iSIlTJabIQwj2ICCR/oLxBA==',
          }
        }
      }
    )
  end

  it 'gets the console sign-in URL for a federated user' do
    expect {
      get_federated_user_console_sign_in_url(
        sts_client,
        user_name,
        duration_seconds,
        policy,
        issuer_url,
        console_url,
        signin_url
      )
    }.not_to raise_error
  end
end
