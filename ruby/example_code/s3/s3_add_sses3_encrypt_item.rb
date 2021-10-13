# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to a add an encrypted object to an Amazon Simple Storage Solution (Amazon S3)
# bucket. The encryption is performed on the server by using the AWS managed customer master key (CMK).

# snippet-start:[s3.ruby.s3_add_sses3_encrypt_item]

require 'aws-sdk-s3'

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
#     Aws::S3::Client.new(region: 'us-west-2'),
#     'doc-example-bucket',
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

# Replace us-west-2 with the AWS Region you're using for Amazon S3.
def run_me
  bucket_name = 'doc-example-bucket'
  object_key = 'my-file.txt'
  content_to_encrypt = 'This is the content of my-file.txt.'
  region = 'us-west-2'
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
# snippet-end:[s3.ruby.s3_add_sses3_encrypt_item]
