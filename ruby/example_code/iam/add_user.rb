# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0
require "aws-sdk-iam"
require "logger"

# snippet-start:[ruby.iam.ManageUsers]
# Manages IAM users
class UserManager
  # Initialize with an AWS IAM client and a logger
  #
  # @param iam_client [Aws::IAM::Client] An initialized IAM client
  def initialize(iam_client, logger = Logger.new($stdout))
    @iam_client = iam_client
    @logger = logger
    @logger.progname = "UserManager"
  end

  # snippet-start:[ruby.iam.CreateUser]
  # Creates a user and their login profile
  #
  # @param user_name [String] The name of the user
  # @param initial_password [String] The initial password for the user
  # @return [String, nil] The ID of the user if created, or nil if an error occurred
  def create_user(user_name, initial_password)
    response = @iam_client.create_user(user_name: user_name)
    @iam_client.wait_until(:user_exists, user_name: user_name)
    @iam_client.create_login_profile(
      user_name: user_name,
      password: initial_password,
      password_reset_required: true
    )
    @logger.info("User '#{user_name}' created successfully.")
    response.user.user_id
  rescue Aws::IAM::Errors::EntityAlreadyExists
    @logger.error("Error creating user '#{user_name}': user already exists.")
    nil
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error creating user '#{user_name}': #{e.message}")
    nil
  end
  # snippet-end:[ruby.iam.CreateUser]

  # snippet-start:[ruby.iam.UpdateUser]
  # Updates an IAM user's name
  #
  # @param current_name [String] The current name of the user
  # @param new_name [String] The new name of the user
  def update_user_name(current_name, new_name)
    @iam_client.update_user(user_name: current_name, new_user_name: new_name)
    true
  rescue StandardError => e
    @logger.error("Error updating user name from '#{current_name}' to '#{new_name}': #{e.message}")
    false
  end
  # snippet-end:[ruby.iam.UpdateUser]

  # snippet-start:[ruby.iam.DeleteUser]
  # Deletes a user and their associated resources
  #
  # @param user_name [String] The name of the user to delete
  def delete_user(user_name)
    user = @iam_client.list_access_keys(user_name: user_name).access_key_metadata
    user.each do |key|
      @iam_client.delete_access_key({ access_key_id: key.access_key_id, user_name: user_name })
      @logger.info("Deleted access key #{key.access_key_id} for user '#{user_name}'.")
    end

    @iam_client.delete_user(user_name: user_name)
    @logger.info("Deleted user '#{user_name}'.")
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error deleting user '#{user_name}': #{e.message}")
  end
  # snippet-end:[ruby.iam.DeleteUser]


  # snippet-start:[ruby.iam.GetUser]
  # Retrieves a user's details
  #
  # @param user_name [String] The name of the user to retrieve
  # @return [Aws::IAM::Types::User, nil] The user object if found, or nil if an error occurred
  def get_user(user_name)
    response = @iam_client.get_user(user_name: user_name)
    response.user
  rescue Aws::IAM::Errors::NoSuchEntity
    @logger.error("User '#{user_name}' not found.")
    nil
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error retrieving user '#{user_name}': #{e.message}")
    nil
  end
  # snippet-end:[ruby.iam.GetUser]

  # snippet-start:[ruby.iam.ListUsers]
  # Lists all users in the AWS account
  #
  # @return [Array<Aws::IAM::Types::User>] An array of user objects
  def list_users
    users = []
    @iam_client.list_users.each_page do |page|
      page.users.each do |user|
        users << user
      end
    end
    users
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error listing users: #{e.message}")
    []
  end
  # snippet-end:[ruby.iam.ListUsers]
end
# snippet-end:[ruby.iam.ManageUsers]

# Example usage:
if __FILE__ == $PROGRAM_NAME
  iam_client = Aws::IAM::Client.new
  logger = Logger.new($stdout)
  user_manager = UserManager.new(iam_client, logger)
  user_name = "example-user"
  initial_password = "InitialP@ssw0rd!"

  # Create a new IAM user
  if (user_id = user_manager.create_user(user_name, initial_password))
    logger.info("User '#{user_name}' created with ID '#{user_id}' and initial password '#{initial_password}'.")
  else
    logger.error("User not created.")
  end

  # Retrieve details of the created user
  if (user = user_manager.get_user(user_name))
    logger.info("Retrieved user '#{user_name}' with creation date #{user.create_date}.")
  else
    logger.error("Could not retrieve user details.")
  end

  # List all IAM users
  users = user_manager.list_users
  if users.any?
    logger.info("Listing all users:")
    users.each { |u| logger.info("User: #{u.user_name}") }
  else
    logger.error("No users found.")
  end

  # Delete the created user
  if user_manager.delete_user(user_name)
    logger.info("User '#{user_name}' deleted successfully.")
  else
    logger.error("Failed to delete user '#{user_name}'.")
  end
end
