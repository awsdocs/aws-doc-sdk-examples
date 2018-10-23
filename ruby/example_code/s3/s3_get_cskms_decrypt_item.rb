#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Decrypts an S3 bucket item using KMS.]
#snippet-keyword:[Amazon Simple Storage Service]
#snippet-keyword:[get_object method]
#snippet-keyword:[Ruby]
#snippet-service:[s3]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-03-16]
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

# Get the key from the command line
if ARGV.empty?()
  puts 'You must supply a key'
  exit 1
end

key = ARGV[0]

bucket = 'my_bucket'
item = 'my_item'

# Create KMS client
kms = Aws::KMS::Client.new

# Create S3 encryption client
client = Aws::S3::Encryption::Client.new(
  kms_key_id: key,
  kms_client: kms,
)

# Get item
resp = client.get_object(bucket: bucket, key: item)

puts resp.body.read
