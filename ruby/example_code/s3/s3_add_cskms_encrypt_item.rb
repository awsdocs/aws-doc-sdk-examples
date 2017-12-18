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
key_id = 'a1bcde2f-12ab-3c4d-aa99-09ab12345cd6'

# Get file content as string
contents = File.read(item)

# Create KMS client
kms = Aws::KMS::Client.new

# Create encryption client
client = Aws::S3::Encryption::Client.new(
  kms_key_id: key_id,
  kms_client: kms
)

# Add encrypted item to bucket
client.put_object(
  body: contents,
  bucket: bucket,
  key: item
)

puts 'Added client-side KMS encrypted item ' + item + ' to bucket ' + bucket
