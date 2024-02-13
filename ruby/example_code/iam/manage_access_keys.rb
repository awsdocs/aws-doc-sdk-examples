# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require "aws-sdk-iam"
require "logger"

# snippet-start:[iam.ruby.exampleAccessKeys]
# Manages access keys for IAM users
class AccessKeyManager
  def initialize(iam_client)
    @iam = iam_client
    @logger = Logger.new($stdout)
    @logger.progname = "AccessKeyManager"
  end

  # Lists access keys for a user
  def list_access_keys(user_name)
    response = @iam.list_access_keys(user_name: user_name)
    if response.access_key_metadata.empty?
      @logger.info("No access keys found for user '#{user_name}'.")
    else
      response.access_key_metadata.map(&:access_key_id)
    end
  rescue Aws::IAM::Errors::NoSuchEntity => e
    @logger.error("Error listing access keys: cannot find user '#{user_name}'.")
    []
  rescue StandardError => e
    @logger.error("Error listing access keys: #{e.message}")
    []
  end

  # Creates an access key for a user
  def create_access_key(user_name)
    response = @iam.create_access_key(user_name: user_name)
    access_key = response.access_key
    @logger.info("Access key created for user '#{user_name}': #{access_key.access_key_id}")
    access_key
  rescue Aws::IAM::Errors::LimitExceeded => e
    @logger.error("Error creating access key: limit exceeded. Cannot create more.")
    nil
  rescue StandardError => e
    @logger.error("Error creating access key: #{e.message}")
    nil
  end

  # Deactivates an access key
  def deactivate_access_key(user_name, access_key_id)
    @iam.update_access_key(
      user_name: user_name,
      access_key_id: access_key_id,
      status: "Inactive"
    )
    true
  rescue StandardError => e
    @logger.error("Error deactivating access key: #{e.message}")
    false
  end

  # Deletes an access key
  def delete_access_key(user_name, access_key_id)
    @iam.delete_access_key(
      user_name: user_name,
      access_key_id: access_key_id
    )
    true
  rescue StandardError => e
    @logger.error("Error deleting access key: #{e.message}")
    false
  end
end
# snippet-end:[iam.ruby.exampleAccessKeys]

# Example usage:
if __FILE__ == $PROGRAM_NAME
  iam = Aws::IAM::Client.new
  manager = AccessKeyManager.new(iam)
  user_name = "my-user"

  puts "Access keys for '#{user_name}':"
  keys_before = manager.list_access_keys(user_name)
  puts keys_before.any? ? keys_before.join("\n") : "No keys"

  new_key = manager.create_access_key(user_name)
  puts "New key created: #{new_key.access_key_id}" if new_key

  keys_after = manager.list_access_keys(user_name)
  puts "Access keys after creation:"
  puts keys_after.any? ? keys_after.join("\n") : "No keys"

  # Optionally deactivate and delete the new access key
  if new_key && manager.deactivate_access_key(user_name, new_key.access_key_id)
    puts "Key #{new_key.access_key_id} deactivated."
  end

  if new_key && manager.delete_access_key(user_name, new_key.access_key_id)
    puts "Key #{new_key.access_key_id} deleted."
  end

  final_keys = manager.list_access_keys(user_name)
  puts "Final access keys for '#{user_name}':"
  puts final_keys.any? ? final_keys.join("\n") : "No keys"
end
