# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-iam'

# Gets information for up to the first 100 SSH public keys for the specified
# user in AWS Identity and Access Management (IAM).
#
# Prerequisites:
# - The user in IAM.
#
# @param iam_client [Aws::IAM::Client] An initialized IAM client.
# @param user_name [String] The name of the user.
# @example
#   get_ssh_public_key_details(Aws::IAM::Client.new, 'my-user')
def get_ssh_public_key_details(iam_client, user_name)
  response = iam_client.list_ssh_public_keys(user_name: user_name)
  if response.ssh_public_keys.count.positive?
    puts 'SSH public key details (up to the first 100 SSH public keys) ' \
      "for user '#{user_name}':"
    response.ssh_public_keys.each do |ssh_public_key|
      puts '-' * 36
      puts "Key ID:      #{ssh_public_key.ssh_public_key_id}"
      puts "Status:      #{ssh_public_key.status}"
      puts "Upload date: #{ssh_public_key.upload_date}"
    end
  else
    puts 'No SSH public keys found.'
  end
rescue StandardError => e
  puts "Error getting SSH public key details: #{e.message}"
end

# Full example call:
def run_me
  user_name = 'my-user'
  iam_client = Aws::IAM::Client.new

  puts "Attempting to get SSH public key details for user '#{user_name}'..."
  get_ssh_public_key_details(iam_client, user_name)
end

run_me if $PROGRAM_NAME == __FILE__
