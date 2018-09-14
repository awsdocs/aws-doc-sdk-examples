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

require 'aws-sdk-s3'  # In v2: require 'aws-sdk'
require 'base64'
require 'openssl'

# Require a pass phrase as command-line argument
if ARGV.empty?()
  puts 'You must supply a pass phrase'
  exit 1
end

pass_phrase = ARGV[0]

bucket = 'my_bucket'
item = 'my_item'
pk_file = 'private_key_file.pem'

# Create S3 client
client = Aws::S3::Client.new(region: region)

# Get item contents as string
resp = client.get_object(bucket: bucket, key: item)
blob = resp.body.read
blob_string = blob.unpack('H*')
cipher_text = blob_string[0].scan(/../).map { |x| x.hex }.pack('c*')

# Decrypt the string using private key
private_key = OpenSSL::PKey::RSA.new(File.read(pk_file), pass_phrase)
decoded_text = private_key.private_decrypt(Base64.decode64(cipher_text))

puts decoded_text
