# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
require 'aws-sdk-iam'
require 'logger'

# snippet-start:[ruby.example_code.iam.GetAccountPasswordPolicy]
# Class to manage IAM account password policies
class PasswordPolicyManager
  attr_accessor :iam_client, :logger

  def initialize(iam_client, logger: Logger.new($stdout))
    @iam_client = iam_client
    @logger = logger
    @logger.progname = 'IAMPolicyManager'
  end

  # Retrieves and logs the account password policy
  def print_account_password_policy
    response = @iam_client.get_account_password_policy
    @logger.info("The account password policy is: #{response.password_policy.to_h}")
  rescue Aws::IAM::Errors::NoSuchEntity
    @logger.info('The account does not have a password policy.')
  rescue Aws::Errors::ServiceError => e
    @logger.error("Couldn't print the account password policy. Error: #{e.code} - #{e.message}")
    raise
  end
end
# snippet-end:[ruby.example_code.iam.GetAccountPasswordPolicy]

# Example usage:
if $PROGRAM_NAME == __FILE__
  iam_client = Aws::IAM::Client.new
  iam_policy_manager = PasswordPolicyManager.new(iam_client)
  iam_policy_manager.print_account_password_policy
end
