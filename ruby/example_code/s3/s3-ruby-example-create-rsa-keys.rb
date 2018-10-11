#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Ruby]
#snippet-keyword:[Code Sample]
#snippet-service:[Amazon S3]
#snippet-sourcetype:[<<snippet or full-example>>]
#snippet-sourcedate:[]
#snippet-sourceauthor:[AWS]
# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

require 'aws-sdk-core'
require 'openssl'

public_key = 'public_key.pem'
private_key = 'private_key.pem'
pass_phrase = 'Mary had a little lamb'

key = OpenSSL::PKey::RSA.new(1024)

# public key
open public_key, 'w' do |io| io.write key.public_key.to_pem end

cipher = OpenSSL::Cipher.new 'AES-128-CBC'

key_secure = key.export cipher, pass_phrase

# private key protected by pass phrase
open private_key, 'w' do |io|
  io.write key_secure
end

puts 'Saved public key to  ' + public_key
puts 'Saved private key to ' + private_key
