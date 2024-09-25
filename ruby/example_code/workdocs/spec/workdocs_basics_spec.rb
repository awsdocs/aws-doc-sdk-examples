# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'rspec'
require_relative '../workdocs_basics'

describe WorkDocsManager do
  let(:client) { Aws::WorkDocs::Client.new(stub_responses: true) }
  let(:manager) { WorkDocsManager.new(client) }
  let(:org_id) { 'd-123456789c' }
  let(:user_email) { 'someone@somewhere' }

  describe '#describe_users' do
    it 'calls describe_users on the WorkDocs client' do
      client.stub_responses(:describe_users, users: [])
      expect { manager.describe_users(org_id) }.not_to raise_error
    end
  end

  describe '#get_user_folder' do
    it 'returns the root folder id for a given user email' do
      users = [{ email_address: user_email, root_folder_id: 'root-folder-id' }]
      expect(manager.get_user_folder(users, user_email)).to eq('root-folder-id')
    end
  end

  describe '#describe_folder_contents' do
    it 'calls describe_folder_contents on the WorkDocs client' do
      folder_id = 'root-folder-id'
      client.stub_responses(:describe_folder_contents, documents: [])
      expect { manager.describe_folder_contents(folder_id) }.not_to raise_error
    end
  end
end
