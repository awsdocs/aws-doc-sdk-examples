# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
#
# Shows how to set the default encryption state for an Amazon Simple Storage Service (Amazon S3)
# bucket using server-side encryption.

# snippet-start:[ruby.example_code.s3.PutBucketEncryption]
require "aws-sdk-s3"

# Wraps Amazon S3 actions.
class BucketEncryptionWrapper
  attr_reader :s3_client

  # @param s3_client [Aws::S3::Client] An Amazon S3 client.
  def initialize(s3_client)
    @s3_client = s3_client
  end

  def set_encryption(bucket_name)
    @s3_client.put_bucket_encryption(
      bucket: bucket_name,
      server_side_encryption_configuration:
      {
        rules:
        [
          {
            apply_server_side_encryption_by_default:
              { sse_algorithm: "AES256" }
          }
        ]
      }
    )
    true
  rescue Aws::Errors::ServiceError => e
    puts "Couldn't set default encryption on #{bucket_name}. Here's why: #{e.message}"
    false
  end
end

def run_demo
  bucket_name = "doc-example-bucket"
  wrapper = BucketEncryptionWrapper.new(Aws::S3::Client.new)
  return unless wrapper.set_encryption(bucket_name)

  puts "Set default encryption on #{bucket_name}."
end

run_demo if $PROGRAM_NAME == __FILE__
# snippet-end:[ruby.example_code.s3.PutBucketEncryption]
