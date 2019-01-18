# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Specifies default KMS encryption on an S3 bucket.]
# snippet-keyword:[Amazon Simple Storage Service]
# snippet-keyword:[put_bucket_encryption method]
# snippet-keyword:[Ruby]
# snippet-service:[s3]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-03-16]
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

# Get the key from the command line
if ARGV.empty?()
  puts 'You must supply a key'
  exit 1
end

key = ARGV[0]

# Create S3 client
client = Aws::S3::Client.new(region: 'us-west-2')

# Set default encryption on bucket
client.put_bucket_encryption(
  bucket: 'my_bucket',
  server_side_encryption_configuration: {
    rules: [{
      apply_server_side_encryption_by_default: {
        sse_algorithm: 'aws:kms',
        kms_master_key_id: key
      }
    }]
  }
)
