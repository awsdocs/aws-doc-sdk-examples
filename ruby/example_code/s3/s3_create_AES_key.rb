# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'openssl'

# Generates a random AES256-GCM key. Call this function if you do not
#   already have an AES256-GCM key that you want to use to encrypt an
#   object.
#
# @return [String] The generated AES256-GCM key. You must keep a record of
#   the key that is reported. You will not be able to later decrypt the
#   contents of any object that is encrypted with this key unless you
#   have this key.
# @example
#     get_random_aes_256_gcm_key
def get_random_aes_256_gcm_key
  cipher = OpenSSL::Cipher.new('aes-256-gcm')
  cipher.encrypt
  random_key = cipher.random_key
  random_key_64_string = [random_key].pack('m')
  random_key_64 = random_key_64_string.unpack('m')[0]
  puts 'The base64-encoded, ASCII-based string representation of the ' \
    'randomly-generated AES256-GCM key is:'
  puts random_key_64_string
  puts 'Keep a record of this key. You will not be able to later ' \
    'decrypt the contents of any object that is encrypted with this key ' \
    'unless you have this key.'
  random_key_64
end

def run_me
  puts 'Generating a random AES256-GCM key...'
  encryption_key = get_random_aes_256_gcm_key
  puts 'The base64-decoded representation of this ASCII-based key string is:'
  puts encryption_key
end

run_me if $PROGRAM_NAME == __FILE__
