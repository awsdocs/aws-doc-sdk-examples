# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

# Purpose
# This code example demonstrates how to set the access control list (ACL) on an
# object in an Amazon Simple Storage Solution (Amazon S3) bucket for the given owner.

# snippet-start:[s3.s3_set_bucket_object_acls.rb]

require 'aws-sdk-s3'

# Sets the access control list (ACL) on an object in an Amazon S3
#   bucket for the given owner.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
# - An object in the bucket.
# - The owner's canonical ID.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name of the object.
# @param permission [String] The object permission level. Allowed values
#   include READ, READ_ACP, WRITE, WRITE_ACP, and FULL_CONTROL.
# @param owner_id [String] The canonical ID of the owner.
# @return [Boolean] true if the ACL was set; otherwise, false.
# @example
#   exit 1 unless object_acl_set_for_owner_id?(
#     Aws::S3::Client.new(region: 'us-west-2'),
#     'doc-example-bucket',
#     'my-file.txt',
#     'READ',
#     'b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE'
#   )
def object_acl_set_for_owner_id?(
    s3_client,
    bucket_name,
    object_key,
    permission,
    owner_id
  )
  s3_client.put_object_acl(
    access_control_policy: {
      grants: [
        {
          grantee: {
            id: owner_id,
            type: 'CanonicalUser'
          },
          permission: permission
        }
      ],
      owner: {
        id: owner_id
      }
    },
    bucket: bucket_name,
    key: object_key
  )
  return true
rescue StandardError => e
  puts "Error setting object ACL: #{e.message}"
  return false
end

# Full example call:
# Replace us-west-2 with the AWS Region you're using for Amazon S3.

def run_me
  bucket_name = 'doc-example-bucket'
  object_key = 'my-file-1.txt'
  permission = 'READ'
  owner_id = 'b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE'
  region = 'us-west-2'
  s3_client = Aws::S3::Client.new(region: region)

  if object_acl_set_for_owner_id?(
    s3_client,
    bucket_name,
    object_key,
    permission,
    owner_id
  )
    puts 'Object ACL set.'
  else
    puts 'Object ACL not set.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
# snippet-end:[s3.s3_set_bucket_object_acls.rb]
