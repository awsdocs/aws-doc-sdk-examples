# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require 'logger'

# snippet-start:[ruby.iam.ScenarioListUsers]
class UserManager
  # Initializes the UserManager with an IAM client and a logger.
  #
  # @param iam_client [Aws::IAM::Client] An initialized IAM client.
  def initialize(iam_client, logger: Logger.new($stdout))
    @iam_client = iam_client
    @logger = logger
  end

  # Displays information about available users in AWS Identity and Access Management (IAM).
  # This includes user names, associated group names, inline embedded user policy names,
  # and access key IDs. Logging is added for monitoring purposes.
  def user_details
    @logger.info('Requesting list of users')
    users_response = @iam_client.list_users

    if users_response.users.empty?
      @logger.warn('No users found.')
      puts 'No users found.'
      return
    end

    message = if users_response.is_truncated
                "(Note: not all users are displayed here, only the first #{users_response.users.count}.)"
              else
                "Found #{users_response.users.count} user(s):"
              end
    @logger.info(message)
    puts message

    users_response.users.each do |user|
      display_user_details(user)
    end
  rescue StandardError => e
    @logger.error("Error getting user details: #{e.message}")
    puts "Error getting user details: #{e.message}"
  end

  private

  def display_user_details(user)
    @logger.info("Displaying details for user: #{user.user_name}")
    puts '-' * 30
    puts "User name: #{user.user_name}"

    display_groups(user.user_name)
    display_policies(user.user_name)
    display_access_keys(user.user_name)
  end

  def display_groups(user_name)
    @logger.info("Listing groups for user: #{user_name}")
    puts 'Groups:'
    groups_response = @iam_client.list_groups_for_user(user_name: user_name)
    if groups_response.groups.empty?
      puts '  None'
    else
      groups_response.groups.each { |group| puts "  #{group.group_name}" }
    end
  end

  def display_policies(user_name)
    @logger.info("Listing policies for user: #{user_name}")
    puts 'Inline embedded user policies:'
    policies_response = @iam_client.list_user_policies(user_name: user_name)
    if policies_response.policy_names.empty?
      puts '  None'
    else
      policies_response.policy_names.each { |policy_name| puts "  #{policy_name}" }
    end
  end

  def display_access_keys(user_name)
    @logger.info("Listing access keys for user: #{user_name}")
    puts 'Access keys:'
    access_keys_response = @iam_client.list_access_keys(user_name: user_name)
    if access_keys_response.access_key_metadata.empty?
      puts '  None'
    else
      access_keys_response.access_key_metadata.each { |access_key| puts "  #{access_key.access_key_id}" }
    end
  end
end
# snippet-end:[ruby.iam.ScenarioListUsers]

# Example usage:
if __FILE__ == $PROGRAM_NAME
  iam_client = Aws::IAM::Client.new
  user_manager = UserManager.new(iam_client)
  puts 'Attempting to get details for available users...'
  user_manager.get_user_details
end
