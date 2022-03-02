# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
#
# Shows how to use the AWS SDK for Ruby to perform common AWS Identity and Access
# Management (IAM) actions that are not covered in other scenarios.

require "aws-sdk-iam"

# Wraps common IAM service actions.
class IamWrapper
  attr_reader :iam_resource

  # @param iam_resource [Aws::IAM::Resource] An IAM resource.
  def initialize(iam_resource)
    @iam_resource = iam_resource
  end

  # Asks the user a question at a command prompt.
  #
  # @param question [String] The question to ask.
  # @return [String] The answer to the question.
  def ask_question(question)
    puts("\n#{question}")
    gets.chomp
  end

  # snippet-start:[ruby.example_code.iam.ListRoles]
  # Lists up to a specified number of roles for the account.
  #
  # @param count [Integer] The maximum number of roles to list.
  # @return [Array] The names of the listed roles.
  def list_roles(count)
    role_names = []
    @iam_resource.roles.limit(count).each_with_index do |role, index|
      puts("\t#{index + 1}: #{role.name}")
      role_names.append(role.name)
    end
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't list roles for the account. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    role_names
  end
  # snippet-end:[ruby.example_code.iam.ListRoles]

  # snippet-start:[ruby.example_code.iam.GetRole]
  # Gets data about a role.
  #
  # @param name [String] The name of the role to look up.
  # @return [Aws::IAM::Role] The retrieved role.
  def get_role(name)
    role = @iam_resource.role(name)
    puts("Got data for role '#{role.name}'. Its ARN is '#{role.arn}'.")
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't get data for role '#{name}' Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    role
  end
  # snippet-end:[ruby.example_code.iam.GetRole]

  # snippet-start:[ruby.example_code.iam.ListUsers]
  # Lists up to a specified number of users in the account.
  #
  # @param count [Integer] The maximum number of users to list.
  def list_users(count)
    @iam_resource.users.limit(count).each do |user|
      puts("\t#{user.name}")
    end
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't list users for the account. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.iam.ListUsers]

  # snippet-start:[ruby.example_code.iam.ListPolicies]
  # Lists up to a specified number of policies in the account.
  #
  # @param count [Integer] The maximum number of policies to list.
  # @return [Array] The Amazon Resource Names (ARNs) of the policies listed.
  def list_policies(count)
    policy_arns = []
    @iam_resource.policies.limit(count).each_with_index do |policy, index|
      puts("\t#{index + 1}: #{policy.policy_name}: #{policy.arn}")
      policy_arns.append(policy.arn)
    end
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't list policies for the account. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    policy_arns
  end
  # snippet-end:[ruby.example_code.iam.ListPolicies]

  # snippet-start:[ruby.example_code.iam.GetPolicy]
  # Gets data about a policy.
  #
  # @param policy_arn [String] The ARN of the policy to look up.
  # @return [Aws::IAM::Policy] The retrieved policy.
  def get_policy(policy_arn)
    policy = @iam_resource.policy(policy_arn)
    puts("Got policy '#{policy.policy_name}'. Its ID is: #{policy.policy_id}.")
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't get policy '#{policy_arn}'. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    policy
  end
  # snippet-end:[ruby.example_code.iam.GetPolicy]

  # snippet-start:[ruby.example_code.iam.ListGroups]
  # Lists up to a specified number of groups for the account.
  #
  # @param count [Integer] The maximum number of groups to list.
  def list_groups(count)
    @iam_resource.groups.limit(count).each do |group|
      puts("\t#{group.name}")
    end
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't list groups for the account. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.iam.ListGroups]

  # snippet-start:[ruby.example_code.iam.GetAccountPasswordPolicy]
  # Prints the password policy for the account.
  def print_account_password_policy
    policy = @iam_resource.account_password_policy
    policy.load
    puts("The account password policy is:")
    puts(policy.data.to_h)
  rescue Aws::Errors::ServiceError => e
    if e.code == "NoSuchEntity"
      puts("The account does not have a password policy.")
    else
      puts("Couldn't print the account password policy. Here's why:")
      puts("\t#{e.code}: #{e.message}")
      raise
    end
  end
  # snippet-end:[ruby.example_code.iam.GetAccountPasswordPolicy]

  # snippet-start:[ruby.example_code.iam.ListSamlProviders]
  # Lists up to a specified number of SAML providers for the account.
  #
  # @param count [Integer] The maximum number of providers to list.
  def list_saml_providers(count)
    @iam_resource.saml_providers.limit(count).each do |provider|
      puts("\t#{provider.arn}")
    end
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't list SAML providers. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.iam.ListSamlProviders]

  # snippet-start:[ruby.example_code.iam.CreateServiceLinkedRole]
  # Creates a service-linked role.
  #
  # @param service_name [String] The name of the service that owns the role.
  # @param description [String] A description to give the role.
  # @return [Aws::IAM::Role] The newly created role.
  def create_service_linked_role(service_name, description)
    response = @iam_resource.client.create_service_linked_role(
      aws_service_name: service_name, description: description)
    role = @iam_resource.role(response.role.role_name)
    puts("Created service-linked role #{role.name}.")
  rescue Aws::Errors::ServiceError => e
    puts("Couldn't create service-linked role for #{service_name}. Here's why:")
    puts("\t#{e.code}: #{e.message}")
    raise
  else
    role
  end
  # snippet-end:[ruby.example_code.iam.CreateServiceLinkedRole]

  # snippet-start:[ruby.example_code.iam.DeleteServiceLinkedRole]
  # Deletes a service-linked role from the account.
  #
  # @param role [Aws::IAM::Role] The role to delete.
  def delete_service_linked_role(role)
      response = @iam_resource.client.delete_service_linked_role(role_name: role.name)
      task_id = response.deletion_task_id
      while true
        response = @iam_resource.client.get_service_linked_role_deletion_status(
          deletion_task_id: task_id)
        status = response.status
        puts("Deletion of #{role.name} #{status}.")
        if %w(SUCCEEDED FAILED).include?(status)
          break
        else
          sleep(3)
        end
      end
  rescue Aws::Errors::ServiceError => e
    # If AWS has not yet fully propagated the role, it deletes the role but
    # returns NoSuchEntity.
    if e.code != "NoSuchEntity"
      puts("Couldn't delete #{role.name}. Here's why:")
      puts("\t#{e.code}: #{e.message}")
      raise
    end
  end
  # snippet-end:[ruby.example_code.iam.DeleteServiceLinkedRole]
