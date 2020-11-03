# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# snippet-start:[s3.ruby.upload_object_presigned_url.rb]
require 'aws-sdk-s3'
require 'net/http'

# Uploads an object to a bucket in Amazon Simple Storage Service (Amazon S3)
#   by using a presigned URL.
#
# Prerequisites:
#
# - An S3 bucket.
# - An object in the bucket to upload content to.
#
# @param s3_client [Aws::S3::Resource] An initialized S3 resource.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name of the object.
# @param object_content [String] The content to upload to the object.
# @param http_client [Net::HTTP] An initialized HTTP client.
#   This is especially useful for testing with mock HTTP clients.
#   If not specified, a default HTTP client is created.
# @return [Boolean] true if the object was uploaded; otherwise, false.
# @example
#   exit 1 unless object_uploaded_to_presigned_url?(
#     Aws::S3::Resource.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'my-file.txt',
#     'This is the content of my-file.txt'
#   )
def object_uploaded_to_presigned_url?(
  s3_resource,
  bucket_name,
  object_key,
  object_content,
  http_client = nil
)
  object = s3_resource.bucket(bucket_name).object(object_key)
  url = URI.parse(object.presigned_url(:put))

  if http_client.nil?
    Net::HTTP.start(url.host) do |http|
      http.send_request(
        'PUT',
        url.request_uri,
        object_content,
        'content-type' => ''
      )
    end
  else
    http_client.start(url.host) do |http|
      http.send_request(
        'PUT',
        url.request_uri,
        object_content,
        'content-type' => ''
      )
    end
  end
  content = object.get.body
  puts "The presigned URL for the object '#{object_key}' in the bucket " \
    "'#{bucket_name}' is:\n\n"
  puts url
  puts "\nUsing this presigned URL to get the content that " \
    "was just uploaded to this object, the object\'s content is:\n\n"
  puts content.read
  return true
rescue StandardError => e
  puts "Error uploading to presigned URL: #{e.message}"
  return false
end
# snippet-end:[s3.ruby.upload_object_presigned_url.rb]

def run_me
  bucket_name = 'doc-example-bucket'
  object_key = 'my-file.txt'
  object_content = 'This is the content of my-file.txt.'
  region = 'us-east-1'
  s3_resource = Aws::S3::Resource.new(region: region)

  unless object_uploaded_to_presigned_url?(
    s3_resource,
    bucket_name,
    object_key,
    object_content
  )
    puts "Content '#{object_content}' not uploaded to '#{object_key}' " \
      "in bucket '#{bucket_name}'."
  end
end

run_me if $PROGRAM_NAME == __FILE__
