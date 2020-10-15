# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# snippet-start:[s3.ruby.upload_files_using_managed_file_uploader.rb]
require 'aws-sdk-s3'

# Uploads an object to a bucket in Amazon Simple Storage Service (Amazon S3).
#
# Prerequisites:
#
# - An S3 bucket.
# - An object to upload to the bucket.
#
# @param s3_client [Aws::S3::Resource] An initialized S3 resource.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name of the object.
# @param file_path [String] The path and file name of the object to upload.
# @return [Boolean] true if the object was uploaded; otherwise, false.
# @example
#   exit 1 unless object_uploaded?(
#     Aws::S3::Resource.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'my-file.txt',
#     './my-file.txt'
#   )
def object_uploaded?(s3_resource, bucket_name, object_key, file_path)
  object = s3_resource.bucket(bucket_name).object(object_key)
  object.upload_file(file_path)
  return true
rescue StandardError => e
  puts "Error uploading object: #{e.message}"
  return false
end
# snippet-end:[s3.ruby.upload_files_using_managed_file_uploader.rb]

# Full example call:
def run_me
  bucket_name = 'doc-example-bucket'
  object_key = 'my-file.txt'
  file_path = "./#{object_key}"
  region = 'us-east-1'
  s3_resource = Aws::S3::Resource.new(region: region)

  if object_uploaded?(s3_resource, bucket_name, object_key, file_path)
    puts "Object '#{object_key}' uploaded to bucket '#{bucket_name}''."
  else
    puts "Object '#{object_key}' not uploaded to bucket '#{bucket_name}'."
  end
end

run_me if $PROGRAM_NAME == __FILE__
