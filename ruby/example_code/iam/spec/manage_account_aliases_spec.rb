# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require_relative '../manage_account_aliases'
require 'rspec'

describe IAMAliasManager do
  before(:each) do
    @iam_client = Aws::IAM::Client.new
    @manager = IAMAliasManager.new(@iam_client)
    @account_alias = "test-account-alias-#{rand(1000)}"

    existing_aliases = @iam_client.list_account_aliases.account_aliases
    @iam_client.delete_account_alias(account_alias: @account_alias) if existing_aliases.include?(@account_alias)
  end

  after(:each) do
    existing_aliases = @iam_client.list_account_aliases.account_aliases
    @iam_client.delete_account_alias(account_alias: @account_alias) if existing_aliases.include?(@account_alias)
  end

  describe '#create_account_alias', :integ do
    it 'creates an account alias successfully' do
      expect(@manager.create_account_alias(@account_alias)).to be true
      expect(@iam_client.list_account_aliases.account_aliases).to include(@account_alias)
    end
  end

  describe '#delete_account_alias', :integ do
    it 'deletes an account alias successfully' do
      @manager.create_account_alias(@account_alias)
      expect(@manager.delete_account_alias(@account_alias)).to be true
      expect(@iam_client.list_account_aliases.account_aliases).not_to include(@account_alias)
    end
  end
end
