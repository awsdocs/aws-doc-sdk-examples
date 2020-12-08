# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# The following code example shows how to to:
# 1. Get a list of user names in AWS Identity and Access Management (IAM).
# 2. Create a user.
# 3. Update the user's name.
# 4. Delete the user.

require 'aws-sdk-iam'

# TODO: Add documentation.
# Get information about available AWS IAM users.
# @param iam_client [Aws::IAM::Client] An initialized IAM client.
# @example
def list_user_names(iam_client)
  response = iam_client.list_users
  if response.key?('users') && response.users.count.positive?
    response.users.each do |user|
      puts user.user_name
    end
  else
    puts 'No users found.'
  end
rescue StandardError => e
  puts "Error listing user names: #{e.message}"
end

# TODO: Add documentation.
# @param iam_client [Aws::IAM::Client] An initialized IAM client.
# @example
def user_created?(iam_client, user_name)
  iam_client.create_user(user_name: user_name)
  return true
rescue Aws::IAM::Errors::EntityAlreadyExists
  puts "Error creating user: user '#{user_name}' already exists."
  return false
rescue StandardError => e
  puts "Error creating user: #{e.message}"
  return false
end

# TODO: Add documentation.
# @param iam_client [Aws::IAM::Client] An initialized IAM client.
# @example
def user_name_changed?(iam_client, user_current_name, user_new_name)
  iam_client.update_user(
    user_name: user_current_name,
    new_user_name: user_new_name
  )
  return true
rescue StandardError => e
  puts "Error updating user name: #{e.message}"
  return false
end

# TODO: Add documentation.
# @param iam_client [Aws::IAM::Client] An initialized IAM client.
# @example
def user_deleted?(iam_client, user_name)
  iam_client.delete_user(user_name: user_name)
  return true
rescue StandardError => e
  puts "Error deleting user: #{e.message}"
  return false
end

# Full example call:
def run_me
  user_name = 'my-user'
  user_changed_name = 'my-changed-user'
  delete_user = true
  iam_client = Aws::IAM::Client.new

  puts "Initial user names are:\n\n"
  list_user_names(iam_client)

  puts "\nAttempting to create user '#{user_name}'..."

  if user_created?(iam_client, user_name)
    puts 'User created.'
  else
    puts 'Could not create user. Stopping program.'
    exit 1
  end

  puts "User names now are:\n\n"
  list_user_names(iam_client)

  puts "\nAttempting to change name of user '#{user_name}' " \
    "to '#{user_changed_name}'..."

  if user_name_changed?(iam_client, user_name, user_changed_name)
    puts 'User name changed.'
    puts "User names now are:\n\n"
    list_user_names(iam_client)

    if delete_user
      # Delete user with changed name.
      puts "\nAttempting to delete user '#{user_changed_name}'..."

      if user_deleted?(iam_client, user_changed_name)
        puts 'User deleted.'
      else
        puts 'Could not delete user. You must delete the user yourself.'
      end

      puts "User names now are:\n\n"
      list_user_names(iam_client)
    end
  else
    puts 'Could not change user name.'
    puts "User names now are:\n\n"
    list_user_names(iam_client)

    if delete_user
      # Delete user with initial name.
      puts "\nAttempting to delete user '#{user_name}'..."

      if user_deleted?(iam_client, user_name)
        puts 'User deleted.'
      else
        puts 'Could not delete user. You must delete the user yourself.'
      end

      puts "User names now are:\n\n"
      list_user_names(iam_client)
    end
  end
end

run_me if $PROGRAM_NAME == __FILE__
