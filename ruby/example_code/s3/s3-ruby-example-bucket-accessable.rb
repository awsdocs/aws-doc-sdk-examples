# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourceauthor:[Doug-AWS]
# snippet-sourcedescription:[???.]
# snippet-keyword:[Amazon Simple Storage Service]
# snippet-keyword:[get_bucket_location method]
# snippet-keyword:[Resource.buckets.any method]
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

# Does such a bucket exist?
found_bucket = s3.buckets.any? { |b| b.name == 'my-bucket' }

if !found_bucket
  puts 'Bucket does not exist'
else
  # Is it in this region?
  found_bucket = s3.client.get_bucket_location(bucket: 'my-bucket').location_constraint == 'us-east-1'

  if found_bucket
    puts 'Bucket exists in this region'
  else
    puts 'Bucket does not exist in this region'
  end
end
