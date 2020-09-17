# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'
require 'openssl'

# Gets the contents of an object in an Amazon S3 bucket. The
#   object's content must have already been encrypted with an RSA public key.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
# - An object in the bucket. This object contains the encrypted content.
# - The RSA private key corresponding to the RSA public key that was
#     originally used to encrypt the object's content.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name of the object to get.
# @param private_key_file [String] The name of the private key file.
# @return [String] If successful, the object's decrypted content; otherwise,
#   diagnostic information about the unsuccessful attempt.
# @example
#   s3_client = Aws::S3::Client.new(region: 'us-east-1')
#   puts get_decrypted_object_content(
#     s3_client,
#     'my-bucket',
#     'my-file.txt',
#     'my-private-key.pem'
#   )
def get_decrypted_object_content(
  s3_client,
  bucket_name,
  object_key,
  private_key_file
)
  response = s3_client.get_object(
    bucket: bucket_name,
    key: object_key)
  if defined?(response.body)
    encrypted_content = response.body.read
    private_key = OpenSSL::PKey::RSA.new(File.read(private_key_file))
    private_key.private_decrypt(Base64.decode64(encrypted_content))
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
  s3_client = Aws::S3::Client.new(region: region)
  private_key_file = 'my-private-key.pem'

  puts get_decrypted_object_content(
    s3_client,
    bucket_name,
    object_key,
    private_key_file
  )
end

run_me if $PROGRAM_NAME == __FILE__
