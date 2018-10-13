#snippet-sourceauthor: [Doug-AWS]

#snippet-sourcedescription:[Description]

#snippet-service:[AWSService]

#snippet-sourcetype:[full example]

#snippet-sourcedate:[N/A]

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

region = 'us-west-2'
bucket = 'my_bucket'
item = 'my_item'
key_file = 'my_kms_key'

# Get file contents as string
contents = File.read(item)

# Get the KMS key from the file
key = File.read(key_file)

# Create S3 client
client = Aws::S3::Client.new(region: region)

# Encrypt item with KMS on server
client.put_object(
  body: contents,
  bucket: bucket,
  key: name,
  server_side_encryption: 'aws:kms',
  ssekms_key_id: key
)

puts 'Added item ' + item + ' to bucket ' + bucket
puts 'with KMS key from ' + key_file
