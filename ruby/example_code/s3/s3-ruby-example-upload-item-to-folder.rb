# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# This code example demonstrates how to upload an item
# (file) to a folder within a bucket in Amazon S3.

# Prerequisites:
#  - An existing Amazon S3 bucket.
#  - An existing folder within the bucket.
#  - An existing file to upload to the folder.

require 'aws-sdk-s3'

# Checks whether a file exists and is indeed a file.
#
# @param file_name [String] The name of the file.
# @return [Boolean] true if the file exists and is indeed a file;
#   otherwise, false.
# @example
#   exit 1 unless file_exists_and_file?('my-file.txt')
def file_exists_and_file?(file_name)
  return true if File.exist?(file_name) && File.file?(file_name)
end

# Checks whether a bucket exists in Amazon S3.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @return [Boolean] true if the bucket exists; otherwise, false.
# @example
#   s3_client = Aws::S3::Client.new(region: 'us-east-1')
#   exit 1 unless bucket_exists?(s3_client, 'doc-example-bucket')
def bucket_exists?(s3_client, bucket_name)
  response = s3_client.list_buckets
  response.buckets.each do |bucket|
    return true if bucket.name == bucket_name
  end
rescue StandardError => e
  puts "Error while checking whether the bucket '#{bucket_name}' " \
    "exists: #{e.message}"
end

# Checks whether a folder exists in a bucket in Amazon S3.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param folder_name [String] The name of the folder.
# @return [Boolean] true if the folder exists; otherwise, false.
# @example
#   s3_client = Aws::S3::Client.new(region: 'us-east-1')
#   exit 1 unless folder_exists?(s3_client, 'doc-example-bucket', 'my-folder/')
def folder_exists?(s3_client, bucket_name, folder_name)
  response = s3_client.list_objects_v2(bucket: bucket_name)
  if response.count.positive?
    response.contents.each do |object|
      return true if object.key == folder_name
    end
  end
rescue StandardError => e
  puts "Error while checking whether the folder '#{folder_name}' " \
    "exists: #{e.message}"
end

# Uploads a file to a folder within a bucket in Amazon S3.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param folder_name [String] The name of the folder.
# @param file_name [String] The name of the file.
# @return [Boolean] true if the file was uploaded; otherwise, false.
# @example
#   s3_client = Aws::S3::Client.new(region: 'us-east-1')
#   exit 1 unless upload_file_to_folder?(
#     s3_client,
#     'doc-example-bucket',
#     'my-folder/',
#     'my-file.txt')
def upload_file_to_folder?(s3_client, bucket_name, folder_name, file_name)
  s3_client.put_object(
    body: file_name,
    bucket: bucket_name,
    key: folder_name + file_name
  )
  return true
rescue StandardError => e
  puts "Error while uploading the file '#{file_name}' to the " \
    "folder '#{folder_name}' in the bucket '#{bucket_name}': #{e.message}"
end

# Full example call:
def run_me
  file_name = 'my-file-1.txt'
  bucket_name = 'doc-example-bucket'
  folder_name = 'my-folder/'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  puts 'Checking whether the specified file exists and is indeed a file...'
  if file_exists_and_file?(file_name)
    puts "The file '#{file_name}' exists and is a file."
  else
    puts "The file '#{file_name}' does not exist or is not a file and will " \
        'not be uploaded. Stopping program.'
    exit 1
  end

  puts "\nChecking whether the specified bucket exists..."
  if bucket_exists?(s3_client, bucket_name)
    puts "The bucket '#{bucket_name}' exists."
  else
    puts "The bucket '#{bucket_name}' does not exist. Stopping program."
    exit 1
  end

  puts "\nChecking whether the specified folder exists..."
  if folder_exists?(s3_client, bucket_name, folder_name)
    puts "The folder '#{folder_name}' exists."
  else
    puts "The folder '#{folder_name}' does not exist in the bucket " \
      "'#{bucket_name}' or access to the bucket is denied. Stopping program."
    exit 1
  end

  puts "\nUploading file..."
  if upload_file_to_folder?(s3_client, bucket_name, folder_name, file_name)
    puts "The file '#{file_name}' was uploaded."
  else
    puts "The file '#{file_name}' could not be uploaded. Stopping program."
    exit 1
  end
end

run_me if $PROGRAM_NAME == __FILE__