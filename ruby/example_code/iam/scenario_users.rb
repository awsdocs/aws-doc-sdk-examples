require 'aws-sdk-iam'
require 'aws-sdk-s3'
require 'logger'
require 'securerandom'
require 'json'

# Demonstrates how to create an IAM user, assume a role, and perform actions with AWS services.
class IAMUserAndRoleManager
  # Initializes the manager with IAM and STS clients.
  #
  # @param iam_client [Aws::IAM::Client] The IAM client.
  # @param logger [Logger] The logger for outputting information.
  def initialize(iam_client, logger = Logger.new($stdout))
    @iam_client = iam_client
    @logger = logger
  end

  # Creates an IAM user with no permissions.
  #
  # @param user_name [String] The name of the user to create.
  # @return [Aws::IAM::Types::User] The created IAM user.
  def create_user(user_name)
    @iam_client.create_user(user_name: user_name).user
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Failed to create user: #{e.message}")
    raise
  end

  # Creates an access key for the specified user.
  #
  # @param user_name [String] The name of the user for whom to create the access key.
  # @return [Aws::IAM::Types::AccessKey] The created access key.
  def create_access_key(user_name)
    @iam_client.create_access_key(user_name: user_name).access_key
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Failed to create access key for user #{user_name}: #{e.message}")
    raise
  end

  # Creates an IAM role with a trust policy that allows assumption by the given user.
  #
  # @param role_name [String] The name of the role to create.
  # @param user_arn [String] The ARN of the user who can assume the role.
  # @return [Aws::IAM::Types::Role] The created IAM role.
  def create_role(role_name, user_arn)
    assume_role_policy_document = {
      Version: '2012-10-17',
      Statement: [{
                    Effect: 'Allow',
                    Principal: { AWS: user_arn },
                    Action: 'sts:AssumeRole'
                  }]
    }.to_json

    @iam_client.create_role(
      role_name: role_name,
      assume_role_policy_document: assume_role_policy_document
    ).role
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Failed to create role #{role_name}: #{e.message}")
    raise
  end

  # Attaches a policy to the specified role that allows listing S3 buckets.
  #
  # @param role_name [String] The name of the role to attach the policy to.
  # @param policy_document [Hash] The policy document.
  def attach_policy_to_role(role_name, policy_document)
    policy_arn = 'arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess'
    @iam_client.attach_role_policy(
      role_name: role_name,
      policy_arn: policy_arn
    )
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Failed to attach policy to role #{role_name}: #{e.message}")
    raise
  end

  # snippet-start:[ruby.example_code.sts.AssumeRole]
  # Assumes a specified role and returns temporary credentials.
  #
  # @param role_arn [String] The ARN of the role to assume.
  # @param session_name [String] An identifier for the session.
  # @return [Aws::Credentials] Temporary credentials for the assumed role.
  def assume_role(role_arn, session_name)
    sts_client = Aws::STS::Client.new
    creds = sts_client.assume_role(
      role_arn: role_arn,
      role_session_name: session_name
    ).credentials

    Aws::Credentials.new(creds.access_key_id, creds.secret_access_key, creds.session_token)
  rescue Aws::STS::Errors::ServiceError => e
    @logger.error("Failed to assume role #{role_arn}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.sts.AssumeRole]


  # Deletes the specified IAM user.
  #
  # @param user_name [String] The name of the user to delete.
  def delete_user(user_name)
    @iam_client.delete_user(user_name: user_name)
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Failed to delete user #{user_name}: #{e.message}")
    raise
  end

  # Deletes the specified IAM role.
  #
  # @param role_name [String] The name of the role to delete.
  def delete_role(role_name)
    @iam_client.delete_role(role_name: role_name)
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Failed to delete role #{role_name}: #{e.message}")
    raise
  end
end

# Example usage:
if $PROGRAM_NAME == __FILE__
  iam_client = Aws::IAM::Client.new
  user_and_role_manager = IAMUserAndRoleManager.new(iam_client)
  logger = Logger.new($stdout)
  logger.info("Attempting to create and manage IAM user and role...")

  # Example usage (ensure to handle exceptions and clean up resources appropriately)
  user_name = "demo-user-#{SecureRandom.uuid}"
  role_name = "demo-role-#{SecureRandom.uuid}"
  user = user_and_role_manager.create_user(user_name)
  logger.info("Created user: #{user.user_name}")

  # Continue with role creation, policy attachment, etc.
end
