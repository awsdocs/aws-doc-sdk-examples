# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# This code example demonstrates how to uploads multiple items
# to a bucket in Amazon S3.

# Prerequisites:
#  - An existing Amazon S3 bucket.
#  - An existing folder within the bucket.
#  - One or more existing files to upload to the bucket
#      (two or more files are preferred).

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
#   exit 1 unless bucket_exists?(s3_client, 'my-bucket')
def bucket_exists?(s3_client, bucket_name)
  response = s3_client.list_buckets
  response.buckets.each do |bucket|
    return true if bucket.name == bucket_name
  end
rescue StandardError => e
  puts "Error while checking whether the bucket '#{bucket_name}' " \
    "exists: #{e.message}"
end

# Uploads a file to a bucket in Amazon S3.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param file_name [String] The name of the file.
# @return [Boolean] true if the file was uploaded; otherwise, false.
# @example
#   s3_client = Aws::S3::Client.new(region: 'us-east-1')
#   exit 1 unless upload_file_to_bucket?(s3_client, 'my-bucket', 'my-file.txt')
def upload_file_to_bucket?(s3_client, bucket_name, file_name)
  s3_client.put_object(
    body: file_name,
    bucket: bucket_name,
    key: file_name
  )
  return true
rescue StandardError => e
  puts "Error while uploading the file '#{file_name}' to the " \
    "bucket '#{bucket_name}': #{e.message}"
end

# Full example call:
=begin
proposed_file_names = ['my-file-1.txt', 'my-file-2.txt']
existing_file_names = []
uploaded_file_names = []
bucket_name = 'my-bucket'
region = 'us-east-1'
s3_client = Aws::S3::Client.new(region: region)

puts 'Checking whether the specified files exist and are indeed files...'
proposed_file_names.each do |file_name|
  if file_exists_and_file?(file_name)
    puts "The file '#{file_name}' exists and is a file."
    existing_file_names.push(file_name)
  else
    puts "The file '#{file_name}' does not exist or is not a file and will " \
      'not be uploaded.'
  end
end

if existing_file_names.count.positive?
  puts "\nThe list of existing file names is:"
  puts existing_file_names
else
  puts "\nNone of the specified files exist. Stopping program."
  exit 1
end

puts "\nChecking whether the specified bucket exists..."
if bucket_exists?(s3_client, bucket_name)
  puts "The bucket '#{bucket_name}' exists."
else
  puts "The bucket '#{bucket_name}' does not exist. Stopping program."
  exit 1
end

puts "\nUploading files..."
existing_file_names.each do |file_name|
  if upload_file_to_bucket?(s3_client, bucket_name, file_name)
    puts "The file '#{file_name}' was uploaded."
    uploaded_file_names.push(file_name)
  else
    puts "The file '#{file_name}' could not be uploaded."
  end
end

if uploaded_file_names.count.positive?
  puts "\nThe list of uploaded file names is:"
  puts uploaded_file_names
else
  puts "\nNone of the existing files were uploaded. Stopping program."
  exit 1
end
=end
