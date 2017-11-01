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

require 'aws-sdk-s3'  # In v2: require 'aws-sdk'

require 'openssl'

region = 'us-west-2'
bucket_name = 'my_bucket'
item = 'my_item'
key_id = '0a1b2c3d-1234-5678-z9y8-abcdef123456'

client = Aws::S3::Client.new(region: region)

# Create 256-bit AES cipher
cipher = OpenSSL::Cipher::AES256.new :CBC
cipher.encrypt
# Get random initialization vector
iv = cipher.random_iv
iv_string = iv.unpack('H*')
puts "Initialization vector: #{iv_string[0]}"

# Create key
cipher.key = key_id

# Get file content as string
file = File.open(item, "rb")
contents = file.read
file.close

# Encode it
cipher_text = cipher.update(contents) + cipher.final

resp = client.put_object({
  body: cipher_text,
  bucket: bucket_name,
  key: name,
})

puts 'Added encrypted item to bucket'
