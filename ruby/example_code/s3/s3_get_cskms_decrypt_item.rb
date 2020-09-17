# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Gets the contents of an encrypted object in an Amazon S3 bucket.
#
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
#   s3_encryption_client = Aws::S3::EncryptionV2::Client.new(
#     region: 'us-east-1',
#     kms_key_id: '9041e78c-7a20-4db3-929e-828abEXAMPLE',
#     key_wrap_schema: :kms_context,
#     content_encryption_schema: :aes_gcm_no_padding,
#     security_profile: :v2
#   )
#   puts get_decrypted_object_content(
#     s3_encryption_client,
#     'my-bucket',
#     'my-file.txt'
#   )
def get_decrypted_object_content(
  s3_encryption_client,
  bucket_name,
  object_key
)
  response = s3_encryption_client.get_object(
    bucket: bucket_name,
    key: object_key
  )
  if defined?(response.body)
    return response.body.read
  else
    return 'Error: Object content empty or unavailable.'
  end
rescue StandardError => e
  return "Error getting object content: #{e.message}"
end

# Full example call:
def run_me
  bucket_name = 'my-bucket'
  object_key = 'my-file.txt'
  region = 'us-east-1'
  kms_key_id = '9041e78c-7a20-4db3-929e-828abEXAMPLE'

  # Note that in the following call:
  # - key_wrap_schema must be kms_context for AWS KMS.
  # - To allow reading and decrypting objects that are encrypted by the
  #   Amazon S3 V1 encryption client instead, use :v2_and_legacy instead of :v2.
  s3_encryption_client = Aws::S3::EncryptionV2::Client.new(
    region: region,
    kms_key_id: kms_key_id,
    key_wrap_schema: :kms_context,
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
