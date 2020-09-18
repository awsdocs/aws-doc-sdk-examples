# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Adds an encrypted object to an Amazon S3 bucket. The encryption is performed
#   on the server by using the aws/s3 AWS managed customer master key (CMK).
#
# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name to assign to the uploaded object.
# @param content_to_encrypt [String] The content to be encrypted.
# @return [Boolean] true if the encrypted object was successfully uploaded;
#   otherwise, false.
# @example
#   exit 1 unless kms_sse_encrypted_object_uploaded?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'my-bucket',
#     'my-file.txt',
#     'This is the content of my-file.txt.'
#   )
def kms_sse_encrypted_object_uploaded?(
  s3_client,
  bucket_name,
  object_key,
  content_to_encrypt
)
  s3_client.put_object(
    bucket: bucket_name,
    key: object_key,
    body: content_to_encrypt,
    server_side_encryption: 'aws:kms'
  )
  return true
rescue StandardError => e
  puts "Error uploading encrypted object: #{e.message}"
  return false
end

def run_me
  bucket_name = 'my-bucket'
  object_key = 'my-file.txt'
  content_to_encrypt = 'This is the content of my-file.txt.'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  if kms_sse_encrypted_object_uploaded?(
    s3_client,
    bucket_name,
    object_key,
    content_to_encrypt
  )
    puts 'Encrypted object uploaded.'
  else
    puts 'Encrypted object not uploaded.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
