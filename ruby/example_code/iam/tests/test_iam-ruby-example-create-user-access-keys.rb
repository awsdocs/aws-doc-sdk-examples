# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam-ruby-example-create-user-access-keys'

describe '#create_access_key' do
  let(:user_name) { 'my-user' }
  let(:access_key_id) { 'AKIAIOSFODNN7EXAMPLE' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        create_access_key: {
          access_key: {
            access_key_id: access_key_id,
            secret_access_key: 'wJalrXUtnFEMI/K7MDENG/bPxRfiCYzEXAMPLEKEY',
            status: 'Active',
            user_name: user_name
          }
        }
      }
    )
  end

  it 'creates an access key' do
    expect { create_access_key(iam_client, user_name) }.not_to raise_error
  end
end
