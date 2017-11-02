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
bucket = 'my_bucket'
item = 'my_item'
key = '0a1b2c3d-1234-5678-z9y8-abcdef123456'
iv = 'abcdef123456abcdef123456abcd1234'

# Create S3 client
client = Aws::S3::Client.new(region: region)

# Get item contents as a string
resp = client.get_object(bucket: bucket, key: item)
blob = resp.body.read
blob_string = blob.unpack('H*')
cipher_text = blob_string[0].scan(/../).map { |x| x.hex }.pack('c*')

# Decrypt the item
decipher = OpenSSL::Cipher::AES256.new :CBC
decipher.decrypt

decipher.iv = iv.scan(/../).map { |x| x.hex }.pack('c*')
decipher.key = key
plain_text = decipher.update(cipher_text) + decipher.final

puts plain_text
