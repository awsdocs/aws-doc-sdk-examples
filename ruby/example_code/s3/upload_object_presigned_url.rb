#**
 #* Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
# snippet-sourcedescription:[create_bucket.rb uploads an object using a presigned URL.]
# snippet-service:[s3]
# snippet-keyword:[Ruby]
# snippet-sourcesyntax:[ruby]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Code Sample]
# snippet-keyword:[PUT Bucket, Presigned URL]
# snippet-keyword:[Upload-presigned-url]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2020-Feb-3]
# snippet-sourceauthor:[Doug-AWS]
# snippet-start:[s3.ruby.upload_object_presigned_url.rb]
# Upload an object using a presigned URL for SDK for Ruby - Version 3.

require 'aws-sdk-s3'
require 'net/http'

s3 = Aws::S3::Resource.new(region: 'us-west-2')

# Replace BucketName with the name of your bucket.
# Replace KeyName with the name of the object you are creating or replacing.
obj = s3.bucket('BucketName').object('KeyName')

url = URI.parse(obj.presigned_url(:put))

# The contents of your object, as a string
body = 'Hello World!'

Net::HTTP.start(url.host) do |http|
  http.send_request('PUT', url.request_uri, body,
                     # Or else Net::HTTP adds a default, unsigned content-type
                    'content-type' => '')
end

# Print the contents of your object to the terminal window
puts obj.get.body.read
# snippet-end:[s3.ruby.upload_object_presigned_url.rb]
