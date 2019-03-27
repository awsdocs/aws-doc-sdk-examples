#**
 #* Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 #*
 #* This file is licensed under the Apache License, Version 2.0 (the "License").
 #* You may not use this file except in compliance with the License. A copy of
 #* the License is located at
 #*
 #* http://aws.amazon.com/apache2.0/
 #*
 #* This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 #* CONDITIONS OF ANY KIND, either express or implied. See the License for the
 #* specific language governing permissions and limitations under the License.
#**
# snippet-sourcedescription:[copy_object_between_buckets demonstrates the preceding tasks using the "copy_object" method to copy an object from one bucket to another.] 
# snippet-service:[s3]
# snippet-keyword:[Ruby]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[COPY object]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-02-11]
# snippet-sourceauthor:[AWS]
# snippet-start:[s3.ruby.copy_object_between_buckets.rb]
require 'aws-sdk-s3'

source_bucket_name = '*** Provide bucket name ***'
target_bucket_name = '*** Provide bucket name ***'
source_key = '*** Provide source key ***'
target_key = '*** Provide target key ***'

begin
  s3 = Aws::S3::Client.new(region: 'us-west-2')
  s3.copy_object(bucket: target_bucket_name, copy_source: source_bucket_name + '/' + source_key, key: target_key)
rescue StandardError => ex
  puts 'Caught exception copying object ' + source_key + ' from bucket ' + source_bucket_name + ' to bucket ' + target_bucket_name + ' as ' + target_key + ':'
  puts ex.message
end

puts 'Copied ' +  source_key + ' from bucket ' + source_bucket_name + ' to bucket ' + target_bucket_name + ' as ' + target_key
# snippet-end:[s3.ruby.copy_object_between_buckets.rb]
