# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Creates public and private keys.]
# snippet-keyword:[RSA]
# snippet-keyword:[Ruby]
# snippet-service:[s3]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

require 'openssl'

# Require a pass phrase as command-line argument
if ARGV.empty?()
  puts 'You must supply a pass phrase'
  exit 1
end

pass_phrase = ARGV[0]
key = OpenSSL::PKey::RSA.new 2048

# Files to store public and private keys
public_key_file = 'public_key.pem'
private_key_file = 'private_secure_key.pem'

open public_key_file, 'w' do |io|
  io.write key.public_key.to_pem
end

cipher = OpenSSL::Cipher.new 'AES-128-CBC'
key_secure = key.export cipher, pass_phrase

open private_key_file, 'w' do |io|
  io.write key_secure
end

puts 'The public key is in '  + public_key_file
puts 'The private key is in ' + private_key_file + ' using the pass phrase:'
puts '"' + pass_phrase + '"'
