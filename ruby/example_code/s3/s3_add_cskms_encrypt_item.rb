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
key = '0a1b2c3d-1234-5678-z9y8-abcdef123456'

# Get file content as string
file = File.open(item, "rb")
contents = file.read
file.close

kms = Aws::KMS::Client.new(region: region)

resp = kms.encrypt({
  key_id: key,
  plaintext: contents,
})

# Add blob as hex string
blob = resp.ciphertext_blob

client = Aws::S3::Client.new(region: region)

resp = client.put_object({
  body: blob,
  bucket: bucket,
  key: item,
})

puts 'Added encrypted item ' + item + ' to bucket ' + bucket
