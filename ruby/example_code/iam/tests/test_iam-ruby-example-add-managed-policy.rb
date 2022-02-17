# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam-ruby-example-add-managed-policy'

describe '#policy_attached_to_user?' do
  let(:user_name) { 'my-user' }
  let(:policy_arn) { 'arn:aws:iam::aws:policy/AmazonS3FullAccess' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        attach_user_policy: {}
      }
    )
  end

  it 'attaches a policy to a user' do
    expect(policy_attached_to_user?(iam_client, user_name, policy_arn)).to be(true)
  end
end
