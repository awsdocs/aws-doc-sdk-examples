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
# snippet-sourcedescription:[determine_object_encryption_state.rb shows how to determine the encryption state of an existing object.] 
# snippet-service:[s3]
# snippet-keyword:[Ruby]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[GET server_side_encryption Object]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-02-11]
# snippet-sourceauthor:[AWS]
# snippet-start:[s3.ruby.determine_object_encryption_state.rb]
# Determine server-side encryption of an object.
require 'aws-sdk-s3'

regionName = 'us-west-2' 
bucketName='bucket-name'
key = 'key'

s3 = Aws::S3::Resource.new(region:regionName)
enc = s3.bucket(bucketName).object(key).server_side_encryption
enc_state = (enc != nil) ? enc : "not set"
puts "Encryption state is #{enc_state}."
# snippet-end:[s3.ruby.determine_object_encryption_state.rb]
