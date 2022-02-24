# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# Purpose
#
# Shows how to get, add, and remove a policy from an
# Amazon Simple Storage Service (Amazon S3) bucket.

# snippet-start:[ruby.example_code.s3.Scenario_BucketPolicyBasics]
require "aws-sdk-s3"

# snippet-start:[ruby.example_code.s3.helper.BucketPolicyWrapper]
# Wraps an Amazon S3 bucket policy.
class BucketPolicyWrapper
  attr_reader :bucket_policy

  # @param bucket_policy [Aws::S3::BucketPolicy] A bucket policy object configured with an existing bucket.
  def initialize(bucket_policy)
    @bucket_policy = bucket_policy
  end
# snippet-end:[ruby.example_code.s3.helper.BucketPolicyWrapper]

  # snippet-start:[ruby.example_code.s3.GetBucketPolicy]
  # Gets the policy of a bucket.
  #
  # @return [Aws::S3::GetBucketPolicyOutput, nil] The current bucket policy.
  def get_policy
    policy = @bucket_policy.data.policy
    policy.respond_to?(:read) ? policy.read : policy
  rescue Aws::Errors::ServiceError => e
    puts "Couldn't get the policy for #{@bucket_policy.bucket.name}. Here's why: #{e.message}"
    nil
  end
  # snippet-end:[ruby.example_code.s3.GetBucketPolicy]

  # snippet-start:[ruby.example_code.s3.PutBucketPolicy]
  # Sets a policy on a bucket.
  #
  def set_policy(policy)
    @bucket_policy.put(policy: policy)
    true
  rescue Aws::Errors::ServiceError => e
    puts "Couldn't set the policy for #{@bucket_policy.bucket.name}. Here's why: #{e.message}"
    false
  end
  # snippet-end:[ruby.example_code.s3.PutBucketPolicy]

  # snippet-start:[ruby.example_code.s3.DeleteBucketPolicy]
  def delete_policy
    @bucket_policy.delete
    true
  rescue Aws::Errors::ServiceError => e
    puts "Couldn't delete the policy from #{@bucket_policy.bucket.name}. Here's why: #{e.message}"
    false
  end
  # snippet-end:[ruby.example_code.s3.DeleteBucketPolicy]
# snippet-start:[ruby.example_code.s3.helper.end.BucketPolicyWrapper]
end
# snippet-end:[ruby.example_code.s3.helper.end.BucketPolicyWrapper]

def run_demo
  bucket_name = "doc-example-bucket"
  policy_user = "arn:aws:iam::111122223333:user/Martha"
  policy = {
    'Version': "2012-10-17",
    'Id': "DemoBucketPolicy",
    'Statement': [
      {
        'Effect': "Allow",
        'Principal': { 'AWS': policy_user },
        'Action': %w[s3:GetObject s3:ListBucket],
        'Resource': %W[arn:aws:s3:::#{bucket_name}/* arn:aws:s3:::#{bucket_name}]
      }
    ]
  }.to_json

  wrapper = BucketPolicyWrapper.new(Aws::S3::BucketPolicy.new(bucket_name))
  return unless wrapper.set_policy(policy)

  puts "Successfully set policy on #{bucket_name}. The policy is:"
  puts wrapper.get_policy
  return unless wrapper.delete_policy

  puts "Successfully deleted the policy from #{bucket_name}."
end

run_demo if $PROGRAM_NAME == __FILE__
# snippet-end:[ruby.example_code.s3.Scenario_BucketPolicyBasics]
