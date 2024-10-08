# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require 'logger'

# snippet-start:[iam.ruby.exampleAccessKeys]
# Manages access keys for IAM users
class AccessKeyManager
  def initialize(iam_client, logger: Logger.new($stdout))
    @iam_client = iam_client
    @logger = logger
    @logger.progname = 'AccessKeyManager'
  end

  # Lists access keys for a user
  #
  # @param user_name [String] The name of the user.
  def list_access_keys(user_name)
    response = @iam_client.list_access_keys(user_name: user_name)
    if response.access_key_metadata.empty?
      @logger.info("No access keys found for user '#{user_name}'.")
    else
      response.access_key_metadata.map(&:access_key_id)
    end
  rescue Aws::IAM::Errors::NoSuchEntity
    @logger.error("Error listing access keys: cannot find user '#{user_name}'.")
    []
  rescue StandardError => e
    @logger.error("Error listing access keys: #{e.message}")
    []
  end

  # Creates an access key for a user
  #
  # @param user_name [String] The name of the user.
  # @return [Boolean]
  def create_access_key(user_name)
    response = @iam_client.create_access_key(user_name: user_name)
    access_key = response.access_key
    @logger.info("Access key created for user '#{user_name}': #{access_key.access_key_id}")
    access_key
  rescue Aws::IAM::Errors::LimitExceeded
    @logger.error('Error creating access key: limit exceeded. Cannot create more.')
    nil
  rescue StandardError => e
    @logger.error("Error creating access key: #{e.message}")
    nil
  end

  # Deactivates an access key
  #
  # @param user_name [String] The name of the user.
  # @param access_key_id [String] The ID for the access key.
  # @return [Boolean]
  def deactivate_access_key(user_name, access_key_id)
    @iam_client.update_access_key(
      user_name: user_name,
      access_key_id: access_key_id,
      status: 'Inactive'
    )
    true
  rescue StandardError => e
    @logger.error("Error deactivating access key: #{e.message}")
    false
  end

  # Deletes an access key
  #
  # @param user_name [String] The name of the user.
  # @param access_key_id [String] The ID for the access key.
  # @return [Boolean]
  def delete_access_key(user_name, access_key_id)
    @iam_client.delete_access_key(
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
  iam_client = Aws::IAM::Client.new
  manager = AccessKeyManager.new(iam)
  user_name = "user-#{rand(10**6)}"

  iam_client.create_user(user_name: user_name)

  puts "Access keys for '#{user_name}':"
  keys_before = manager.list_access_keys(user_name)
  puts keys_before.any? ? keys_before.join("\n") : 'No keys'

  new_key = manager.create_access_key(user_name)
  puts "New key created: #{new_key.access_key_id}" if new_key

  keys_after = manager.list_access_keys(user_name)
  puts 'Access keys after creation:'
  puts keys_after.any? ? keys_after.join("\n") : 'No keys'

  # Optionally deactivate and delete the new access key
  puts "Key #{new_key.access_key_id} deactivated." if new_key && manager.deactivate_access_key(user_name, new_key.access_key_id)

  puts "Key #{new_key.access_key_id} deleted." if new_key && manager.delete_access_key(user_name, new_key.access_key_id)

  final_keys = manager.list_access_keys(user_name)
  puts "Final access keys for '#{user_name}':"
  puts final_keys.any? ? final_keys.join("\n") : 'No keys'
end
