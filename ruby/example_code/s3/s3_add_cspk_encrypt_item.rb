# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# This code example demonstrates how to use an RSA public key to
# encrypt content and then upload that encrypted content
# to an Amazon S3 bucket.

require 'aws-sdk-s3'
require 'openssl'

# Generates a random set of corresponding 2048-bit RSA public and
#   private key pair strings. Call this function if you do not
#   already have a public/private key pair that you want to use to
#   encrypt the object content.
#
# @return [Hash] The generated public and private key pair strings.
#   You must keep a record of the strings that are reported. You will
#   not be able to properly encrypt and decrypt the contents of
#   objects without these keys.
# @example
#     create_public_private_rsa_key_pair_strings
def create_public_private_rsa_key_pair_strings
  private_key = OpenSSL::PKey::RSA.new(2048)
  public_key = private_key.public_key
  private_public_key_strings_pair = {
    'private_key_string' => private_key,
    'public_key_string' => public_key
  }
  puts 'The randomly-generated 2048-bit RSA private key string is:'
  puts private_public_key_strings_pair['private_key_string']
  puts 'The corresponding 2048-bit public key string is:'
  puts private_public_key_strings_pair['public_key_string']
  puts 'Keep a record of these key strings. You will not be able to properly ' \
  'encrypt and decrypt the contents of objects without these keys.'
  return private_public_key_strings_pair
end

# Saves a set of corresponding public/private key pair strings to
#   files on disk. Call this function if you have a set of corresponding
#   key pair strings in memory but you have not yet save them to disk.
#
# @param public_key_string [String] The public key string to save.
# @param public_key_file_name [String] The name of the file to save the
#    public key to.
# @param private_key_string [String] The private key string to save.
# @param private_key_file_name [String] The name of the file to save the
#    private key to.
# @return [Boolean] true if the files were saved; otherwise, false.
# @example
#     exit 1 unless public_private_rsa_key_pair_files_created?(
#       '***my public key string***',
#       'my-public-key.pem',
#       '***my private key string***',
#       'my-private-key.pem'
#     )
def public_private_rsa_key_pair_files_created?(
  public_key_string,
  public_key_file_name,
  private_key_string,
  private_key_file_name
)
  public_key_file = File.new(public_key_file_name, 'w')
  public_key_file.write(public_key_string)
  public_key_file.close
  private_key_file = File.new(private_key_file_name, 'w')
  private_key_file.write(private_key_string)
  private_key_file.close
  return true
rescue StandardError => e
  puts "Could not create public/private key pair files: #{e.message}"
  return false
end

# Uses an RSA public key file to encrypt the specified content, and
#   returns a base64-encoded representation of the encrypted content.

# Prerequisites:
#
# - A PEM-encoded RSA public key file.
#
# @param public_key_file [String] The name of the public key file.
# @param content [String] The content to encrypt.
# @return [String] A base 64-encoded representation of the encrypted content.
# @example
#     puts encrypt_and_return_object_content('my-public-key.pem', 'Hello!')
def encrypt_and_return_object_content(public_key_file, content)
  public_key = OpenSSL::PKey::RSA.new(File.read(public_key_file))
  Base64.encode64(public_key.public_encrypt(content))
end

# Uploads an object with encrypted content to an Amazon S3 bucket.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name of the object to upload.
# @param encrypted_object_content [String] The encrypted content of 
#   the object to upload.
# @return [Boolean] true if the object was uploaded; otherwise, false.
# @example
#   s3_client = Aws::S3::Client.new(region: 'us-east-1')
#   if encrypted_object_uploaded?(
#     s3_client,
#     'my-bucket',
#     'my-file.txt',
#     'This is the content of my-file.txt.'
#   )
#     puts 'Uploaded.'
#   else
#     puts 'Not uploaded.'
#   end
def encrypted_object_uploaded?(
  s3_client,
  bucket_name,
  object_key,
  encrypted_object_content
)
  s3_client.put_object(
    bucket: bucket_name,
    key: object_key,
    body: encrypted_object_content
  )
  return true
rescue StandardError => e
  puts "Error uploading object: #{e.message}"
  return false
end

# Full example call:
def run_me
  key_strings = create_public_private_rsa_key_pair_strings
  public_key_string = key_strings['public_key_string']
  public_key_file = 'my-public-key.pem'
  private_key_string = key_strings['private_key_string']
  private_key_file = 'my-private-key.pem'
  content_to_encrypt = 'Hello, World!'
  bucket_name = 'my-bucket'
  object_key = 'my-file.txt'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  unless public_private_rsa_key_pair_files_created?(
    public_key_string,
    public_key_file,
    private_key_string,
    private_key_file
  )
    puts 'Program will stop.'
    exit 1
  end

  encrypted_content = encrypt_and_return_object_content(
    public_key_file,
    content_to_encrypt
  )
  puts "Encrypted representation of '#{content_to_encrypt}' is:"
  puts encrypted_content

  if encrypted_object_uploaded?(
    s3_client,
    bucket_name,
    object_key,
    encrypted_content
  )
    puts 'Uploaded.'
  else
    puts 'Not uploaded.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
