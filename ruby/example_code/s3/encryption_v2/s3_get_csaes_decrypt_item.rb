# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to get the contents of an encrypted object
# in an Amazon Simple Storage Service (Amazon S3) bucket.

# snippet-start:[s3.s3_get_csaes_decrypt_item.rb]

require "aws-sdk-s3"

# Prerequisites:
#
# - An Amazon S3 bucket.
# - An encrypted object in the bucket to get.
#
# @param s3_encryption_client [Aws::S3::EncryptionV2::Client]
#   An initialized Amazon S3 V2 encryption client.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name of the encrypted object to get.
# @return [String] If successful, the object's content; otherwise,
#   diagnostic information about the unsuccessful attempt.
# @example
#   encryption_key_string = 'XSiKrmzhtDKR9tTwJRSLjgwLhiMA82TC2z3GEXAMPLE='
#   encryption_key = encryption_key_string.unpack('m')[0]
#   s3_encryption_client = Aws::S3::EncryptionV2::Client.new(
#     region: 'us-west-2',
#     encryption_key: encryption_key,
#     key_wrap_schema: :aes_gcm,
#     content_encryption_schema: :aes_gcm_no_padding,
#     security_profile: :v2
#   )
#   puts get_decrypted_object_content(
#     s3_encryption_client,
#     'doc-example-bucket',
#     'my-file.txt'
#   )
def get_decrypted_object_content(
  s3_encryption_client,
  bucket_name,
  object_key
)
  response = s3_encryption_client.get_object(
    bucket: bucket_name,
    key: object_key)
  if defined?(response.body)
    return response.body.read
  else
    return "Error: Object content empty or unavailable."
  end
rescue Aws::Errors::ServiceError => e
  return "Error getting object content: #{e.message}"
end

# Full example call:
# Replace us-west-2 with the AWS Region you're using for Amazon S3.
def run_me
  bucket_name = "doc-example-bucket"
  object_key = "my-file.txt"
  region = "us-west-2"

  # Provide a base64-encoded string representation of the key that
  # was originally used to encrypt the object. For example:
  encryption_key_string = "XSiKrmzhtDKR9tTwJRSLjgwLhiMA82TC2z3GEXAMPLE="
  encryption_key = encryption_key_string.unpack("m")[0]

  # Note that in the following call:
  # - key_wrap_schema must be aes_gcm for symmetric keys.
  # - To allow reading and decrypting objects that are encrypted by the
  #   Amazon S3 V1 encryption client instead, use :v2_and_legacy instead of :v2.
  s3_encryption_client = Aws::S3::EncryptionV2::Client.new(
    region: region,
    encryption_key: encryption_key,
    key_wrap_schema: :aes_gcm,
    content_encryption_schema: :aes_gcm_no_padding,
    security_profile: :v2
  )

  puts get_decrypted_object_content(
    s3_encryption_client,
    bucket_name,
    object_key
  )
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[s3.s3_get_csaes_decrypt_item.rb]
