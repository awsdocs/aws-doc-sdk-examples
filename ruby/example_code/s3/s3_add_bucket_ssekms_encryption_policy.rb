# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[Adds a policy to an S3 bucket that denies un-encrypted uploads.]
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

require 'aws-sdk-iam' # To get user ARN; In v2: require 'aws-sdk'
require 'aws-sdk-s3'

region = 'us-west-2'
bucket = 'my_bucket'

# Get ARN for current user
iam = Aws::IAM::Resource.new(region: region)
user = iam.current_user
arn = user.arn

s3 = Aws::S3::Client.new(region: region)

policy = {
  'Version':'2012-10-17',
  'Id':'PutObjPolicy',
  'Statement':[{
    'Sid':'DenyUnEncryptedObjectUploads',
    'Effect':'Deny',
    'Principal':'*',
    'Action':'s3:PutObject',
    'Resource':'arn:aws:s3:::' + bucket + '/*',
    'Condition':{
      'StringNotEquals':{
        's3:x-amz-server-side-encryption':'aws:kms'
      }
    }
  }]
}.to_json

s3.put_bucket_policy(
  bucket: bucket,
  policy: policy
)

puts 'Successfully added policy to bucket ' + bucket
