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
require 'digest/md5'

# Require key as command-line argument
if ARGV.empty?()
  puts 'You must supply the key'
  exit 1
end

key = ARGV[0] # KMS key is a string
md5 = Digest::MD5.digest(key)

bucket = 'my_bucket'
item = 'my_item'

# Get file contents as a string
contents = File.read(item)

# Create S3 client
client = Aws::S3::Client.new(region: 'us-west-2')

# Encrypt item with user-supplied KMS key on server
client.put_object(
  body: contents,
  bucket: bucket,
  key: item,
  sse_customer_algorithm: 'aws:kms',
  sse_customer_key: key,
  sse_customer_key_md5: md5
)

puts 'Added item ' + item + ' to bucket ' + bucket
