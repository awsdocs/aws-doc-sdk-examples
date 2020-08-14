# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# This code example allows a federated user with a limited set of
# permissions to list the objects in the specified Amazon S3 bucket.

# snippet-start:[s3.ruby.auth_federation_token_request_test.rb]
require 'aws-sdk-s3'
require 'aws-sdk-iam'

# Uses an AWS IAM user with a limited set of persmissions to list the objects
# in the specified Amazon S3 bucket.
# @param region [String] The AWS Region to use for configuring the AWS STS
#   and S3 clients.
# @param user_name [String] The user's name.
# @param bucket_name [String] The bucket's name.
# @return [Boolean] true if all operations succeed; otherwise, false.
# @example
#   list_objects_with_temporary_credentials('us-east-1', 'my-user', 'my-bucket')
def list_objects_with_temporary_credentials(region, user_name, bucket_name)
  # Create a new AWS STS client and get temporary credentials.
  outcome = false
  sts = Aws::STS::Client.new(region: region)
  creds = ''

  begin
    creds = sts.get_federation_token({
      duration_seconds: 3600,
      name: user_name,
      policy: "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Sid\":\"Stmt1\",\"Effect\":\"Allow\",\"Action\":\"s3:ListBucket\",\"Resource\":\"arn:aws:s3:::#{bucket_name}\"}]}"
    })
  rescue StandardError => e
    puts "Error while getting federation token: #{e.message}"
  end

  # Create an Amazon S3 resource with temporary credentials.
  s3 = Aws::S3::Resource.new(region: region, credentials: creds)

  begin
    puts "Accessing the contents of the bucket named '#{bucket_name}'..."
    objects = s3.bucket(bucket_name).objects
    if objects.count.positive?
      puts "Contents of the bucket named '#{bucket_name}' (first 50 objects):"
      puts 'Name => GUID'
      objects.limit(50).each do |obj|
        puts "#{obj.key} => #{obj.etag}"
      end
    else
      puts "No objects in the bucket named '#{bucket_name}'."
    end
    outcome = true
  rescue StandardError => e
    puts "Error while accessing the bucket named '#{bucket_name}': #{e.message}"
  end
  puts outcome
  return outcome
end

# Checks to see whether the specified user exists in IAM; otherwise,
# creates the user.
# @param region [String] The AWS Region to use for configuring the IAM client.
# @param user_name [String] The user's name.
# @return [Boolean] true if the user exists or is created; otherwise, false.
# @example
#   get_user('us-east-1', 'my-user')
def get_user(region, user_name)
  outcome = false
  iam = Aws::IAM::Client.new(region: region)

  # Check to see if the user exists.
  begin
    puts "Checking for a user with the name '#{user_name}'..."
    iam.get_user(user_name: user_name)
    puts "A user with the name '#{user_name}' already exists."
    outcome = true
  # If the user doesn't exit, create them.
  rescue Aws::IAM::Errors::NoSuchEntity
    puts "A user with the name '#{user_name}' doesn't exist. Creating this user..."
    iam.create_user(user_name: user_name)
    iam.wait_until(:user_exists, user_name: user_name)
    puts "Created user with the name '#{user_name}'."
    outcome = true
  rescue StandardError => e
    puts "Error while accessing or creating the user named '#{user_name}': #{e.message}"
  end
  return outcome
end
# snippet-end:[s3.ruby.auth_federation_token_request_test.rb]
