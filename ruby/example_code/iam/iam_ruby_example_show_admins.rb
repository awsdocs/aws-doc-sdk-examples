# Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

require 'aws-sdk-iam'  # v2: require 'aws-sdk'
require 'os'

if OS.windows?
  Aws.use_bundled_cert!
end

def user_has_admin_policy(user, admin_access)
  policies = user.user_policy_list

  policies.each do |p|
    if p.policy_name == admin_access
      return true
    end
  end
  
  false
end

def user_has_attached_policy(user, admin_access)
  attached_policies = user.attached_managed_policies

  attached_policies.each do |p|
    if p.policy_name == admin_access
      return true
    end
  end

  false
end

def group_has_admin_policy(client, group, admin_access)
  resp = client.list_group_policies(
    group_name: group.group_name
  )

  resp.policy_names.each do |name|
    if name == admin_access
      return true
    end
  end

  false
end

def group_has_attached_policy(client, group, admin_access)
  resp = client.list_attached_group_policies(
    group_name: group.group_name # required
  )

  resp.attached_policies.each do |policy|
    if policy.policy_name == admin_access
      return true
    end
  end

  false
end

def user_has_admin_from_group(client, user, admin_access)
  resp = client.list_groups_for_user(
    user_name: user.user_name
  )

  resp.groups.each do |group|
    has_admin_policy = group_has_admin_policy(client, group, admin_access)
    if has_admin_policy
      return true
    end

    has_attached_policy = group_has_attached_policy(client, group, admin_access)
    if has_attached_policy
      return true
    end
  end

  false
end

def is_user_admin(client, user, admin_access)
  has_admin_policy = user_has_admin_policy(user, admin_access)
  if has_admin_policy
    return true
  end

  has_attached_admin_policy = user_has_attached_policy(user, admin_access)
  if has_attached_admin_policy
    return true
  end

  has_admin_from_group = user_has_admin_from_group(client, user, admin_access)
  if has_admin_from_group
    return true
  end

  false
end

def get_admin_count(client, users, admin_access)
  num_admins = 0

  users.each do |user|
    is_admin = is_user_admin(client, user, admin_access)
    if is_admin
      puts user.user_name
      num_admins += 1
    end
  end

  num_admins
end

# main starts here
client = Aws::IAM::Client.new

num_users = 0
num_admins = 0
access_admin = 'AdministratorAccess'

details = client.get_account_authorization_details(
  filter: ['User']
)

users = details.user_detail_list
num_users += users.count
more_admins = get_admin_count(client, users, access_admin)
num_admins += more_admins

more_users = details.is_truncated

while more_users
  details = client.get_account_authorization_details(
    filter: ['User'],
    marker: details.marker
  )

  users = details.user_detail_list

  num_users += users.count
  more_admins = get_admin_count(client, users, access_admin)
  num_admins += more_admins

  more_users = details.is_truncated
end

puts
puts "Found #{num_admins} admin(s) out of #{num_users} user(s)"
