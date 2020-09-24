# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_ruby_bucket_cors'

describe '#bucket_cors_rule_set?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:allowed_methods) { %w[GET PUT POST DELETE HEAD] }
  let(:allowed_origins) { %w[*] }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_bucket_cors: {}
      }
    )
  end

  it 'checks whether the CORS rules for a bucket are set' do
    expect(
      bucket_cors_rule_set?(
        s3_client,
        bucket_name,
        allowed_methods,
        allowed_origins
      )
    ).to be(true)
  end
end

describe '#bucket_cors_rules' do
  let(:allowed_origin) { 'http://www.example.com' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        get_bucket_cors: {
          cors_rules: [
            {
              allowed_headers: [
                '*'
              ],
              allowed_methods: [
                'GET',
                'PUT',
                'POST',
                'DELETE'
              ],
              allowed_origins: [
                allowed_origin
              ],
              max_age_seconds: 3000
            }
          ]
        }
      }
    )
  end
  let(:bucket_name) { 'doc-example-bucket' }

  it 'gets the CORS rules for a bucket' do
    cors_rules = bucket_cors_rules(s3_client, bucket_name)
    expect(cors_rules[0].allowed_origins[0]).to eq(allowed_origin)
  end
end
