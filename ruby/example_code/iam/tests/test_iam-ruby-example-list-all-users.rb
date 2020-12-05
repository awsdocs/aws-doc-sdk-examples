# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam-ruby-example-list-all-users'

describe '#get_user_details' do
  let(:user_name) { 'my-user' }
  let(:group_name) { 'my-group' }
  let(:policy_name) { 'my-policy' }
  let(:user_id) { 'AIDACKCEVSQ6C2EXAMPLE' }
  let(:group_id) { 'AIDACKCEVSQ6C2EXAMPLE' }
  let(:access_key_id) { 'AKIAIOSFODNN7EXAMPLE' }

  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        list_users: {
          users: [
            {
              user_name: user_name,
              arn: "arn:aws:iam::111111111111:user/#{user_name}",
              create_date: Time.now,
              path: '/',
              user_id: user_id
            }
          ]
        },
        list_groups_for_user: {
          groups: [
            {
              group_name: group_name,
              arn: "arn:aws:iam::111111111111:group/#{group_name}",
              create_date: Time.now,
              path: '/',
              group_id: group_id
            }
          ]
        },
        list_user_policies: {
          policy_names: [
            policy_name
          ]
        },
        list_access_keys: {
          access_key_metadata: [
            {
              access_key_id: access_key_id
            }
          ]
        }
      }
    )
  end

  it 'displays information about available users' do
    expect { get_user_details(iam_client) }.not_to raise_error
  end
end