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
# snippet-sourcedescription:[copy_object_encrypt_copy.rb demonstrates how to copy an object and encrypt the copy.] 
# snippet-service:[s3]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[COPY Object]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-28]
# snippet-sourceauthor:[AWS]
# snippet-start:[s3.ruby.copy_object_encrypt_copy.rb]
require 'aws-sdk-s3'

regionName = 'us-west-2' 
encryptionType = 'AES256'

s3 = Aws::S3::Resource.new(region:regionName)
bucket1 = s3.bucket('source-bucket-name')
bucket2 = s3.bucket('target-bucket-name')
obj1 = bucket1.object('Bucket1Key')
obj2 = bucket2.object('Bucket2Key')
 
obj1.copy_to(obj2, :server_side_encryption => encryptionType)
# snippet-end:[s3.ruby.copy_object_encrypt_copy.rb]
