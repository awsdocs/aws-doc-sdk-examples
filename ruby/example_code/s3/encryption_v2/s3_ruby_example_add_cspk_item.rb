# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# s3-ruby-example-add-cspk-item.rb demonstrates how to upload an object to an Amazon Simple Storage Service
# (Amazon S3) bucket. The object's contents are encrypted with an RSA public key.

# snippet-start:[s3.ruby.s3-ruby-example-add-cspk-item]

require "aws-sdk-s3"
require "openssl"

# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param s3_encryption_client [Aws::S3::EncryptionV2::Client] An initialized
#   Amazon S3 encryption client.
# @param bucket_name [String] The bucket's name.
# @param object_key [String] The name of the object.
# @param object_content [String] The content to add to the object.
# @return [Boolean] true if the object was uploaded; otherwise, false.
# @example
#   exit 1 unless object_uploaded_with_public_key_encryption?(
#     Aws::S3::EncryptionV2::Client.new(
#       encryption_key: OpenSSL::PKey::RSA.new(File.read('my-public-key.pem')),
#       key_wrap_schema: :rsa_oaep_sha1,
#       content_encryption_schema: :aes_gcm_no_padding,
#       security_profile: :v2,
#       region: 'us-west-2'
#     ),
#     'doc-example-bucket',
#     'my-file.txt',
#     'This is the content of my-file.txt.'
#   )
def object_uploaded_with_public_key_encryption?(
  s3_encryption_client,
  bucket_name,
  object_key,
  object_content
)
  s3_encryption_client.put_object(
    bucket: bucket_name,
    key: object_key,
    body: object_content
  )
  return true
rescue Aws::Errors::ServiceError => e
  puts "Error uploading object: #{e.message}"
  return false
end

# Full example call:
# Prerequisites: an RSA key pair.

# Replace us-west-2 with the AWS Region you're using for Amazon S3.
def run_me
  bucket_name = "doc-example-bucket"
  object_key = "my-file.txt"
  object_content = "This is the content of my-file.txt."
  region = "us-west-2"
  public_key_file = "my-public-key.pem"
  public_key = OpenSSL::PKey::RSA.new(File.read(public_key_file))

  # When initializing this Amazon S3 encryption client, note:
  # - For key_wrap_schema, use rsa_oaep_sha1 for asymmetric keys.
  # - For security_profile, for reading or decrypting objects encrypted
  #     by the v1 encryption client, use :v2_and_legacy instead.
  s3_encryption_client = Aws::S3::EncryptionV2::Client.new(
    encryption_key: public_key,
    key_wrap_schema: :rsa_oaep_sha1,
    content_encryption_schema: :aes_gcm_no_padding,
    security_profile: :v2,
    region: region
  )

  if object_uploaded_with_public_key_encryption?(
    s3_encryption_client,
    bucket_name,
    object_key,
    object_content
  )
    puts "Object uploaded."
  else
    puts "Object not uploaded."
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[s3.ruby.s3-ruby-example-add-cspk-item]