end

# Runs the IAM common service actions demo.
def usage_demo(wrapper)
  puts("-" * 88)
  puts("Welcome to the IAM common actions demo!")
  puts("-" * 88)

  count = 10
  puts("Listing up to #{count} roles for the account:")
  role_names = wrapper.list_roles(count)
  role_index = wrapper.ask_question("Enter the number of a role to look up its ARN: ").to_i
  wrapper.get_role(role_names[role_index - 1])
  puts("Listing up to #{count} users for the account:")
  wrapper.list_users(count)
  puts("Listing up to #{count} policies for the account:")
  policy_arns = wrapper.list_policies(count)
  policy_index = wrapper.ask_question("Enter the number of a policy to look up its ID: ").to_i
  wrapper.get_policy(policy_arns[policy_index - 1])
  puts("Listing up to #{count} groups for the account:")
  wrapper.list_groups(count)
  puts("Looking up the password policy for the account.")
  wrapper.print_account_password_policy
  puts("Listing up to #{count} SAML providers for the account:")
  wrapper.list_saml_providers(count)
  service_name = wrapper.ask_question(
    "Enter the name of a service to create a service-linked role. "\
    "For example, 'elasticbeanstalk.amazonaws.com' or 'batch.amazonaws.com': ")
  role = wrapper.create_service_linked_role(service_name, "Example service-linked role.")
  answer = wrapper.ask_question(
    "Do you want to delete the role? You should only do this if you are sure "\
    "it is not being used. (y/n)? ").downcase
  if answer == "y"
    puts("Deleting the service-linked role '#{role.name}'.")
    wrapper.delete_service_linked_role(role)
  end

  puts("Thanks for watching!")
  puts("-" * 88)

rescue Aws::Errors::ServiceError => e
  puts("Something went wrong with the demo. Here are the details:")
  puts("\t#{e.code}: #{e.message}")
end

usage_demo(IamWrapper.new(Aws::IAM::Resource.new)) if $PROGRAM_NAME == __FILE__
