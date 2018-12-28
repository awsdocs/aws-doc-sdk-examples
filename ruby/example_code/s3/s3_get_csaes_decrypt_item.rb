#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Gets and decrypts an S3 bucket item using a key from a file.]
#snippet-keyword:[Amazon Simple Storage Service]
#snippet-keyword:[get_object method]
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

require 'aws-sdk-s3'  # In v2: require 'aws-sdk'

# Require key as command-line argument
if ARGV.empty?()
  puts 'You must supply the key as a string'
  exit 1
end

encoded_string = ARGV[0]
key = encoded_string.unpack("m*")[0]

bucket = 'my_bucket'
item = 'my_item'

# Create S3 encryption client
client = Aws::S3::Encryption::Client.new(region: 'us-west-2', encryption_key: key)

# Get item and print it out
resp = client.get_object(bucket: bucket, key: item)
puts resp.body.read
