# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require 'logger'

# snippet-start:[ruby.iam.AttachRolePolicy]
# Manages policies in AWS Identity and Access Management (IAM)
class RolePolicyManager
  # Initialize with an AWS IAM client
  #
  # @param iam_client [Aws::IAM::Client] An initialized IAM client
  def initialize(iam_client, logger: Logger.new($stdout))
    @iam_client = iam_client
    @logger = logger
    @logger.progname = 'PolicyManager'
  end

  # Creates a policy
  #
  # @param policy_name [String] The name of the policy
  # @param policy_document [Hash] The policy document
  # @return [String] The policy ARN if successful, otherwise nil
  def create_policy(policy_name, policy_document)
    response = @iam_client.create_policy(
      policy_name: policy_name,
      policy_document: policy_document.to_json
    )
    response.policy.arn
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error creating policy: #{e.message}")
    nil
  end

  # snippet-start:[ruby.example_code.iam.GetPolicy]
  # Fetches an IAM policy by its ARN
  # @param policy_arn [String] the ARN of the IAM policy to retrieve
  # @return [Aws::IAM::Types::GetPolicyResponse] the policy object if found
  def get_policy(policy_arn)
    response = @iam_client.get_policy(policy_arn: policy_arn)
    policy = response.policy
    @logger.info("Got policy '#{policy.policy_name}'. Its ID is: #{policy.policy_id}.")
    policy
  rescue Aws::IAM::Errors::NoSuchEntity
    @logger.error("Couldn't get policy '#{policy_arn}'. The policy does not exist.")
    raise
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Couldn't get policy '#{policy_arn}'. Here's why: #{e.code}: #{e.message}")
    raise
  end
  # snippet-end:[ruby.example_code.iam.GetPolicy]

  # Attaches a policy to a role
  #
  # @param role_name [String] The name of the role
  # @param policy_arn [String] The policy ARN
  # @return [Boolean] true if successful, false otherwise
  def attach_policy_to_role(role_name, policy_arn)
    @iam_client.attach_role_policy(
      role_name: role_name,
      policy_arn: policy_arn
    )
    true
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error attaching policy to role: #{e.message}")
    false
  end

  # snippet-start:[ruby.iam.ListRolePolicies]
  # Lists policy ARNs attached to a role
  #
  # @param role_name [String] The name of the role
  # @return [Array<String>] List of policy ARNs
  def list_attached_policy_arns(role_name)
    response = @iam_client.list_attached_role_policies(role_name: role_name)
    response.attached_policies.map(&:policy_arn)
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error listing policies attached to role: #{e.message}")
    []
  end
  # snippet-end:[ruby.iam.ListRolePolicies]

  # Detaches a policy from a role
  #
  # @param role_name [String] The name of the role
  # @param policy_arn [String] The policy ARN
  # @return [Boolean] true if successful, false otherwise
  def detach_policy_from_role(role_name, policy_arn)
    @iam_client.detach_role_policy(
      role_name: role_name,
      policy_arn: policy_arn
    )
    true
  rescue Aws::IAM::Errors::ServiceError => e
    @logger.error("Error detaching policy from role: #{e.message}")
    false
  end
end
# snippet-end:[ruby.iam.AttachRolePolicy]

# Example usage:
if __FILE__ == $PROGRAM_NAME
  iam_client = Aws::IAM::Client.new
  manager = RolePolicyManager.new(iam_client)
  role_name = 'my-role'
  policy_name = 'my-policy'
  policy_document = {
    'Version' => '2012-10-17',
    'Statement' => [
      {
        'Effect' => 'Allow',
        'Action' => 's3:ListAllMyBuckets',
        'Resource' => 'arn:aws:s3:::*'
      }
    ]
  }
  iam_client.create_role(
    role_name: role_name,
    assume_role_policy_document: {
      'Version' => '2012-10-17',
      'Statement' => [
        {
          'Effect' => 'Allow',
          'Principal' => { 'Service' => 'ec2.amazonaws.com' },
          'Action' => 'sts:AssumeRole'
        }
      ]
    }.to_json
  )
  policy_arn = manager.create_policy(policy_name, policy_document)
  if policy_arn
    manager.attach_policy_to_role(role_name, policy_arn)
    attached_policies = manager.list_attached_policy_arns(role_name)
    puts "Policy ARNs attached to role '#{role_name}': #{attached_policies}"
    manager.detach_policy_from_role(role_name, policy_arn)
  else
    puts 'Failed to create policy.'
  end
end
