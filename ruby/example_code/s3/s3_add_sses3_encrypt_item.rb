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

require 'aws-sdk-s3' # In v2: require 'aws-sdk'

# Get the key from the command line
if empty?(ARGV)
  puts 'You must supply a key'
  exit 1
end

key = ARGV[0]

bucket = 'my_bucket'
item = 'my_item'

# Get file contents as a string
contents = File.read(item)

# Create S3 client
client = Aws::S3::Client.new(region: 'us-west-2')

# Encrypt item with KMS on server
client.put_object(
  body: contents,
  bucket: bucket,
  key: item,
  server_side_encryption: 'aws:kms',
  ssekms_key_id: key
)

puts 'Added item ' + name + ' to bucket ' + bucket
