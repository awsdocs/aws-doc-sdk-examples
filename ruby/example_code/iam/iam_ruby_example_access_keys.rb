# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose:
# iam-ruby-example-access-keys.rb demonstrates how to do the following using the AWS SDK for Ruby:
# 1. List access keys for a user in AWS Identity and Access Management (IAM).
# 2. Create an access key for a user.
# 3. Determine when a user's access keys were last used.
# 4. Deactivate an access key for a user.
# 5. Delete an access key for a user.

# snippet-start:[iam.ruby.exampleAccessKeys]

require "aws-sdk-iam"

# Lists information about access keys for a user in
# AWS Identity and Access Management (IAM).
#
# Prerequisites:
# - The user in IAM.
#
# @param iam [Aws::IAM::Client] An initialized IAM client.
# @param user_name [String] The name of the user.
# @example
#   puts list_access_keys(Aws::IAM::Client.new, 'my-user')
def list_access_keys(iam, user_name)
  response = iam.list_access_keys(user_name: user_name)

  if response.access_key_metadata.count.positive?
    puts "Access key IDs:"
    response.access_key_metadata.each do |key_metadata|
      puts "  #{key_metadata.access_key_id}"
    end
  else
    puts "No access keys found for user '#{user_name}'."
  end
rescue Aws::IAM::Errors::NoSuchEntity
  puts "Error listing access keys: cannot find user '#{user_name}'."
  exit 1
rescue StandardError => e
  puts "Error listing access keys: #{e.message}"
end

# Creates an access key for a user in AWS Identity and Access Management (IAM).
#
# Prerequisites:
# - The user in IAM.
#
# @param iam [Aws::IAM::Client] An initialized IAM client.
# @param user_name [String] The name of the user.
# @return [Aws::IAM::Types::AccessKey] Information about the new access key;
#   otherwise, the string 'Error'.
# @example
#   puts create_access_key(Aws::IAM::Client.new, 'my-user')
def create_access_key(iam, user_name)
  response = iam.create_access_key(user_name: user_name)
  access_key = response.access_key
  puts "Access key created:"
  puts "  Access key ID: #{access_key.access_key_id}"
  puts "  Secret access key: #{access_key.secret_access_key}"
  puts "Keep a record of this information in a secure location. " \
    "This will be the only time you will be able to view the " \
    "secret access key."
  return access_key
rescue Aws::IAM::Errors::LimitExceeded
  puts "Error creating access key: limit exceeded. Cannot create any more. " \
    "To create more, delete an existing access key, and then try again."
  return "Error"
rescue StandardError => e
  puts "Error creating access key: #{e.message}"
  return "Error"
end

# Lists information about when access keys for a user in
# AWS Identity and Access Management (IAM) were last used.
#
# Prerequisites:
# - The user in IAM.
#
# @param iam [Aws::IAM::Client] An initialized IAM client.
# @param user_name [String] The name of the user.
# @example
#   puts access_keys_last_used(Aws::IAM::Client.new, 'my-user')
def access_keys_last_used(iam, user_name)
  response = iam.list_access_keys(user_name: user_name)

  response.access_key_metadata.each do |key_metadata|
    last_used = iam.get_access_key_last_used(access_key_id: key_metadata.access_key_id)
    if last_used.access_key_last_used.last_used_date.nil?
      puts "  Key '#{key_metadata.access_key_id}' not used or date undetermined."
    else
      puts "  Key '#{key_metadata.access_key_id}' last used on " \
      "#{last_used.access_key_last_used.last_used_date}"
    end
  end
rescue StandardError => e
  puts "Error determining when access keys were last used: #{e.message}"
end

# Deactivates an access key in AWS Identity and Access Management (IAM).
#
# Prerequisites:
# - A user in IAM.
# - An access key for that user.
#
# @param iam [Aws::IAM::Client] An initialized IAM client.
# @param user_name [String] The name of the user.
# @param access_key_id [String] The ID of the access key.
# @return [Boolean] true if the access key was deactivated;
#   otherwise, false.
# @example
#   exit 1 unless access_key_deactivated?(
#     Aws::IAM::Client.new,
#     'my-user',
#     'AKIAIOSFODNN7EXAMPLE'
#   )
def access_key_deactivated?(iam, user_name, access_key_id)
  iam.update_access_key(
    user_name: user_name,
    access_key_id: access_key_id,
    status: "Inactive"
  )
  return true
rescue StandardError => e
  puts "Error deactivating access key: #{e.message}"
  return false
end

# Deletes an access key in AWS Identity and Access Management (IAM).
#
# Prerequisites:
# - A user in IAM.
# - An access key for that user.
#
# @param iam [Aws::IAM::Client] An initialized IAM client.
# @param user_name [String] The name of the user.
# @param access_key_id [String] The ID of the access key.
# @return [Boolean] true if the access key was deleted;
#   otherwise, false.
# @example
#   exit 1 unless access_key_deleted?(
#     Aws::IAM::Client.new,
#     'my-user',
#     'AKIAIOSFODNN7EXAMPLE'
#   )
def access_key_deleted?(iam, user_name, access_key_id)
  iam.delete_access_key(
    user_name: user_name,
    access_key_id: access_key_id
  )
  return true
rescue StandardError => e
  puts "Error deleting access key: #{e.message}"
  return false
end

# Full example call:
def run_me
  iam = Aws::IAM::Client.new
  user_name = "my-user"
  create_key = true  # Set to false to not create a new access key.
  delete_key = true  # Set to false to not delete any generated access key.

  puts "Access keys for user '#{user_name}' before attempting to create an " \
    "additional access key for the user:"
  list_access_keys(iam, user_name)

  access_key = ""

  if create_key
    puts "Attempting to create an additional access key..."
    access_key = create_access_key(iam, user_name)

    if access_key == "Error"
      puts "Additional access key not created. Stopping program."
      exit 1
    end

    puts "Additional access key created. Access keys for user now are:"
    list_access_keys(iam, user_name)
  end

  puts "Determining when current access keys were last used..."
  access_keys_last_used(iam, user_name)

  if create_key && delete_key
    puts "Attempting to deactivate additional access key..."

    if access_key_deactivated?(iam, user_name, access_key.access_key_id)
      puts "Access key deactivated. Access keys for user now are:"
      list_access_keys(iam, user_name)
    else
      puts "Access key not deactivated. Stopping program."
      puts "You will need to delete the access key yourself."
    end

    puts "Attempting to delete additional access key..."

    if access_key_deleted?(iam, user_name, access_key.access_key_id)
      puts "Access key deleted. Access keys for user now are:"
      list_access_keys(iam, user_name)
    else
      puts "Access key not deleted. You will need to delete the " \
        "access key yourself."
    end
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[iam.ruby.exampleAccessKeys]
