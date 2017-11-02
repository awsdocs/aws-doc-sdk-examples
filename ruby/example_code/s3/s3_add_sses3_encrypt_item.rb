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

region = 'us-west-2'
bucket = 'my_bucket'
item = 'my_item'

# Get file IO
file = File.open(item, "rb")

# Get just the filename to use as key
name = File.basename(item)

# Create S3 client
client = Aws::S3::Client.new(region: region)

# Encrypt item with 256-bit AES on server
resp = client.put_object({
  body: contents,
  bucket: bucket,
  key: name,
  server_side_encryption: 'AES256',
})

# Close file
file.close

puts 'Added item ' + name ' to bucket ' + bucket
