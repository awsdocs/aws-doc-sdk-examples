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

bucket = 'my_bucket'
item = 'my_item'
key_file = 'aes_key.bin'

# Get contents of item
contents = File.read(item)

# Get AES key from related filename
key = File.binread(key_file)

# Create S3 encryption client
client = Aws::S3::Encryption::Client.new(region: 'us-west-2', encryption_key: key)

# Add encrypted item to bucket
client.put_object(
  bucket: bucket,
  key: item,
  body: contents
)

puts 'Added encrypted item ' + item + ' to bucket ' + bucket
