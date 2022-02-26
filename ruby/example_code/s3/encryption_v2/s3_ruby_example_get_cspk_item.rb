# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to download an object from an Amazon Simple Storage Service (Amazon S3) bucket.
# The object's contents were originally encrypted with an RSA public key.

# snippet-start:[s3.ruby.s3-ruby-example-get-cspk-item]

require "aws-sdk-s3"
require "openssl"


#
# Prerequisites:
#
# - An Amazon S3 bucket.
# - An object in this bucket.
#
# @param s3_encryption_client [Aws::S3::EncryptionV2::Client] An initialized
#   Amazon S3 encryption client.
# @param bucket_name [String] The bucket's name.
# @param object_key [String] The name of the object.
# @return [String] The object's content; otherwise, information about the
#   failed download operation.
# @example
#   puts download_object_with_private_key_encryption(
#     Aws::S3::EncryptionV2::Client.new(
#       encryption_key: OpenSSL::PKey::RSA.new(File.read('my-private-key.pem')),
#       key_wrap_schema: :rsa_oaep_sha1,
#       content_encryption_schema: :aes_gcm_no_padding,
#       security_profile: :v2,
#       region: 'us-west-2'
#     ),
#     'doc-example-bucket',
#     'my-file.txt'
#   )
def download_object_with_private_key_encryption(
  s3_encryption_client,
  bucket_name,
  object_key
)
  response = s3_encryption_client.get_object(
    bucket: bucket_name,
    key: object_key
  )
  return response.body.read
rescue Aws::Errors::ServiceError => e
  puts "Error downloading object: #{e.message}"
end

# Full example call:
# Prerequisites: the same RSA key pair you originally used to encrypt the object.
# Replace us-west-2 with the AWS Region you're using for Amazon S3.
def run_me
  bucket_name = "doc-example-bucket"
  object_key = "my-file.txt"
  region = "us-west-2"
  private_key_file = "my-private-key.pem"
  private_key = OpenSSL::PKey::RSA.new(File.read(private_key_file))

  # When initializing this Amazon S3 encryption client, note:
  # - For key_wrap_schema, use rsa_oaep_sha1 for asymmetric keys.
  # - For security_profile, for reading or decrypting objects encrypted
  #     by the v1 encryption client, use :v2_and_legacy instead.
  s3_encryption_client = Aws::S3::EncryptionV2::Client.new(
    encryption_key: private_key,
    key_wrap_schema: :rsa_oaep_sha1,
    content_encryption_schema: :aes_gcm_no_padding,
    security_profile: :v2,
    region: region
  )
  puts "The content of '#{object_key}' in bucket '#{bucket_name}' is:"
  puts download_object_with_private_key_encryption(
    s3_encryption_client,
    bucket_name,
    object_key
  )
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[s3.ruby.s3-ruby-example-get-cspk-item]
