# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Demonstrates various Amazon S3 operations, such as creating and listing
#   buckets and uploading, copying, and deleting objects from buckets.

# Lists the available Amazon S3 buckets.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @example
#   list_buckets(Aws::S3::Client.new(region: 'us-east-1'))
def list_buckets(s3_client)
  response = s3_client.list_buckets
  if response.buckets.count.zero?
    puts 'No buckets.'
  else
    response.buckets.each do |bucket|
      puts bucket.name
    end
  end
rescue StandardError => e
  puts "Error listing buckets: #{e.message}"
end

# Creates a bucket in Amazon S3.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @return [Boolean] true if the bucket was created; otherwise, false.
# @example
#   exit 1 unless bucket_created?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket'
#   )
def bucket_created?(s3_client, bucket_name)
  response = s3_client.create_bucket(bucket: bucket_name)
  if response.location == '/' + bucket_name
    return true
  else
    return false
  end
rescue StandardError => e
  puts "Error creating bucket: #{e.message}"
  return false
end

# Uploads an object to a bucket in Amazon S3.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
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
  response = s3_client.put_object(
    bucket: bucket_name,
    key: object_key,
    body: object_content
  )
  if response.etag
    return true
  else
    return false
  end
rescue StandardError => e
  puts "Error uploading object: #{e.message}"
  return false
end

# Lists available objects in an Amazon S3 bucket.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @example
#   list_objects(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket'
#   )
def list_objects(s3_client, bucket_name)
  response = s3_client.list_objects_v2(bucket: bucket_name)
  if response.contents.count.zero?
    puts 'No objects.'
  else
    response.contents.each do |object|
      puts object.key
    end
  end
rescue StandardError => e
  puts "Error listing objects: #{e.message}"
end

# Copies an object from one Amazon S3 bucket to another.
#
# Prerequisites:
#
# - Two Amazon S3 bucket.
# - An object in the source bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param source_bucket_name [String] The name of the source bucket.
# @param source_object_key [String] The name of the object to copy.
# @param target_bucket_name [String] The name of the target bucket.
# @param target_object_key [String] The name of the copied object.
# @return [Boolean] true if the object was copied; otherwise, false.
# @example
#   exit 1 unless object_copied?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'my-file.txt',
#     'doc-example-bucket1',
#     'my-file-1.txt'
#   )
def object_copied?(
  s3_client,
  source_bucket_name,
  source_object_key,
  target_bucket_name,
  target_object_key
)
  response = s3_client.copy_object(
    bucket: target_bucket_name,
    copy_source: "#{source_bucket_name}/#{source_object_key}",
    key: target_object_key
  )
  if response.copy_object_result.etag
    return true
  else
    return false
  end
rescue StandardError => e
  puts "Error copying object: #{e.message}"
  return false
end

# Deletes an object from an Amazon S3 bucket.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
# - An object to be deleted from the bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @return object_key [String] The name of the object.
# @example
#   exit 1 unless object_deleted?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'my-file.txt'
#   )
def object_deleted?(s3_client, bucket_name, object_key)
  response = s3_client.delete_objects(
    bucket: bucket_name,
    delete: {
      objects: [
        {
          key: object_key
        }
      ]
    }
  )
  if response.deleted.count == 1
    return true
  else
    return false
  end
rescue StandardError => e
  puts "Error deleting object: #{e.message}"
  return false
end

# Full example call:
def run_me
  bucket_name = 'doc-example-bucket'
  object_key = 'my-file.txt'
  object_content = 'This is the content of my-file.txt.'
  target_bucket_name = 'doc-example-bucket1'
  target_object_key = 'my-file-1.txt'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  puts 'Available buckets:'
  list_buckets(s3_client)

  if bucket_created?(s3_client, bucket_name)
    puts "Bucket '#{bucket_name}' created."
  else
    puts "Bucket '#{bucket_name}' not created. Program will stop."
    exit 1
  end

  if bucket_created?(s3_client, target_bucket_name)
    puts "Bucket '#{target_bucket_name}' created."
  else
    puts "Bucket '#{target_bucket_name}' not created. Program will stop."
    exit 1
  end

  if object_uploaded?(s3_client, bucket_name, object_key, object_content)
    puts "Object '#{object_key}' uploaded to bucket '#{bucket_name}'."
  else
    puts "Object '#{object_key}' uploaded to bucket '#{bucket_name}'. " \
      'Program will stop.'
    exit 1
  end

  puts "Objects in bucket '#{bucket_name}':"
  list_objects(s3_client, bucket_name)

  if object_copied?(
    s3_client,
    bucket_name,
    object_key,
    target_bucket_name,
    target_object_key
  )
    puts "Object '#{object_key}' copied to bucket '#{target_bucket_name}' " \
      "as object '#{target_object_key}'."
  else
    puts "Object '#{object_key}' not copied to bucket '#{target_bucket_name}' " \
    "as object '#{target_object_key}'."
  end

  if object_deleted?(s3_client, bucket_name, object_key)
    puts "Object '#{object_key}' deleted from bucket '#{bucket_name}'."
  else
    puts "Object '#{object_key}' not deleted from bucket '#{bucket_name}'."
  end
end

run_me if $PROGRAM_NAME == __FILE__
