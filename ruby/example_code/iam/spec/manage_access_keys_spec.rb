# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require_relative('../manage_access_keys')
require 'rspec'

describe AccessKeyManager do
  before(:all) do
    @iam_client = Aws::IAM::Client.new
    @manager = AccessKeyManager.new(@iam_client)
    @user_name = "test-user-#{Time.now.to_i}"
    @iam_create_user = @iam_client.create_user(user_name: @user_name)
  end

  after(:all) do
    @iam_client.delete_user(user_name: @user_name)
  end

  it 'creates and deletes an access key for a user' do
    access_key = @manager.create_access_key(@user_name)
    expect(access_key).not_to be_nil

    keys_after_creation = @manager.list_access_keys(@user_name)
    expect(keys_after_creation).to include(access_key.access_key_id)

    delete_success = @manager.delete_access_key(@user_name, access_key.access_key_id)
    expect(delete_success).to be true

    keys_after_deletion = @manager.list_access_keys(@user_name)
    expect(keys_after_deletion).to be true
  end
end
