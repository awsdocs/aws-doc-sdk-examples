# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam-ruby-example-manage-users'

describe '#list_user_names' do
  let(:user_name) { 'my-user' }
  let(:user_arn) { "arn:aws:iam::111111111111:user/#{user_name}" }
  let(:user_id) { 'AIDACKCEVSQ6C2EXAMPLE' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        list_users: {
          users: [
            user_name: user_name,
            arn: user_arn,
            create_date: Time.now,
            path: '/',
            user_id: user_id
          ]
        }
      }
    )
  end

  it 'lists available user names' do
    expect { list_user_names(iam_client) }.not_to raise_error
  end
end

describe '#user_created?' do
  let(:user_name) { 'my-user' }
  let(:user_arn) { "arn:aws:iam::111111111111:user/#{user_name}" }
  let(:user_id) { 'AIDACKCEVSQ6C2EXAMPLE' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        create_user: {
          user: {
            user_name: user_name,
            arn: user_arn,
            create_date: Time.now,
            path: '/',
            user_id: user_id
          }
        }
      }
    )
  end

  it 'creates a user' do
    expect(user_created?(iam_client, user_name)).to be(true)
  end
end

describe '#user_name_changed?' do
  let(:user_current_name) { 'my-user' }
  let(:user_new_name) { 'my-changed-user' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        update_user: {}
      }
    )
  end

  it 'changes the name of a user' do
    expect(user_name_changed?(iam_client, user_current_name, user_new_name)).to be(true)
  end
end

describe '#user_deleted?' do
  let(:user_name) { 'my-user' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        delete_user: {}
      }
    )
  end

  it 'deletes a user' do
    expect(user_deleted?(iam_client, user_name)).to be(true)
  end
end
