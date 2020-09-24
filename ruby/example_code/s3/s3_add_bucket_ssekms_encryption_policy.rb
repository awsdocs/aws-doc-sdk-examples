# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Denies uploads of unencrypted objects to an Amazon S3 bucket.
#
# Prerequisites:
#
# - The Amazon S3 bucket to deny uploading unencrypted objects.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The bucket's name.
# @return [Boolean] true if a policy was added to the bucket to
#   deny uploading unencrypted objects; otherwise, false.
# @example
#   if deny_uploads_without_encryption?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket'
#   )
#     puts 'Policy added.'
#   else
#     puts 'Policy not added.'
#   end
def deny_uploads_without_encryption?(s3_client, bucket_name)
  policy = {
    'Version': '2012-10-17',
    'Id': 'PutObjPolicy',
    'Statement': [
      {
        'Sid': 'DenyUnEncryptedObjectUploads',
        'Effect': 'Deny',
        'Principal': '*',
        'Action': 's3:PutObject',
        'Resource': 'arn:aws:s3:::' + bucket_name + '/*',
        'Condition': {
          'StringNotEquals': {
            's3:x-amz-server-side-encryption': [
              'AES256',
              'aws:kms'
            ]
          }
        }
      },
      {
        'Sid': 'DenyUnEncryptedObjectUploads',
        'Effect': 'Deny',
        'Principal': '*',
        'Action': 's3:PutObject',
        'Resource': 'arn:aws:s3:::' + bucket_name + '/*',
        'Condition': {
          'Null': {
            's3:x-amz-server-side-encryption': 'true'
          }
        }
      }
    ]
  }.to_json
  s3_client.put_bucket_policy(
    bucket: bucket_name,
    policy: policy
  )
  return true
rescue StandardError => e
  puts "Error adding policy: #{e.message}"
  return false
end

# Full example call:
def run_me
  if deny_uploads_without_encryption?(
    Aws::S3::Client.new(region: 'us-east-1'),
    'doc-example-bucket'
  )
    puts 'Policy added.'
  else
    puts 'Policy not added.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
