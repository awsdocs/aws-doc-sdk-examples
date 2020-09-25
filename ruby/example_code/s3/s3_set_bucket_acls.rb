# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Sets the access control list (ACL) on an Amazon S3 bucket for the
#   given owner.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
# - The owner's canonical ID.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param permission [String] The bucket permission level. Allowed values
#   include READ, READ_ACP, WRITE, WRITE_ACP, and FULL_CONTROL.
# @param owner_id [String] The canonical ID of the owner.
# @return [Boolean] true if the ACL was set; otherwise, false.
# @example
#   exit 1 unless bucket_acl_set_for_owner_id?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'READ',
#     'b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE'
#   )
def bucket_acl_set_for_owner_id?(
  s3_client,
  bucket_name,
  permission,
  owner_id
)
  s3_client.put_bucket_acl(
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
    bucket: bucket_name
  )
  return true
rescue StandardError => e
  puts "Error setting bucket ACL: #{e.message}"
  return false
end

# Full example call:
def run_me
  bucket_name = 'doc-example-bucket'
  permission = 'READ'
  owner_id = 'b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  if bucket_acl_set_for_owner_id?(
    s3_client,
    bucket_name,
    permission,
    owner_id
  )
    puts 'Bucket ACL set.'
  else
    puts 'Bucket ACL not set.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
