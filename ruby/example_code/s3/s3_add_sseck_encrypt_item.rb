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
key_file = 'my_aes_key_file'
md5_file = 'my_md5_file'

# Get file contents as a string
contents = File.read(item)

# Get key from key file
key = File.binread(key_file) # use File.read for KMS key

# Get md5 value from file
md5 = File.binread(md5_file)

# Create S3 client
client = Aws::S3::Client.new(region: 'us-west-2')

# Encrypt item with user-supplied AES key on server
client.put_object(
  body: contents,
  bucket: bucket,
  key: item,
  sse_customer_algorithm: 'AES256', # use aws:kms for KMS key
  sse_customer_key: key,
  sse_customer_key_md5: md5
)

puts 'Added item ' + item + ' to bucket ' + bucket
puts 'with AES key from ' + key_file
puts 'and MD5 from ' + md5_file
