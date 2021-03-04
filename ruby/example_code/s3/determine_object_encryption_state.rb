# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# snippet-start:[s3.ruby.determine_object_encryption_state.rb]
require 'aws-sdk-s3'

# Gets the server-side encryption state of an object in an Amazon S3 bucket.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
# - An object within that bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The bucket's name.
# @param object_key [String] The object's key.
# @return [String] The server-side encryption state.
# @example
#   s3_client = Aws::S3::Client.new(region: 'us-east-1')
#   puts get_server_side_encryption_state(
#     s3_client,
#     'doc-example-bucket',
#     'my-file.txt'
#   )
def get_server_side_encryption_state(s3_client, bucket_name, object_key)
  response = s3_client.get_object(
    bucket: bucket_name,
    key: object_key
  )
  encryption_state = response.server_side_encryption
  encryption_state.nil? ? 'not set' : encryption_state
rescue StandardError => e
  "unknown or error: #{e.message}"
end
# snippet-end:[s3.ruby.determine_object_encryption_state.rb]

# Full example call:
def run_me
  bucket_name = 'doc-example-bucket'
  object_key = 'my-file.txt'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  puts "Getting server-side encryption state for object '#{object_key}' " \
    "in bucket '#{bucket_name}'..."

  state = get_server_side_encryption_state(
    s3_client,
    bucket_name,
    object_key
  )

  puts "Encryption state is #{state}."
end

run_me if $PROGRAM_NAME == __FILE__
