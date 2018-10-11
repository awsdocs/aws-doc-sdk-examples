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

require 'aws-sdk-s3'  # In v2: require 'aws-sdk'
require 'base64'
require 'openssl'

bucket = 'my_bucket'
item = 'my_item'
pk_file = 'public_key_file.pem'

# Get file content as string
contents = File.read(item)

# Encrypt item using public key
public_key = OpenSSL::PKey::RSA.new(File.read(pk_file))
encrypted_string = Base64.encode64(public_key.public_encrypt(contents))

# Create S3 client
client = Aws::S3::Client.new(region: 'us-west-2')

# Upload encrypted item to bucket
client.put_object(
  body: encrypted_string,
  bucket: bucket,
  key: item
)

puts 'Added encrypted item ' + item + ' to bucket ' + bucket
puts 'using public key from ' + pk_file
