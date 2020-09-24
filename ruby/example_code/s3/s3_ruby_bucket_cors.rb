# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-s3'

# Adds a cross-origin resource sharing (CORS) configuration containing
#   a single rule to an Amazon S3 bucket.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @param allowed_methods [Array] The types of requests to allow, such as GET.
# @param allowed_origins [Array] The origins to allow, for example
#   http://www.example.com.
# @param allowed_headers [Array] The preflight request headers to allow, for
#   example x-amz-*.
# @param expose_headers [Array] The headers in the response that you want
#   callers to be able to access from their applications, for example
#   Content-Type.
# @param max_age_seconds [Integer] The maximum number of seconds
#   that your browser can cache the response for a preflight request
#   as identified by the resource, the HTTP method, and the origin.
# @returns [Boolean] true if the CORS rule was successfully set;
#   otherwise, false.
# @example
#   exit 1 unless if bucket_cors_rule_set?(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket',
#     %w[GET PUT POST DELETE],
#     %w[http://www.example.com],
#     %w[*],
#     %w[x-amz-server-side-encryption x-amz-request-id x-amz-id-2],
#     3000
#   )
def bucket_cors_rule_set?(
  s3_client,
  bucket_name,
  allowed_methods = %w[GET PUT POST DELETE HEAD],
  allowed_origins = %w[*],
  allowed_headers = nil,
  expose_headers = nil,
  max_age_seconds = nil
)
  methods = []
  if allowed_methods.count.zero?
    puts 'Error: No CORS methods provided.'
    return false
  else
    allowed_methods.each do |method|
      case method.upcase
      when 'GET', 'PUT', 'POST', 'DELETE', 'HEAD'
        methods.append(method)
      else
        puts "Error: '#{method}' is not an allowed CORS method."
        return false
      end
    end
  end
  s3_client.put_bucket_cors(
    bucket: bucket_name,
    cors_configuration: {
      cors_rules: [
        {
          allowed_headers: allowed_headers,
          allowed_methods: methods,
          allowed_origins: allowed_origins,
          expose_headers: expose_headers,
          max_age_seconds: max_age_seconds
        }
      ]
    }
  )
  return true
rescue StandardError => e
  puts "Error setting CORS methods: #{e.message}"
  return false
end

# Gets the cross-origin resource sharing (CORS) rules for an Amazon S3 bucket.
#
# Prerequisites:
#
# - An Amazon S3 bucket.
#
# @param s3_client [Aws::S3::Client] An initialized Amazon S3 client.
# @param bucket_name [String] The name of the bucket.
# @returns [Array<Aws::S3::Types::CORSRule>] The list of CORS rules.
# @example
#   puts bucket_cors_rules(
#     Aws::S3::Client.new(region: 'us-east-1'),
#     'doc-example-bucket')
def bucket_cors_rules(s3_client, bucket_name)
  response = s3_client.get_bucket_cors(bucket: bucket_name)
  response.cors_rules
rescue StandardError => e
  puts "Error getting CORS rules: #{e.message}"
end

def run_me
  bucket_name = 'doc-example-bucket'
  allowed_methods = %w[GET PUT POST DELETE]
  allowed_origins = %w[http://www.example.com]
  allowed_headers = %w[*]
  expose_headers = %w[x-amz-server-side-encryption x-amz-request-id x-amz-id-2]
  max_age_seconds = 3000
  region = 'us-east-1'
  s3_client = Aws::S3::Client.new(region: region)

  if bucket_cors_rule_set?(
    s3_client,
    bucket_name,
    allowed_methods,
    allowed_origins,
    allowed_headers,
    expose_headers,
    max_age_seconds
  )
    puts 'CORS rule set. Current rules:'
    puts bucket_cors_rules(s3_client, bucket_name)
  else
    puts 'CORS rule not set.'
  end
end

run_me if $PROGRAM_NAME == __FILE__
