# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require_relative('../attach_user_policy')
require 'rspec'

describe UserPolicyManager do
  let(:iam_client) { Aws::IAM::Client.new }
  let(:user_manager) { UserPolicyManager.new(iam_client) }
  let(:user_name) { "test-user-#{Time.now.to_i}" }
  let(:policy_arn) { 'arn:aws:iam::aws:policy/AmazonS3FullAccess' }

  before(:all) do
    @iam_client = Aws::IAM::Client.new
    @user_name = "test-user-#{Time.now.to_i}"
    @iam_client.create_user(user_name: @user_name)
    @policy_arn = 'arn:aws:iam::aws:policy/AmazonS3FullAccess'
    @manager = UserPolicyManager.new(@iam_client)
  end

  after(:all) do
    @iam_client.delete_user(user_name: @user_name)
  end

  it 'attaches a policy to a user' do
    expect(@manager.attach_policy_to_user(@user_name, @policy_arn)).to be true
  end

  it 'detaches a policy from a user' do
    expect(@manager.detach_user_policy(@user_name, @policy_arn)).to be true
  end
end
