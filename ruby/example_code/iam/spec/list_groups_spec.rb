# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'rspec'
require_relative '../list_groups'

describe IamGroupManager do
  let(:iam_client) { Aws::IAM::Client.new }
  let(:iam_group_manager) { IamGroupManager.new(iam_client) }

  describe '#list_groups' do
    it 'lists up to a specified number of groups without raising an error' do
      expect { iam_group_manager.list_groups(10) }.not_to raise_error
    end
  end
end
