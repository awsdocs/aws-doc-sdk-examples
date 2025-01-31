# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require_relative('../list_admins')
require 'rspec'

describe AdminPrivilegeManager do
  let(:iam_client) { Aws::IAM::Client.new }
  let(:admin_privilege_manager) { AdminPrivilegeManager.new(iam_client) }

  describe '#has_admin_privileges?' do
    context 'when the entity is a user with admin privileges' do
      it 'returns true' do
        user = iam_client.create_user(user_name: 'test_admin_user').user
        iam_client.attach_user_policy({
                                        policy_arn: 'arn:aws:iam::aws:policy/AdministratorAccess',
                                        # Use the correct ARN for the admin policy
                                        user_name: user.user_name
                                      })

        expect(admin_privilege_manager.has_admin_privileges?(user)).to eq(true)

        # Detach the policy and delete the user after the test
        iam_client.detach_user_policy({
                                        policy_arn: 'arn:aws:iam::aws:policy/AdministratorAccess',
                                        user_name: user.user_name
                                      })
        iam_client.delete_user({ user_name: user.user_name })
      end
    end

    context 'when the entity is a user without admin privileges' do
      it 'returns false' do
        user = iam_client.create_user(user_name: 'test_non_admin_user').user
        expect(admin_privilege_manager.has_admin_privileges?(user)).to eq(false)
        iam_client.delete_user({ user_name: user.user_name }) # Cleanup after test
      end
    end
  end
end
