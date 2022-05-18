# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
#
# Shows how to use the AWS SDK for Ruby to create an AWS Identity and Access
# Management (IAM) user, assume a role, and perform AWS actions.
#
# 1. Create a user who has no permissions.
# 2. Create a role that grants permission to list Amazon Simple Storage Service
#    (Amazon S3) buckets for the account.
# 3. Add a policy to let the user assume the role.
# 4. Assume the role and list S3 buckets using temporary credentials.
# 5. Delete the policy, role, and user.

# snippet-start:[ruby.example_code.iam.Scenario_CreateUserAssumeRole]
require "aws-sdk-iam"
require "aws-sdk-s3"

# Wraps the scenario actions.
class ScenarioCreateUserAssumeRole
  attr_reader :iam_resource

  # @param iam_resource [Aws::IAM::Resource] An AWS IAM resource.
  def initialize(iam_resource)
    @iam_resource = iam_resource
  end

  # Waits for the specified number of seconds.
  #
  # @param duration [Integer] The number of seconds to wait.
  def wait(duration)
    puts("Give AWS time to propagate resources...")
    sleep(duration)
  end

  # snippet-start:[ruby.example_code.iam.CreateUser]
  # Creates a user.
  #
  # @param user_name [String] The name to give the user.
  # @return [Aws::IAM::User] The newly created user.
  def create_user(user_name)
    user = @iam_resource.create_user(user_name: user_name)
    puts("Created demo user named #{user.name}.")
  rescue Aws::Errors::ServiceError => e
    puts("Tried and failed to create demo user.")
    puts("\t#{e.code}: #{e.message}")
    puts("\nCan't continue the demo without a user!")
    raise
  else
    user
  end
  # snippet-end:[ruby.example_code.iam.CreateUser]

  # snippet-start:[ruby.example_code.iam.CreateAccessKey]
  # Creates an access key for a user.
  #
  # @param user [Aws::IAM::User] The user that owns the key.
  # @return [Aws::IAM::AccessKeyPair] The newly created access key.
  def create_access_key_pair(user)
    user_key = user.create_access_key_pair
    puts("Created access key pair for user.")
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't create access keys for user #{user.name}.")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    user_key
  end
  # snippet-end:[ruby.example_code.iam.CreateAccessKey]

  # snippet-start:[ruby.example_code.iam.CreateRole]
  # Creates a role that can be assumed by a user.
  #
  # @param role_name [String] The name to give the role.
  # @param user [Aws::IAM::User] The user who is granted permission to assume the role.
  # @return [Aws::IAM::Role] The newly created role.
  def create_role(role_name, user)
    role = @iam_resource.create_role(
      role_name: role_name,
      assume_role_policy_document: {
        Version: "2012-10-17",
        Statement: [{
          Effect: "Allow",
          Principal: {'AWS': user.arn},
          Action: "sts:AssumeRole"
        }]
      }.to_json)
    puts("Created role #{role.name}.")
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't create a role for the demo. Here's why: ")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    role
  end
  # snippet-end:[ruby.example_code.iam.CreateRole]

  # snippet-start:[ruby.example_code.iam.CreatePolicy]
  # snippet-start:[ruby.example_code.iam.AttachRolePolicy]
  # Creates a policy that grants permission to list S3 buckets in the account, and
  # then attaches the policy to a role.
  #
  # @param policy_name [String] The name to give the policy.
  # @param role [Aws::IAM::Role] The role that the policy is attached to.
  # @return [Aws::IAM::Policy] The newly created policy.
  def create_and_attach_role_policy(policy_name, role)
    policy = @iam_resource.create_policy(
      policy_name: policy_name,
      policy_document: {
        Version: "2012-10-17",
        Statement: [{
          Effect: "Allow",
          Action: "s3:ListAllMyBuckets",
          Resource: "arn:aws:s3:::*"
        }]
      }.to_json)
    role.attach_policy(policy_arn: policy.arn)
    puts("Created policy #{policy.policy_name} and attached it to role #{role.name}.")
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't create a policy and attach it to role #{role.name}. Here's why: ")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    policy
  end
  # snippet-end:[ruby.example_code.iam.AttachRolePolicy]
  # snippet-end:[ruby.example_code.iam.CreatePolicy]

  # snippet-start:[ruby.example_code.iam.PutUserPolicy]
  # Creates an inline policy for a user that lets the user assume a role.
  #
  # @param policy_name [String] The name to give the policy.
  # @param user [Aws::IAM::User] The user that owns the policy.
  # @param role [Aws::IAM::Role] The role that can be assumed.
  # @return [Aws::IAM::UserPolicy] The newly created policy.
  def create_user_policy(policy_name, user, role)
    policy = user.create_policy(
      policy_name: policy_name,
      policy_document: {
        Version: "2012-10-17",
        Statement: [{
          Effect: "Allow",
          Action: "sts:AssumeRole",
          Resource: role.arn
        }]
      }.to_json)
    puts("Created an inline policy for #{user.name} that lets the user assume role #{role.name}.")
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't create an inline policy for user #{user.name}. Here's why: ")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    policy
  end
  # snippet-end:[ruby.example_code.iam.PutUserPolicy]

  # Creates an Amazon S3 resource with specified credentials. This is separated into a
  # factory function so that it can be mocked for unit testing.
  #
  # @param credentials [Aws::Credentials] The credentials used by the Amazon S3 resource.
  def create_s3_resource(credentials)
    Aws::S3::Resource.new(client: Aws::S3::Client.new(credentials: credentials))
  end

  # Lists the S3 buckets for the account, using the specified Amazon S3 resource.
  # Because the resource uses credentials with limited access, it may not be able to
  # list the S3 buckets.
  #
  # @param s3_resource [Aws::S3::Resource] An Amazon S3 resource.
  def list_buckets(s3_resource)
    count = 10
    s3_resource.buckets.each do |bucket|
      puts "\t#{bucket.name}"
      count -= 1
      break if count.zero?
    end
  rescue Aws::Errors::ServiceError => e
    if e.code == "AccessDenied"
      puts("Attempt to list buckets with no permissions: AccessDenied.")
    else
      puts("Couldn't list buckets for the account. Here's why: ")
      puts("\t#{e.code}: #{e.message}")
      raise
    end
  end

  # snippet-start:[ruby.example_code.sts.AssumeRole]
  # Creates an AWS Security Token Service (AWS STS) client with specified credentials.
  # This is separated into a factory function so that it can be mocked for unit testing.
  #
  # @param key_id [String] The ID of the access key used by the STS client.
  # @param key_secret [String] The secret part of the access key used by the STS client.
  def create_sts_client(key_id, key_secret)
    Aws::STS::Client.new(access_key_id: key_id, secret_access_key: key_secret)
  end

  # Gets temporary credentials that can be used to assume a role.
  #
  # @param role_arn [String] The ARN of the role that is assumed when these credentials
  #                          are used.
  # @param sts_client [AWS::STS::Client] An AWS STS client.
  # @return [Aws::AssumeRoleCredentials] The credentials that can be used to assume the role.
  def assume_role(role_arn, sts_client)
    credentials = Aws::AssumeRoleCredentials.new(
      client: sts_client,
      role_arn: role_arn,
      role_session_name: "create-use-assume-role-scenario"
    )
    puts("Assumed role '#{role_arn}', got temporary credentials.")
    credentials
  end
  # snippet-end:[ruby.example_code.sts.AssumeRole]

  # snippet-start:[ruby.example_code.iam.DeleteRole]
  # Deletes a role. If the role has policies attached, they are detached and
  # deleted before the role is deleted.
  #
  # @param role [Aws::IAM::Role] The role to delete.
  def delete_role(role)
    role.attached_policies.each do |policy|
      name = policy.policy_name
      policy.detach_role(role_name: role.name)
      policy.delete
      puts("Deleted policy #{name}.")
    end
    name = role.name
    role.delete
    puts("Deleted role #{name}.")
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't detach policies and delete role #{role.name}. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.iam.DeleteRole]

  # snippet-start:[ruby.example_code.iam.DeleteUser]
  # Deletes a user. If the user has inline policies or access keys, they are deleted
  # before the user is deleted.
  #
  # @param user [Aws::IAM::User] The user to delete.
  def delete_user(user)
    user.policies.each do |policy|
      name = policy.name
      policy.delete
      puts("Deleted user policy #{name}.")
    end
    user.access_keys.each do |key|
      key.delete
      puts("Deleted access key for user #{user.name}.")
    end
    name = user.name
    user.delete
    puts("Deleted user #{name}.")
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't detach policies and delete user #{user.name}. Here's why:")
    puts("\t#{e.code}: #{e.message}")
  end
  # snippet-end:[ruby.example_code.iam.DeleteUser]
