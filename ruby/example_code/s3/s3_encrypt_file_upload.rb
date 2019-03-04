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
# snippet-sourcedescription:[s3_encrypt_file_upload.rb demonstrates how to specify that a file uploaded to Amazon S3 be encrypted at rest.] 
# snippet-service:[s3]
# snippet-keyword:[Ruby]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[ENCRYPT UPLOAD File]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-02-11]
# snippet-sourceauthor:[AWS]
# snippet-start:[s3.ruby.s3_encrypt_file_upload.rb]
# The following example demonstrates how to specify that a file uploaded to Amazon S3 be encrypted at rest.
require 'aws-sdk-s3' 

regionName = 'us-west-2' 
bucketName = 'my-bucket' 
key = 'key' 
filePath = 'local/path/to/file'
encryptionType = 'AES256'

s3 = Aws::S3::Resource.new(region:regionName) 
obj = s3.bucket(bucketName).object(key) 
obj.upload_file(filePath, :server_side_encryption => encryptionType)
# snippet-end:[s3.ruby.s3_encrypt_file_upload.rb]
