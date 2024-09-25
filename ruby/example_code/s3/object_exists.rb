# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
#
# Shows how to check if an Amazon Simple Storage Service (Amazon S3) object exists.

# snippet-start:[ruby.example_code.s3.exists]
require 'aws-sdk-s3'

# Wraps Amazon S3 object actions.
class ObjectExistsWrapper
  attr_reader :object

  # @param object [Aws::S3::Object] An Amazon S3 object.
  def initialize(object)
    @object = object
  end

  # Checks whether the object exists.
  #
  # @return [Boolean] True if the object exists; otherwise false.
  def exists?
    @object.exists?
  rescue Aws::Errors::ServiceError => e
    puts "Couldn't check existence of object #{@object.bucket.name}:#{@object.key}. Here's why: #{e.message}"
    false
  end
end

# Example usage:
def run_demo
<<<<<<< HEAD
  bucket_name = "amzn-s3-demo-bucket"
  object_key = "my-object.txt"
=======
  bucket_name = 'doc-example-bucket'
  object_key = 'my-object.txt'
>>>>>>> 999c6133e (fixes)

  wrapper = ObjectExistsWrapper.new(Aws::S3::Object.new(bucket_name, object_key))
  exists = wrapper.exists?

  puts "Object #{object_key} #{exists ? 'does' : 'does not'} exist."
end

run_demo if $PROGRAM_NAME == __FILE__
# snippet-end:[ruby.example_code.s3.exists]
