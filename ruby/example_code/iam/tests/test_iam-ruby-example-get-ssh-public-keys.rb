# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam-ruby-example-get-ssh-public-keys'

describe '#get_ssh_public_key_details' do
  let(:user_name) { 'my-user' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        list_ssh_public_keys: {
          ssh_public_keys: [
            {
              user_name: user_name,
              ssh_public_key_id: 'APKAEIBAERJR2EXAMPLE',
              status: 'Active',
              upload_date: Time.now
            }
          ]
        }
      }
    )
  end

  it 'gets details about a user\'s SSH public keys' do
    expect { get_ssh_public_key_details(iam_client, user_name) }.not_to raise_error
  end
end