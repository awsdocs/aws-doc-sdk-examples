# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# This code example determines the users available to you in
# AWS Identity and Access Management (IAM), how many of them are associated
# with a policy that provides administrator privileges.
#
# This code example begins running by calling the custom run_me function.
# This function calls the custom function is_user_admin?, which in turn
# calls the following custom functions:
# - user_has_admin_policy?
# - user_has attached_policy?
# - user_has_admin_from_group?
# The custom user_has_admin_from_group? function calls the following
# custom functions:
# - group_has_admin_policy?
# - group_has_attached_policy?

# snippet-start:[elastictranscoder.ruby.showAdmins]


require "aws-sdk-iam"

# Determines whether the specified user in
# AWS Identity and Access Management (IAM) is associated with a policy that
# provides administrator privileges.
#
# Prerequisites:
# - The existing user in IAM.
#
# @param user [Aws::IAM::User] The specified user.
# @param admin_access [String] The name of the administrator-related policy
#   to search for, for example 'AdministratorAccess'.
# @return [Boolean] true if the specified user is associated with a policy that
#   provides administrator privileges; otherwise, false.
# @example
#   client = Aws::IAM::Client.new
#   users = client.get_account_authorization_details(filter: ['User']).user_detail_list
#   exit 1 unless user_has_admin_policy?(users[0], 'AdministratorAccess')
def user_has_admin_policy?(user, admin_access)
  policies = user.user_policy_list

  policies.each do |p|
    return true if p.policy_name == admin_access
  end

  return false
end

# Determines whether the specified user in
# AWS Identity and Access Management (IAM) has a policy attached that
# provides administrator privileges.
#
# Prerequisites:
# - The existing user in IAM.
#
# @param user [Aws::IAM::User] The specified user.
# @param admin_access [String] The name of the administrator-related policy
#   to search for, for example 'AdministratorAccess'.
# @return [Boolean] true if the specified user has a policy attached that
#   provides administrator privileges; otherwise, false.
# @example
#   client = Aws::IAM::Client.new
#   users = client.get_account_authorization_details(filter: ['User']).user_detail_list
#   exit 1 unless user_has_attached_policy?(users[0], 'AdministratorAccess')
def user_has_attached_policy?(user, admin_access)
  attached_policies = user.attached_managed_policies

  attached_policies.each do |p|
    return true if p.policy_name == admin_access
  end

  return false
end

# Determines whether the specified group in
# AWS Identity and Access Management (IAM) is associated with a policy that
# provides administrator privileges.
#
# Prerequisites:
# - The existing group in IAM.
#
# @param client [Aws::IAM::Client] An initialized IAM client.
# @param group [Aws::IAM::Group] The specified group.
# @param admin_access [String] The name of the administrator-related policy
#   to search for, for example 'AdministratorAccess'.
# @return [Boolean] true if the specified group is associated with a policy that
#   provides administrator privileges; otherwise, false.
# @example
#   client = Aws::IAM::Client.new
#   groups = client.list_groups_for_user(user_name: 'Mary')
#   exit 1 unless group_has_admin_policy?(client, groups[0], 'AdministratorAccess')
def group_has_admin_policy?(client, group, admin_access)
  resp = client.list_group_policies(
    group_name: group.group_name
  )

  resp.policy_names.each do |name|
    return true if name == admin_access
  end

  return false
end

# Determines whether the specified group in
# AWS Identity and Access Management (IAM) has a policy attached that
# provides administrator privileges.
#
# Prerequisites:
# - The existing group in IAM.
#
# @param client [Aws::IAM::Client] An initialized IAM client.
# @param group [Aws::IAM::Group] The specified group.
# @param admin_access [String] The name of the administrator-related policy
#   to search for, for example 'AdministratorAccess'.
# @return [Boolean] true if the specified group has a policy attached that
#   provides administrator privileges; otherwise, false.
# @example
#   client = Aws::IAM::Client.new
#   groups = client.list_groups_for_user(user_name: 'Mary')
#   exit 1 unless group_has_attached_policy?(client, groups[0], 'AdministratorAccess')
def group_has_attached_policy?(client, group, admin_access)
  resp = client.list_attached_group_policies(
    group_name: group.group_name # required
  )

  resp.attached_policies.each do |policy|
    return true if policy.policy_name == admin_access
  end

  return false
