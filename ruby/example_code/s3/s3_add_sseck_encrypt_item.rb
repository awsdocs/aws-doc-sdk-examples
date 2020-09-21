# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'
require 'digest/md5'

# Adds an encrypted object to an Amazon S3 bucket. The encryption is performed
#   on the server by using the specified encryption key.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
# - An AES256-GCM key.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name to assign to the uploaded object.
# @param content_to_encrypt [String] The content to be encrypted.
# @param encryption_key [String] The decoded representation of the
#   base64-encoded encryption key string to be used for encryption.
# @return [Boolean] true if the encrypted object was successfully uploaded;
#   otherwise, false.
# @example
#   exit 1 unless customer_key_sse_encrypted_object_uploaded?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'my-file.txt',
#     'This is the content of my-file.txt.',
#     get_random_aes_256_gcm_key # See later in this file.
#   )
def customer_key_sse_encrypted_object_uploaded?(
  s3_client,
  bucket_name,
  object_key,
  content_to_encrypt,
  encryption_key
)
  s3_client.put_object(
    bucket: bucket_name,
    key: object_key,
    body: content_to_encrypt,
    sse_customer_algorithm: 'AES256',
    sse_customer_key: encryption_key,
    sse_customer_key_md5: Digest::MD5.digest(encryption_key)
  )
  return true
rescue StandardError => e
  puts "Error uploading encrypted object: #{e.message}"
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
  cipher = OpenSSL::Cipher.new('aes-256-gcm')
  cipher.encrypt
  random_key = cipher.random_key
  random_key_64_string = [random_key].pack('m')
  random_key_64 = random_key_64_string.unpack('m')[0]
  puts 'The base 64-encoded string representation of the randomly-' \
    'generated AES256-GCM key is:'
  puts random_key_64_string
  puts 'Keep a record of this key string. You will not be able to later ' \
    'decrypt the contents of any object that is encrypted with this key ' \
    'unless you have this key.'
  return random_key_64
end

def run_me
  bucket_name = 'doc-example-bucket'
  object_key = 'my-file.txt'
  content_to_encrypt = 'This is the content of my-file.txt.'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  # The following call generates a random AES256-GCM key. Alternatively, you can
  # provide a base64-encoded string representation of an existing key that
  # you want to use to encrypt the object. For example:#
  # encryption_key_string = 'XSiKrmzhtDKR9tTwJRSLjgwLhiMA82TC2z3GEXAMPLE='
  # encryption_key = encryption_key_string.unpack('m')[0]
  encryption_key = get_random_aes_256_gcm_key

  if customer_key_sse_encrypted_object_uploaded?(
    s3_client,
    bucket_name,
    object_key,
    content_to_encrypt,
    encryption_key
  )
    puts 'Encrypted object uploaded.'
  else
    puts 'Encrypted object not uploaded.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
