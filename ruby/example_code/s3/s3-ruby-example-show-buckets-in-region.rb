#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourceauthor:[Doug-AWS]
#snippet-sourcedescription:[Lists up to 50 of your S3 buckets in the specified region.]
#snippet-keyword:[Amazon Simple Storage Service]
#snippet-keyword:[get_bucket_location method]
#snippet-keyword:[Resource.buckets method]
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

require 'aws-sdk-s3'  # v2: require 'aws-sdk'

region = 'us-west-2'
s3 = Aws::S3::Resource.new(region: region)

s3.buckets.limit(50).each do |b|
  if s3.client.get_bucket_location(bucket: b.name).location_constraint == region
    puts "#{b.name}"
  end
end
