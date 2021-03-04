# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Lists the Amazon Simple Storage Service (Amazon S3) buckets owned by the
#   authenticated sender of the request.
#
# @param s3_client [Aws::S3::Client] An initialized S3 client.
# @param max_buckets [Integer] The maximum number of buckets to list. The
#   number must be between 1 and 50. If not specified, only up to the
#   first 50 objects will be listed.
# @example
#   list_buckets(Aws::S3::Client.new(region: 'us-east-1'), 25)
def list_buckets(s3_client, max_buckets = 50)
  if max_buckets < 1 || max_buckets > 50
    puts 'Maximum number of buckets to request must be between 1 and 50.'
    return
  end
  buckets = s3_client.list_buckets.buckets
  if buckets.count.zero?
    puts 'No buckets.'
    return
  else
    if buckets.count > max_buckets
      puts "First #{max_buckets} buckets:"
      i = 0
      max_buckets.times do
        puts "#{i + 1}) #{buckets[i].name}"
        i += 1
      end
    else
      puts "#{buckets.count} buckets:"
      i = 0
      buckets.count.times do
        puts "#{i + 1}) #{buckets[i].name}"
        i += 1
      end
    end
  end
rescue StandardError => e
  puts "Error listing buckets: #{e.message}"
end

def run_me
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)
  list_buckets(s3_client, 25)
end

run_me if $PROGRAM_NAME == __FILE__
