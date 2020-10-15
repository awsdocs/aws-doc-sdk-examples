# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'aws-sdk-s3'

# Copies an object from one Amazon Simple Storage Service (Amazon S3)
#   bucket to another. You can also set an access control list
#   (ACL) and an S3 storage class on the copied object.
#
# Prerequisites:
#
# - A source S3 bucket and a target S3 bucket.
# - An object in the source bucket to copy.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param source_object_path [String] The path and file name of the
#   object to copy.
# @param target_bucket_name [String] The name of the destination bucket.
# @param target_object_path [String] The path and file name of the
#   copied object in the destination bucket.
# @param canned_acl [String] A predetermined ACL. Allowed values include
#   'private', 'public-read', 'public-read-write', 'authenticated-read',
#   'aws-exec-read', 'bucket-owner-read', and 'bucket-owner-full-control'.
#   If not specified, the default is 'private'.
# @param storage_class [String] The S3 storage class for the copied object.
#   Allowed values include 'STANDARD', 'REDUCED_REDUNDANCY', 'STANDARD_IA',
#   'ONEZONE_IA', 'INTELLIGENT_TIERING', 'GLACIER', and 'DEEP_ARCHIVE'.
#   If not specified, the default is 'STANDARD'.
# @return [Boolean] true if the object was copied; otherwise, false.
# @example
#   exit 1 unless object_copied_with_additional_properties?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket/my-file.txt',
#     'doc-example-bucket1',
#     'copied-files/my-copied-file.txt',
#     'bucket-owner-read',
#     'STANDARD_IA'
#   )
def object_copied_with_additional_properties?(
  s3_client,
  source_object_path,
  target_bucket_name,
  target_object_path,
  canned_acl = 'private',
  storage_class = 'STANDARD'
)
  s3_client.copy_object(
    bucket: target_bucket_name,
    copy_source: source_object_path,
    key: target_object_path,
    acl: canned_acl,
    storage_class: storage_class
  )
  return true
rescue StandardError => e
  puts "Error copying object: #{e.message}"
  return false
end

# Full example call:
def run_me
  source_object_path = 'doc-example-bucket/my-file.txt'
  target_bucket_name = 'doc-example-bucket1'
  target_object_path = 'copied-files/my-copied-file.txt'
  canned_acl = 'bucket-owner-read'
  storage_class = 'STANDARD_IA'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  if object_copied_with_additional_properties?(
    s3_client,
    source_object_path,
    target_bucket_name,
    target_object_path,
    canned_acl,
    storage_class
  )
    puts "Object copied from '#{source_object_path}' to " \
      "'#{target_bucket_name}/#{target_object_path}'."
  else
    puts "Object '#{source_object_path}' not copied to " \
      "'#{target_bucket_name}/#{target_object_path}'."
  end
end

run_me if $PROGRAM_NAME == __FILE__
