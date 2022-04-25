# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
#
# Shows how to configure cross-origin resource sharing (CORS) rules for an
# Amazon Simple Storage Service (Amazon S3) bucket.

# snippet-start:[ruby.example_code.s3.helper.BucketCorsWrapper]
require "aws-sdk-s3"

# Wraps Amazon S3 bucket CORS configuration.
class BucketCorsWrapper
  attr_reader :bucket_cors

  # @param bucket_cors [Aws::S3::BucketCors] A bucket CORS object configured with an existing bucket.
  def initialize(bucket_cors)
    @bucket_cors = bucket_cors
  end
# snippet-end:[ruby.example_code.s3.helper.BucketCorsWrapper]

  # snippet-start:[ruby.example_code.s3.GetBucketCors]
  # Gets the CORS configuration of a bucket.
  #
  # @return [Aws::S3::Type::GetBucketCorsOutput, nil] The current CORS configuration for the bucket.
  def get_cors
    @bucket_cors.data
  rescue Aws::Errors::ServiceError => e
    puts "Couldn't get CORS configuration for #{@bucket_cors.bucket.name}. Here's why: #{e.message}"
    nil
  end
  # snippet-end:[ruby.example_code.s3.GetBucketCors]

  # snippet-start:[ruby.example_code.s3.PutBucketCors]
  # Sets CORS rules on a bucket.
  #
  # @param allowed_methods [Array<String>] The types of HTTP requests to allow.
  # @param allowed_origins [Array<String>] The origins to allow.
  # @returns [Boolean] True if the CORS rules were set; otherwise, false.
  def set_cors(allowed_methods, allowed_origins)
    @bucket_cors.put(
      cors_configuration: {
        cors_rules: [
          {
            allowed_methods: allowed_methods,
            allowed_origins: allowed_origins,
            allowed_headers: %w[*],
            max_age_seconds: 3600
          }
        ]
      }
    )
    true
  rescue Aws::Errors::ServiceError => e
    puts "Couldn't set CORS rules for #{@bucket_cors.bucket.name}. Here's why: #{e.message}"
    false
  end
  # snippet-end:[ruby.example_code.s3.PutBucketCors]

  # snippet-start:[ruby.example_code.s3.DeleteBucketCors]
  # Deletes the CORS configuration of a bucket.
  #
  # @return [Boolean] True if the CORS rules were deleted; otherwise, false.
  def delete_cors
    @bucket_cors.delete
    true
  rescue Aws::Errors::ServiceError => e
    puts "Couldn't delete CORS rules for #{@bucket_cors.bucket.name}. Here's why: #{e.message}"
    false
  end
  # snippet-end:[ruby.example_code.s3.DeleteBucketCors]
# snippet-start:[ruby.example_code.s3.helper.end.BucketCorsWrapper]
end
# snippet-end:[ruby.example_code.s3.helper.end.BucketCorsWrapper]

def run_demo
  bucket_name = "doc-example-bucket"
  allowed_methods = %w[GET PUT]
  allowed_origins = %w[http://www.example.com]

  wrapper = BucketCorsWrapper.new(Aws::S3::BucketCors.new(bucket_name))
  return unless wrapper.set_cors(allowed_methods, allowed_origins)

  puts "Successfully set CORS rules on #{bucket_name}. The rules are:"
  puts wrapper.get_cors
  return unless wrapper.delete_cors

  puts "Successfully deleted CORS rules from #{bucket_name}."
end

run_demo if $PROGRAM_NAME == __FILE__
