# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# This code example demonstrates how to:
# - Create a bucket in Amazon Simple Storage Service (Amazon S3).
# - Add a bucket policy to the bucket.
# - Change the bucket policy.
# - Remove the bucket policy from the bucket.
# - Delete the bucket.

require 'aws-sdk-s3'
require 'securerandom'

# Creates an Amazon Simple Storage Service (Amazon S3) bucket.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param bucket_name [String] The bucket's name.
# @return [Boolean] true if the bucket was created; otherwise, false.
# @example
#   exit 1 unless bucket_created?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket'
#   )
def bucket_created?(s3_client, bucket_name)
  s3_client.create_bucket(bucket: bucket_name)
  return true
rescue StandardError => e
  puts "Error creating bucket: #{e.message}"
  return false
end

# Adds a bucket policy to an Amazon Simple Storage Service (Amazon S3) bucket.
#
# Prerequisites:
#
# - An S3 bucket.
# - A valid AWS principal Amazon Resource Name (ARN).
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param bucket_name [String] The bucket's name.
# @param aws_principal [String] The ARN of the AWS principal to allow.
# @param action [String] The bucket action to allow.
# @return [Boolean] true if the bucket policy was added; otherwise, false.
# @example
#   exit 1 unless bucket_policy_added?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'arn:aws:iam::111111111111:user/SomeUser',
#     's3:GetObject'
#   )
def bucket_policy_added?(s3_client, bucket_name, aws_principal, action)
  bucket_policy = {
    'Version' => '2012-10-17',
    'Statement' => [
      {
        'Effect' => 'Allow',
        'Principal' => {
          'AWS' => aws_principal
        },
        'Action' => action,
        'Resource' => "arn:aws:s3:::#{bucket_name}/*"
      }
    ]
  }.to_json
  s3_client.put_bucket_policy(
    bucket: bucket_name,
    policy: bucket_policy
  )
  return true
rescue StandardError => e
  puts "Error adding bucket policy: #{e.message}"
  return false
end

# Updates the AWS principal Amazon Resource Name (ARN) in an existing
#   bucket policy for an Amazon Simple Storage Service (Amazon S3) bucket.
#
# Prerequisites:
#
# - An S3 bucket.
# - A bucket policy attached to the bucket.
# - A valid AWS principal ARN.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param bucket_name [String] The bucket's name.
# @param aws_principal [String] The ARN of the new AWS principal to allow.
# @return [Boolean] true if the bucket policy was updated; otherwise, false.
# @example
#   exit 1 unless bucket_policy_aws_principal_updated?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'arn:aws:iam::111111111111:user/SomeOtherUser'
#   )
def bucket_policy_aws_principal_updated?(
  s3_client,
  bucket_name,
  new_aws_principal
)
  bucket_policy = s3_client.get_bucket_policy(bucket: bucket_name).policy.read
  policy_json = JSON.parse(bucket_policy)

  policy_json['Statement'][0]['Principal']['AWS'] = new_aws_principal

  s3_client.put_bucket_policy(
    bucket: bucket_name,
    policy: policy_json.to_json
  )
  return true
rescue StandardError => e
  puts "Error updating bucket policy: #{e.message}"
  return false
end

# Deletes a bucket policy for an Amazon Simple Storage Service
#   (Amazon S3) bucket.
#
# Prerequisites:
#
# - An S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param bucket_name [String] The bucket's name.
# @return [Boolean] true if the bucket policy was deleted; otherwise, false.
# @example
#   exit 1 unless bucket_policy_deleted?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket'
#   )
def bucket_policy_deleted?(s3_client, bucket_name)
  s3_client.delete_bucket_policy(bucket: bucket_name)
  return true
rescue StandardError => e
  puts "Error deleting bucket policy: #{e.message}"
  return false
end

# Deletes an Amazon Simple Storage Service (Amazon S3) bucket.
#
# Prerequisites:
#
# - An S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param bucket_name [String] The bucket's name.
# @return [Boolean] true if the bucket was deleted; otherwise, false.
# @example
#   exit 1 unless bucket_deleted?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket'
#   )
def bucket_deleted?(s3_client, bucket_name)
  s3_client.delete_bucket(bucket: bucket_name)
  return true
rescue StandardError => e
  puts "Error deleting bucket: #{e.message}"
  return false
end

# Full example call:
def run_me
  aws_principal = 'arn:aws:iam::111111111111:user/SomeUser'
  new_aws_principal = 'arn:aws:iam::111111111111:user/SomeOtherUser'
  action = 's3:GetObject'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)
  bucket_name = 'bucket-' + SecureRandom.uuid

  if bucket_created?(s3_client, bucket_name)
    puts "Bucket '#{bucket_name}' created."
  else
    puts "Bucket '#{bucket_name}' not created. Stopping program."
    exit 1
  end

  if bucket_policy_added?(s3_client, bucket_name, aws_principal, action)
    puts 'Bucket policy added.'
  else 
    puts 'Bucket policy not added.'
  end

  if bucket_policy_aws_principal_updated?(
    s3_client,
    bucket_name,
    new_aws_principal
  )
    puts 'Bucket policy updated with new AWS principal.'
  else
    puts 'Bucket policy not updated with new AWS principal.'
  end

  if bucket_policy_deleted?(s3_client, bucket_name)
    puts 'Bucket policy (if any) deleted.'
  else
    puts 'Bucket policy (if any) not deleted.'
  end

  if bucket_deleted?(s3_client, bucket_name)
    puts "Bucket '#{bucket_name}' deleted."
  else
    puts "Bucket '#{bucket_name}' not deleted. " \
      'You must delete this bucket yourself.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
