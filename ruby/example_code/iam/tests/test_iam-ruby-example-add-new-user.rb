# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../iam-ruby-example-add-new-user'

describe '#create_user' do
  let(:user_name) { 'my-user' }
  let(:initial_password) { 'my-!p@55w0rd!' }
  let(:user_id) { 'AKIAIOSFODNN7EXAMPLE' }
  let(:iam_client) do
    Aws::IAM::Client.new(
      stub_responses: {
        create_user: {
          user: {
            arn: "arn:aws:iam::111111111111:user/#{user_name}",
            create_date: Time.now,
            path: '/',
            user_id: user_id,
            user_name: user_name
          }
        },
        get_user: {
          user: {
            arn: "arn:aws:iam::111111111111:user/#{user_name}",
            create_date: Time.now,
            path: '/',
            user_id: user_id,
            user_name: user_name
          }
        },
        create_login_profile: {
          login_profile: {
            user_name: user_name,
            create_date: Time.now
          }
        }
      }
    )
  end

  it 'creates a user' do
    expect(create_user(iam_client, user_name, initial_password)).to eq(user_id)
  end
end
