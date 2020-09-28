# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'
require 'net/http'

# Sets the access control list (ACL) for an Amazon S3 bucket
#   for public access.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The bucket's name.
# @param access_level [String] The access level for the bucket. Allowed values
#   include private, public-read, public-read-write, and authenticated-read.
# @return [Boolean] true if the ACL was set; otherwise, false.
# @example
#   exit 1 unless bucket_acl_set?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'private'
#   )
def bucket_acl_set?(s3_client, bucket_name, access_level)
  s3_client.put_bucket_acl(
    bucket: bucket_name,
    acl: access_level
  )
  return true
rescue StandardError => e
  puts "Error setting bucket ACL: #{e.message}"
  return false
end

# Uploads an object to an Amazon S3 bucket.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The bucket's name.
# @param object_key [String] The name of the object.
# @param object_content [String] The content to add to the object.
# @return [Boolean] true if the object was uploaded; otherwise, false.
# @example
#   exit 1 unless object_uploaded?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'my-file.txt',
#     'This is the content of my-file.txt.'
#   )
def object_uploaded?(s3_client, bucket_name, object_key, object_content)
  s3_client.put_object(
    bucket: bucket_name,
    key: object_key,
    body: object_content
  )
  return true
rescue StandardError => e
  puts "Error uploading object: #{e.message}"
  return false
end

# Sets the access control list (ACL) for an object in an
#   Amazon S3 bucket for public access.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
# - An object in the bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The bucket's name.
# @param object_key [String] The name of the object.
# @param access_level [String] The access level for the bucket. Allowed values
#   include private, public-read, public-read-write, and authenticated-read.
# @return [Boolean] true if the ACL was set; otherwise, false.
# @example
#   exit 1 unless object_acl_set?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'my-file.txt',
#     'private'
#   )
def object_acl_set?(s3_client, bucket_name, object_key, access_level)
  s3_client.put_object_acl(
    bucket: bucket_name,
    key: object_key,
    acl: access_level
  )
  return true
rescue StandardError => e
  puts "Error setting object ACL: #{e.message}"
  return false
end

# Prints information about the Amazon S3 bucket at the given path.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param bucket_name [String] The bucket's name.
# @param region [String] The AWS Region for the bucket.
# @example
#   object_content_by_bucket_unsigned_request(
#     'doc-example-bucket',
#     'us-east-1'
#   )
def object_content_by_bucket_unsigned_request(bucket_name, region)
  bucket_path = "https://s3.#{region}.amazonaws.com/#{bucket_name}"
  response = Net::HTTP.get(URI(bucket_path))
  puts "Content of unsigned request to '#{bucket_path}':\n\n#{response}\n\n"
end

# Prints information about the Amazon S3 object in the bucket
#   at the given path.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
# - An object in the bucket.
#
# @param bucket_name [String] The bucket's name.
# @param object_key [String] The name of the object in the bucket.
# @param region [String] The AWS Region for the bucket.
# @example
#   object_content_by_object_unsigned_request(
#     'doc-example-bucket',
#     'my-file.txt',
#     'us-east-1'
#   )
def object_content_by_object_unsigned_request(bucket_name, object_key, region)
  object_path = "https://s3.#{region}.amazonaws.com/#{bucket_name}/#{object_key}"
  response = Net::HTTP.get(URI(object_path))
  puts "Content of unsigned request to '#{object_path}':\n\n#{response}\n\n"
end

# Full example call:
def run_me
  bucket_name = 'doc-example-bucket'
  object_key = 'my-file.txt'
  object_content = 'This is the content of my-file.txt.'
  access_level_before = 'private'
  access_level_after = 'public-read'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  # Set the initial access level of the bucket to 'private'
  #   for public access.
  if bucket_acl_set?(s3_client, bucket_name, access_level_before)
    puts "1. Initial bucket ACL set to '#{access_level_before}' " \
      "for public access.\n\n"
  else
    puts "1. Initial bucket ACL not set to '#{access_level_before}' " \
      'for public access. Stopping program.'
    exit 1
  end

  # What happens when you try to access the bucket? (It should be denied.)
  puts "2. After initial bucket ACL set to '#{access_level_before}' " \
    "for public access, trying to access the bucket:\n\n"
  object_content_by_bucket_unsigned_request(bucket_name, region)

  # Upload an object to the bucket.
  if object_uploaded?(s3_client, bucket_name, object_key, object_content)
    puts "3. Object uploaded to bucket.\n\n"
  else
    puts '3. Object not uploaded to bucket. Stopping program. ' \
      "Note that the bucket ACL is still set to '#{access_level_before}' " \
      'for public access.'
    exit 1
  end

  # What happens when you try to access the object now?
  #   (It should still be denied.)
  puts "4. After object uploaded, trying to access the object:\n\n"
  object_content_by_object_unsigned_request(bucket_name, object_key, region)

  # Now set the initial access level of the object to 'public-read'
  #   for public access.
  if object_acl_set?(s3_client, bucket_name, object_key, access_level_after)
    puts "5. Object ACL set to '#{access_level_after}' for public access.\n\n"
  else
    puts "5. Object ACL not set to '#{access_level_after}' for public " \
      'access. Stopping program. ' \
      "Note that the bucket ACL is still set to '#{access_level_before}' " \
      'for public access.'
    exit 1
  end

  # What happens when you try to access the object now? (It should now work.)
  puts "6. After object ACL set to '#{access_level_after}' for public " \
    "access, trying to access the object:\n\n"
  object_content_by_object_unsigned_request(bucket_name, object_key, region)

  # Now set the access level for the object to 'private' for public access.
  if object_acl_set?(s3_client, bucket_name, object_key, access_level_before)
    puts "7. Object ACL now set to '#{access_level_before}' " \
      "for public access.\n\n"
  else
    puts "7. Object ACL not set to '#{access_level_before}' " \
      'for public access. Stopping program. ' \
      "Note that the bucket ACL is still set to '#{access_level_before}'."
    exit 1
  end

  # What happens when you try to access the object now?
  #   (It should now be denied.)
  puts "8. After object ACL set to '#{access_level_before}' " \
    "for public access, trying to access the object:\n\n"
  object_content_by_object_unsigned_request(bucket_name, object_key, region)

  puts '9. Program ends. Note that the bucket ACL is still set to ' \
    "'#{access_level_before}' for public access."
end

run_me if $PROGRAM_NAME == __FILE__
