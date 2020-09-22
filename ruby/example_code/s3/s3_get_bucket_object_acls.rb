# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Lists the access control lists (ACLs) for an object in an Amazon S3 bucket.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
# - An object in the bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param object_key [String] The name of the object.
# @return [String] Information about the ACLs.
# @example
#   list_object_acls(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     'my-file.txt'
#   )
def list_object_acls(s3_client, bucket_name, object_key)
  response = s3_client.get_object_acl(bucket: bucket_name, key: object_key)
  if response.grants.count.zero?
    puts 'No ACLs for this object.'
  else
    puts
    puts "Owner       #{response.owner.display_name}"
    puts
    response.grants.each do |grant|
      grantee = grant.grantee
      if grantee.type == 'Group'
        puts 'Grantee     GROUP'
        puts 'URI         ' + grantee.uri
      else
        if grantee.display_name.nil?
          puts 'Grantee     EVERYONE'
        else
          puts 'Grantee     ' + grantee.display_name
        end
        if grantee.id.nil?
          puts 'ID          NONE'
        else
          puts 'ID          ' + grantee.id
        end
      end
      puts 'Permission  ' + grant.permission
      puts
    end
  end
rescue StandardError => e
  puts "Error getting bucket ACLs: #{e.message}"
end

def run_me
  bucket_name = 'doc-example-bucket'
  object_key = 'my-file.txt'
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  list_object_acls(s3_client, bucket_name, object_key)
end

run_me if $PROGRAM_NAME == __FILE__
