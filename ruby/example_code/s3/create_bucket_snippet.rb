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
 #* specific language governing permissions and lim2019itations under the License.
#**
# snippet-sourcedescription:[create_bucket_snippet creates a bucket programmatically by using the AWS SDK for Ruby.] 
# snippet-service:[s3]
# snippet-keyword:[Ruby]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[CREATE Bucket]
# snippet-sourcetype:[snippet]
# snippet-sourcedate:[2019-01-28]
# snippet-sourceauthor:[AWS]
# snippet-start:[s3.ruby.create_bucket_snippet.rb]
require 'aws-sdk-s3'
	        
s3 = Aws::S3::Client.new(region: 'us-west-2')
s3.create_bucket(bucket: 'bucket-name')
# snippet-end:[s3.ruby.create_bucket_snippet.rb]
