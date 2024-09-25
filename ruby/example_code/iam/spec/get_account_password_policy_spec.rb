# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'rspec'
require_relative '../get_account_password_policy'
require 'aws-sdk-iam'

describe PasswordPolicyManager do
  before(:each) do
    @iam_client = Aws::IAM::Client.new
    @iam_policy_manager = PasswordPolicyManager.new(@iam_client)
  end

  describe '#print_account_password_policy' do
    context 'when the account has a password policy' do
      it 'logs the password policy details' do
        expect { @iam_policy_manager.print_account_password_policy }.not_to raise_error
      end
    end

    context 'when the account does not have a password policy' do
      it 'logs a specific message' do
        expect { @iam_policy_manager.print_account_password_policy }.not_to raise_error
      end
    end
  end
end