end

# Runs the IAM create a user and assume a role scenario.
def run_scenario(scenario)
  puts("-" * 88)
  puts("Welcome to the IAM create a user and assume a role demo!")
  puts("-" * 88)

  user = scenario.create_user("doc-example-user-#{Random.uuid}")
  user_key = scenario.create_access_key_pair(user)
  scenario.wait(10)
  role = scenario.create_role("doc-example-role-#{Random.uuid}", user)
  scenario.create_and_attach_role_policy("doc-example-role-policy-#{Random.uuid}", role)
  scenario.create_user_policy("doc-example-user-policy-#{Random.uuid}", user, role)
  scenario.wait(10)
  puts("Try to list buckets with credentials for a user who has no permissions.")
  puts("Expect AccessDenied from this call.")
  scenario.list_buckets(
    scenario.create_s3_resource(Aws::Credentials.new(user_key.id, user_key.secret)))
  puts("Now, assume the role that grants permission.")
  temp_credentials = scenario.assume_role(
    role.arn, scenario.create_sts_client(user_key.id, user_key.secret))
  puts("Here are your buckets:")
  scenario.list_buckets(scenario.create_s3_resource(temp_credentials))
  puts("Deleting role '#{role.name}' and attached policies.")
  scenario.delete_role(role)
  puts("Deleting user '#{user.name}', policies, and keys.")
  scenario.delete_user(user)

  puts("Thanks for watching!")
  puts("-" * 88)
rescue Aws::Errors::ServiceError => e
  puts("Something went wrong with the demo.")
  puts("\t#{e.code}: #{e.message}")
end

run_scenario(ScenarioCreateUserAssumeRole.new(Aws::IAM::Resource.new)) if $PROGRAM_NAME == __FILE__
# snippet-end:[ruby.example_code.iam.Scenario_CreateUserAssumeRole]
