# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-iam'

# Displays information about available users in
# AWS Identity and Access Management (IAM) including users'
# names, associated group names, inline embedded user policy names,
# and access key IDs.
#
# @param iam_client [Aws::IAM::Client] An initialized IAM client.
# @example
#   get_user_details(Aws::IAM::Client.new)
def get_user_details(iam_client)
  users_response = iam_client.list_users

  if users_response.key?('users') && users_response.users.count.positive?

    # Are there more users available than can be displayed?
    if users_response.key?('is_truncated') && users_response.is_truncated
      puts '(Note: not all users are displayed here, ' \
        "only the first #{users_response.users.count}.)"
    else
      puts "Found #{users_response.users.count} user(s):"
    end

    users_response.users.each do |user|
      name = user.user_name
      puts '-' * 30
      puts "User name: #{name}"

      puts "Groups:"
      groups_response = iam_client.list_groups_for_user(user_name: name)
      if groups_response.key?('groups') &&
        groups_response.groups.count.positive?

        groups_response.groups.each do |group|
          puts "  #{group.group_name}"
        end
      else
        puts '  None'
      end

      puts 'Inline embedded user policies:'
      policies_response = iam_client.list_user_policies(user_name: name)
      if policies_response.key?('policy_names') &&
        policies_response.policy_names.count.positive?

        policies_response.policy_names.each do |policy_name|
          puts "  #{policy_name}"
        end
      else
        puts '  None'
      end

      puts 'Access keys:'
      access_keys_response = iam_client.list_access_keys(user_name: name)

      if access_keys_response.key?('access_key_metadata') &&
        access_keys_response.access_key_metadata.count.positive?

        access_keys_response.access_key_metadata.each do |access_key|
          puts "  #{access_key.access_key_id}"
        end
      else
        puts '  None'
      end
    end
  else
    puts 'No users found.'
  end
rescue StandardError => e
  puts "Error getting user details: #{e.message}"
end

# Full example call:
def run_me
  iam_client = Aws::IAM::Client.new
  puts 'Attempting to get details for available users...'
  get_user_details(iam_client)
end

run_me if $PROGRAM_NAME == __FILE__
