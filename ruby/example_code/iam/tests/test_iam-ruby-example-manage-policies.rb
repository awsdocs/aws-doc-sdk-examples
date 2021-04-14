# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam-ruby-example-manage-policies'

describe '#create_policy' do
  let(:policy_name) { 'my-policy' }
  let(:policy_document) do
    {
      'Version': '2012-10-17',
      'Statement': [
        {
          'Effect': 'Allow',
          'Action': 's3:ListAllMyBuckets',
          'Resource': 'arn:aws:s3:::*'
        }
      ]
    }
  end
  let(:policy_arn) { 'arn:aws:iam::111111111111:policy/my-policy' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        create_policy: {
          policy: {
            arn: policy_arn
          }
        }
      }
    )
  end

  it 'creates a policy' do
    expect(create_policy(iam_client, policy_name, policy_document)).to eq(policy_arn)
  end
end

describe '#policy_attached_to_role?' do
  let(:role_name) { 'my-role' }
  let(:policy_arn) { 'arn:aws:iam::111111111111:policy/my-policy' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        attach_role_policy: {}
      }
    )
  end

  it 'attaches a policy to a role' do
    expect(policy_attached_to_role?(iam_client, role_name, policy_arn)).to eq(true)
  end
end

describe '#list_policy_arns_attached_to_role' do
  let(:policy_name) { 'my-policy' }
  let(:role_name) { 'my-role' }
  let(:policy_arn) { 'arn:aws:iam::111111111111:policy/my-policy' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        list_attached_role_policies: {
          attached_policies: [
            policy_arn: policy_arn
          ]
        }
      }
    )
  end

  it 'lists the policy ARNs that are attached to a role' do
    expect { list_policy_arns_attached_to_role(iam_client, role_name) }.not_to raise_error
  end
end

describe '#policy_detached_from_role?' do
  let(:role_name) { 'my-role' }
  let(:policy_arn) { 'arn:aws:iam::111111111111:policy/my-policy' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        detach_role_policy: {}
      }
    )
  end

  it 'detaches a policy from a role' do
    expect(policy_detached_from_role?(iam_client, role_name, policy_arn)).to be(true)
  end
end
