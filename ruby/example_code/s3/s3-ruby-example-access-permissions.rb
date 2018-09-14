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

# Create a S3 client
client = Aws::S3::Client.new(region: 'us-west-2')

bucket = 'my-bucket'
# Sets a bucket to public-read
client.put_bucket_acl({
  acl: "public-read",
  bucket: bucket,
})

object_key = "my-key"
# Put an object in the public bucket
client.put_object({
  bucket: bucket,
  key: object_key,
  body: 'Hello World',
})

# Accessing an object in the bucket with unauthorize request
bucket_path = "http://#{bucket}.s3-us-west-2.amazonaws.com/"
resp = Net::HTTP.get(URI(bucket_path))
puts "Content of unsigned request to #{bucket_path}:\n\n#{resp}\n\n"

# However, accessing the object is denied since object Acl is not public-read
object_path = "http://#{bucket}.s3-us-west-2.amazonaws.com/#{object_key}"
resp = Net::HTTP.get(URI(object_path))
puts "Content of unsigned request to #{object_path}:\n\n#{resp}\n\n"

# Setting the object to public-read
client.put_object_acl({
  acl: "public-read",
  bucket: bucket,
  key: object_key,
})
object_path = "http://#{bucket}.s3-us-west-2.amazonaws.com/#{object_key}"
puts "Now I can access object (#{object_key}) :\n#{Net::HTTP.get(URI(object_path))}\n\n"

# Setting bucket to private again
client.put_bucket_acl({
  bucket: bucket,
  acl: 'private',
})

# Get current bucket Acl
resp = client.get_bucket_acl(bucket: bucket)
puts resp.grants

resp = Net::HTTP.get(URI(bucket_path))
puts "Content of unsigned request to #{bucket_path}:\n\n#{resp}\n\n"
