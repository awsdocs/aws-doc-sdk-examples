# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to upload an encrypted object to an
# Amazon Simple Storage Service (Amazon S3) bucket.

# snippet-start:[s3.ruby.s3_add_csaes_encrypt_item]

require "aws-sdk-s3"
require "openssl"


# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param s3_encryption_client [Aws::S3::EncryptionV2::Client]
#   An initialized Amazon S3 V2 encryption client.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name of the object to upload.
# @param object_content [String] The content of the object to upload.
# @return [Boolean] true if the object was encrypted and uploaded;
#   otherwise, false.
# @example
#   s3_encryption_client = Aws::S3::EncryptionV2::Client.new(
#     region: 'us-west-2',
#     encryption_key: get_random_aes_256_gcm_key, # See later in this file.
#     key_wrap_schema: :aes_gcm,
#     content_encryption_schema: :aes_gcm_no_padding,
#     security_profile: :v2
#   )
#   if encrypted_object_uploaded?(
#     s3_encryption_client,
#     'doc-example-bucket',
#     'my-file.txt',
#     'This is the content of my-file.txt.'
#   )
#     puts 'Uploaded.'
#   else
#     puts 'Not uploaded.'
#   end
def encrypted_object_uploaded?(
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

# Generates a random AES256-GCM key. Call this function if you do not
#   already have an AES256-GCM key that you want to use to encrypt the
#   object.
#
# @ return [String] The generated AES256-GCM key. You must keep a record of
#   the key string that is reported. You will not be able to later decrypt the
#   contents of any object that is encrypted with this key unless you
#   have this key.
# @ example
#     get_random_aes_256_gcm_key
def get_random_aes_256_gcm_key
  cipher = OpenSSL::Cipher.new("aes-256-gcm")
  cipher.encrypt
  random_key = cipher.random_key
  random_key_64_string = [random_key].pack("m")
  random_key_64 = random_key_64_string.unpack("m")[0]
  puts "The base 64-encoded string representation of the randomly-" \
    "generated AES256-GCM key is:"
  puts random_key_64_string
  puts "Keep a record of this key string. You will not be able to later " \
    "decrypt the contents of any object that is encrypted with this key " \
    "unless you have this key."
  return random_key_64
end

# Full example call:
# Replace us-west-2 with the AWS Region you're using for Amazon S3.
def run_me
  bucket_name = "doc-example-bucket"
  object_key = "my-file.txt"
  region = "us-west-2"
  object_content = File.read(object_key)

  # The following call generates a random AES256-GCM key. Alternatively, you can
  # provide a base64-encoded string representation of an existing key that
  # you want to use to encrypt the object. For example:#
  # encryption_key_string = 'XSiKrmzhtDKR9tTwJRSLjgwLhiMA82TC2z3GEXAMPLE='
  # encryption_key = encryption_key_string.unpack('m')[0]
  encryption_key = get_random_aes_256_gcm_key

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

  if encrypted_object_uploaded?(
    s3_encryption_client,
    bucket_name,
    object_key,
    object_content
  )
    puts "Uploaded."
  else
    puts "Not uploaded."
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[s3.ruby.s3_add_csaes_encrypt_item]
