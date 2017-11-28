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

require 'aws-sdk-s3'

bucket = 'my_bucket'
item = 'my_item'
key_id = 'd8daec4f-63cf-4d3d-bb64-20ad15929cf6'

# Create KMS client
kms = Aws::KMS::Client.new

# Create S3 encryption client
client = Aws::S3::Encryption::Client.new(
  kms_key_id: key_id,
  kms_client: kms,
)

# Get item
resp = client.get_object(bucket: bucket, key: item)

puts resp.body.read
