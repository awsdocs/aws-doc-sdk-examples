# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require 'logger'

# Manages IAM user policies
class UserPolicyManager
  # Initialize with an AWS IAM client
  #
  # @param iam_client [Aws::IAM::Client] An initialized IAM client
  def initialize(iam_client, logger: Logger.new($stdout))
    @iam_client = iam_client
    @logger = logger
    @logger.progname = 'UserManager'
  end

  # snippet-start:[ruby.iam.PutUserPolicy]
  # Creates an inline policy for a specified user.
  # @param username [String] The name of the IAM user.
  # @param policy_name [String] The name of the policy to create.
  # @param policy_document [String] The JSON policy document.
  # @return [Boolean]
  def create_user_policy(username, policy_name, policy_document)
    @iam_client.put_user_policy({
                                  user_name: username,
                                  policy_name: policy_name,
                                  policy_document: policy_document
                                })
    @logger.info("Policy #{policy_name} created for user #{username}.")
    true
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Couldn't create policy #{policy_name} for user #{username}. Here's why:")
    @logger.error("\t#{e.code}: #{e.message}")
    false
  end
  # snippet-end:[ruby.iam.PutUserPolicy]

  # snippet-start:[ruby.iam.AttachUserPolicy]
  # Attaches a policy to a user
  #
  # @param user_name [String] The name of the user
  # @param policy_arn [String] The Amazon Resource Name (ARN) of the policy
  # @return [Boolean] true if successful, false otherwise
  def attach_policy_to_user(user_name, policy_arn)
    @iam_client.attach_user_policy(
      user_name: user_name,
      policy_arn: policy_arn
    )
    true
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error attaching policy to user: #{e.message}")
    false
  end
  # snippet-end:[ruby.iam.AttachUserPolicy]

  # snippet-start:[ruby.iam.DetachUserPolicy]
  # Detaches a policy from a user
  #
  # @param user_name [String] The name of the user
  # @param policy_arn [String] The ARN of the policy to detach
  # @return [Boolean] true if the policy was successfully detached, false otherwise
  def detach_user_policy(user_name, policy_arn)
    @iam_client.detach_user_policy(
      user_name: user_name,
      policy_arn: policy_arn
    )
    @logger.info("Policy '#{policy_arn}' detached from user '#{user_name}' successfully.")
    true
  rescue Aws::IAM::Errors::NoSuchEntity
    @logger.error('Error detaching policy: Policy or user does not exist.')
    false
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error detaching policy from user '#{user_name}': #{e.message}")
    false
  end
  # snippet-end:[ruby.iam.DetachUserPolicy]
end

# Example usage:
if __FILE__ == $PROGRAM_NAME
  iam_client = Aws::IAM::Client.new
  user_manager = UserPolicyManager.new(iam_client)
  user_name = 'my-user'
  policy_arn = 'arn:aws:iam::aws:policy/AmazonS3FullAccess'

  if user_manager.attach_policy_to_user(user_name, policy_arn)
    puts "Policy attached to user '#{user_name}'."
  else
    puts "Failed to attach policy to user '#{user_name}'."
  end

  if user_manager.detach_user_policy(user_name, policy_arn)
    puts "Policy detached from user '#{user_name}'."
  else
    puts "Failed to detach policy from user '#{user_name}'."
  end

  policy_name = "test-policy-#{Time.now.to_i}"
  policy_document =
    {
      'Version' => '2012-10-17',
      'Statement' => [
        {
          'Effect' => 'Allow',
          'Action' => 's3:ListAllMyBuckets',
          'Resource' => 'arn:aws:s3:::*'
        }
      ]
    }
  if user_manager.create_user_policy(user_name, policy_name, policy_document.to_json)
    puts "Created user policy for '#{user_name}'."
  else
    puts "Failed to create user policy for '#{user_name}'."
  end
end
