#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Adds an item to an S3 bucket and encrypts it with KMS on the server.]
#snippet-keyword:[Amazon Simple Storage Service]
#snippet-keyword:[put_object method]
#snippet-keyword:[Ruby]
#snippet-service:[s3]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-03-16]
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
  server_side_encryption: 'aws:kms'
)

puts 'Added item ' + name + ' to bucket ' + bucket
