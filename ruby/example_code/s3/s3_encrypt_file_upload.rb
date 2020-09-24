# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# snippet-start:[s3.ruby.s3_encrypt_file_upload.rb]
require 'aws-sdk-s3'

# Uploads a file to an Amazon S3 bucket and then encrypts the file server-side
#   by using the 256-bit Advanced Encryption Standard (AES-256) block cipher.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name for the uploaded object.
# @param object_content [String] The content to upload into the object.
# @return [Boolean] true if the file was successfully uploaded and then
#   encrypted; otherwise, false.
# @example
#   exit 1 unless upload_file_encrypted_aes256_at_rest?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'my-file.txt',
#     'This is the content of my-file.txt.'
#   )
def upload_file_encrypted_aes256_at_rest?(
  s3_client,
  bucket_name,
  object_key,
  object_content
)
  s3_client.put_object(
    bucket: bucket_name,
    key: object_key,
    body: object_content,
    server_side_encryption: 'AES256'
  )
  return true
rescue StandardError => e
  puts "Error uploading object: #{e.message}"
  return false
end
# snippet-end:[s3.ruby.s3_encrypt_file_upload.rb]

def run_me
  bucket_name = 'doc-example-bucket'
  object_key = 'my-file.txt'
  object_content = 'This is the content of my-file.txt.'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  if upload_file_encrypted_aes256_at_rest?(
    s3_client,
    bucket_name,
    object_key,
    object_content
  )
    puts 'File uploaded and encrypted.'
  else
    puts 'File not uploaded.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
