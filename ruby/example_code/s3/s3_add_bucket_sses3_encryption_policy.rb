# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Adds a policy to and S3 bucket that requires server-side KMS encrytion on items uploaded.]
# snippet-keyword:[Amazon Simple Storage Service]
# snippet-keyword:[put_bucket_policy method]
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

bucket = 'my_bucket'

# Require server-side KMS encryption to upload item to bucket
policy = {
  'Version': '2012-10-17',
  'Id': 'PutObjPolicy',
  'Statement': [
    {
      'Sid': 'DenyIncorrectEncryptionHeader',
      'Effect': 'Deny',
      'Principal': '*',
      'Action': 's3:PutObject',
      'Resource': 'arn:aws:s3:::' + bucket + '/*',
      'Condition': {
        'StringNotEquals': {
          's3:x-amz-server-side-encryption': 'aws:kms'
        }
      }
    },
    {
      'Sid': 'DenyUnEncryptedObjectUploads',
      'Effect': 'Deny',
      'Principal': '*',
      'Action': 's3:PutObject',
      'Resource': 'arn:aws:s3:::' + bucket + '/*',
      'Condition': {
        'Null': {
          's3:x-amz-server-side-encryption': 'true'
        }
      }
    }
  ]
}.to_json

# Create S3 client
s3 = Aws::S3::Client.new(region: 'us-west-2')

# Apply bucket policy
s3.put_bucket_policy(
  bucket: bucket,
  policy: policy
)

puts 'Successfully added policy to bucket ' + bucket
