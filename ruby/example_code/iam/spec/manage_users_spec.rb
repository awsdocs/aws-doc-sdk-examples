# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'rspec'
require_relative('../manage_users')

describe UserManager do
  before(:each) do
    @iam_client = Aws::IAM::Client.new
    @logger = Logger.new($stdout)
    @user_manager = UserManager.new(@iam_client, @logger)
    @user_name = "test-user-#{SecureRandom.hex(4)}"
    @initial_password = 'TestPa$$w0rd!'
  end

  after(:each) do
    @user_manager.delete_user(@user_name)
  rescue StandardError => e
    @logger.error("Cleanup failed: #{e.message}")
  end

  describe '#create_user' do
    it 'creates a new IAM user' do
      user_id = @user_manager.create_user(@user_name, @initial_password)
      expect(user_id).not_to be_nil
    end
  end

  describe '#get_user' do
    it 'retrieves details of an existing user' do
      @user_manager.create_user(@user_name, @initial_password)
      user = @user_manager.get_user(@user_name)
      expect(user).not_to be_nil
      expect(user.user_name).to eq(@user_name)
    end
  end

  describe '#list_users' do
    it 'lists all IAM users' do
      @user_manager.create_user(@user_name, @initial_password)
      users = @user_manager.list_users
      expect(users.any? { |u| u.user_name == @user_name }).to be true
    end
  end
end
