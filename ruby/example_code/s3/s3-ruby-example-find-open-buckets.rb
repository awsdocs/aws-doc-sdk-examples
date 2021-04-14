# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Checks to see which Amazon Simple Storage Service (Amazon S3)
#   buckets are open for public read access. These buckets must also
#   be accessible to you and were initially created with the target
#   AWS Region specified.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param region [String] The Region to check.
# @return [Array] The list of any buckets open for public read access.
# @example
#   open_buckets = []
#   open_buckets = get_open_buckets(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'us-east-1'
#   )
#   unless open_buckets.count.zero?
#     open_buckets.each do |open_bucket|
#       puts open_bucket
#     end
#   end
def get_open_buckets(s3_client, region)
  open_buckets = []
  response = s3_client.list_buckets
  if response.buckets.count.zero?
    return open_buckets
  else
    response.buckets.each do |bucket|
      location = s3_client.get_bucket_location(
        bucket: bucket.name
      ).location_constraint
      if region == location
        bucket_acl = s3_client.get_bucket_acl(bucket: bucket.name)
        grants = bucket_acl.grants
        grants.each do |grant|
          if grant.grantee.display_name.nil? && grant.permission == 'READ'
            open_buckets << bucket.name
          end
        end
      end
    end
  end
  return open_buckets
rescue StandardError => e
  puts "Error getting information about buckets: #{e.message}"
end

# Full example call:
def run_me
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)
  open_buckets = get_open_buckets(s3_client, region)
  if open_buckets.count.zero?
    puts 'No open buckets among accessible buckets with AWS Region specified ' \
      "as '#{region}' on initial creation."
  else
    puts 'Open buckets among accessible buckets with AWS Region specified ' \
      "as '#{region}' on initial creation:"
    open_buckets.each do |open_bucket|
      puts open_bucket
    end
  end
end

run_me if $PROGRAM_NAME == __FILE__