end

# Determines whether the specified user in
# AWS Identity and Access Management (IAM) is associated with a group
# that has administrator privileges.
#
# Prerequisites:
# - The existing user in IAM.
#
# @param client [Aws::IAM::Client] An initialized IAM client.
# @param user [Aws::IAM::User] The specified user.
# @param admin_access [String] The name of the administrator-related policy
#   to search for, for example 'AdministratorAccess'.
# @return [Boolean] true if the specified user is associated with a group that
#   has administrator privileges; otherwise, false.
# @example
#   client = Aws::IAM::Client.new
#   users = client.get_account_authorization_details(filter: ['User']).user_detail_list
#   exit 1 unless user_has_admin_from_group?(client, users[0], 'AdministratorAccess')
def user_has_admin_from_group?(client, user, admin_access)
  resp = client.list_groups_for_user(
    user_name: user.user_name
  )

  resp.groups.each do |group|
    return true if group_has_admin_policy?(client, group, admin_access)
    return true if group_has_attached_policy?(client, group, admin_access)
  end

  return false
end

# Determines whether the specified user in
# AWS Identity and Access Management (IAM) has administrator privileges.
#
# Prerequisites:
# - The existing user in IAM.
#
# @param client [Aws::IAM::Client] An initialized IAM client.
# @param user [Aws::IAM::User] The specified user.
# @param admin_access [String] The name of the administrator-related policy
#   to search for, for example 'AdministratorAccess'.
# @return [Boolean] true if the specified user has administrator privileges;
#   otherwise, false.
# @example
#   client = Aws::IAM::Client.new
#   users = client.get_account_authorization_details(filter: ['User']).user_detail_list
#   exit 1 unless is_user_admin?(client, users[0], 'AdministratorAccess')
def is_user_admin?(client, user, admin_access)
  return true if user_has_admin_policy?(user, admin_access)
  return true if user_has_attached_policy?(user, admin_access)
  return true if user_has_admin_from_group?(client, user, admin_access)

  return false
end

# Determines how many of the available users in
# AWS Identity and Access Management (IAM) have administrator privileges.
#
# Prerequisites:
# - One or more existing users in IAM.
#
# @param client [Aws::IAM::Client] An initialized IAM client.
# @param users [Array] A list of users of type Aws::IAM::Types::UserDetail.
# @param admin_access [String] The name of the administrator-related policy
#   to search for, for example 'AdministratorAccess'.
# @return [Integer] The number of available users who have
#   administrator privileges.
# @example
#   client = Aws::IAM::Client.new
#   puts get_admin_count(
#     client,
#     client.get_account_authorization_details(filter: ['User']).user_detail_list,
#     'AdministratorAccess'
#   )
def get_admin_count(client, users, admin_access)
  num_admins = 0

  users.each do |user|
    is_admin = is_user_admin?(client, user, admin_access)
    if is_admin
      puts user.user_name
      num_admins += 1
    end
  end

  num_admins
end

# Full example call:
def run_me
  client = Aws::IAM::Client.new

  num_users = 0
  num_admins = 0
  access_admin = "AdministratorAccess"

  puts "Getting the list of available users..."
  details = client.get_account_authorization_details(filter: ["User"])
  users = details.user_detail_list

  unless users.count.positive?
    puts "No available users found. Stopping program."
    exit 1
  end

  num_users += users.count

  puts "Getting the list of available users who are associated with the " \
    "policy '#{access_admin}'..."
  more_admins = get_admin_count(client, users, access_admin)
  num_admins += more_admins

  unless num_admins.positive?
    puts "No available users found yet who are associated with the " \
      "policy '#{access_admin}'. Looking for more available " \
      "users..."
  end

  more_users = details.is_truncated

  while more_users
    details = client.get_account_authorization_details(
      filter: ["User"],
      marker: details.marker
    )

    users = details.user_detail_list
    num_users += users.count
    more_admins = get_admin_count(client, users, access_admin)
    num_admins += more_admins
    more_users = details.is_truncated
  end

  puts "Out of #{num_users} available user(s), found #{num_admins} " \
    "available user(s) who is/are associated with the policy " \
    "'#{access_admin}'."
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[elastictranscoder.ruby.showAdmins]
