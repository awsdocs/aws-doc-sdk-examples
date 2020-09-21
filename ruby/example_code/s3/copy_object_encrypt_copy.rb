# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# snippet-start:[s3.ruby.copy_object_encrypt_copy.rb]
require 'aws-sdk-s3'

# Copies an object from one Amazon S3 bucket to another,
#   changing the object's server-side encryption state during 
#   the copy operation.
#
# Prerequisites:
#
# - A bucket containing an object to be copied.
# - A separate bucket to copy the object into.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param source_bucket_name [String] The source bucket's name.
# @param source_object_key [String] The name of the object to be copied.
# @param target_bucket_name [String] The target bucket's name.
# @param target_object_key [String] The name of the copied object.
# @param encryption_type [String] The server-side encryption type for
#   the copied object.
# @return [Boolean] true if the object was copied with the specified
#   server-side encryption; otherwise, false.
# @example
#   s3_client = Aws::S3::Client.new(region: 'us-east-1')
#   if object_copied_with_encryption?(
#     s3_client,
#     'doc-example-bucket1',
#     'my-source-file.txt',
#     'doc-example-bucket2',
#     'my-target-file.txt',
#     'AES256'
#   )
#     puts 'Copied.'
#   else
#     puts 'Not copied.'
#   end
def object_copied_with_encryption?(
  s3_client,
  source_bucket_name,
  source_object_key,
  target_bucket_name,
  target_object_key,
  encryption_type
)
  response = s3_client.copy_object(
    bucket: target_bucket_name,
    copy_source: source_bucket_name + '/' + source_object_key,
    key: target_object_key,
    server_side_encryption: encryption_type
  )
  return true if response.copy_object_result
rescue StandardError => e
  puts "Error while copying object: #{e.message}"
end
# snippet-end:[s3.ruby.copy_object_encrypt_copy.rb]

# Full example call:
def run_me
  s3_client = Aws::S3::Client.new(region: 'us-east-1')

  if object_copied_with_encryption?(
    s3_client,
    'doc-example-bucket1',
    'my-source-file.txt',
    'doc-example-bucket2',
    'my-target-file.txt',
    'AES256'
  )
    puts 'Copied.'
  else
    puts 'Not copied.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
