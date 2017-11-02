# Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

pass_phrase = 'mary had a little lamb'
key = OpenSSL::PKey::RSA.new 2048

# public key saved as public_key.pem
open 'public_key.pem', 'w' do |io| io.write key.public_key.to_pem end

cipher = OpenSSL::Cipher.new 'AES-128-CBC'

key_secure = key.export cipher, pass_phrase

# private key, protected by pass phrase, saved as private_secure_key.pem
open 'private_secure_key.pem', 'w' do |io|
  io.write key_secure
end
