# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require 'logger'

# snippet-start:[iam.ruby.ListAdmins]
# Manages IAM user's administrative privileges
class AdminPrivilegeManager
  ADMIN_ACCESS_POLICY_NAME = 'AdministratorAccess'.freeze

  def initialize(iam_client, logger: Logger.new($stdout))
    @iam = iam_client
    @logger = logger
    @logger.progname = 'AdminPrivilegeManager'
  end

  # Checks if the specified IAM entity (user or group) has admin privileges
  def admin_privileges?(entity)
    entity_policies = @iam.list_attached_user_policies(user_name: entity.user_name) if entity.respond_to?(:user_name)
    entity_policies ||= @iam.list_attached_group_policies(group_name: entity.group_name) if entity.respond_to?(:group_name)

    entity_policies.attached_policies.any? { |p| p.policy_name == ADMIN_ACCESS_POLICY_NAME }
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error checking admin privileges: #{e.message}")
    false
  end

  # Determines whether the specified user is associated with admin privileges
  def user_is_admin?(user)
    return true if has_admin_privileges?(user)

    @iam.list_groups_for_user(user_name: user.user_name).groups.any? do |group|
      has_admin_privileges?(group)
    end
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error checking if user is admin: #{e.message}")
    false
  end

  # Counts and lists users with admin privileges
  def count_and_list_admins
    num_users = 0
    num_admins = 0
    @iam.list_users.users.each do |user|
      num_users += 1
      if user_is_admin?(user)
        @logger.info("#{user.user_name} has admin privileges")
        num_admins += 1
      end
    end

    [num_users, num_admins]
  end
end
# snippet-end:[iam.ruby.ListAdmins]

# Example usage:
if __FILE__ == $PROGRAM_NAME
  client = Aws::IAM::Client.new
  admin_manager = AdminPrivilegeManager.new(client)

  num_users, num_admins = admin_manager.count_and_list_admins
  puts "Total users: #{num_users}, Users with admin privileges: #{num_admins}"
end
