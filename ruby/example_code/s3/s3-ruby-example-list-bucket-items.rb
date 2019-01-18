# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[???.]
# snippet-keyword:[Amazon Simple Storage Service]
# snippet-keyword:[Resource.bucket method]
# snippet-keyword:[Bucket.objects method]
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

require 'aws-sdk-s3'  # v2: require 'aws-sdk'

s3 = Aws::S3::Resource.new(region: 'us-west-2')

bucket = s3.bucket('my-bucket')

# Show only the first 50 items
bucket.objects.limit(50).each do |item|
  puts "Name:  #{item.key}"
  puts "URL:   #{item.presigned_url(:get)}"
end
