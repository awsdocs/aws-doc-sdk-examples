# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_ruby_bucket_website'

describe '#bucket_website_configured?' do
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_bucket_website: {}
      }
    )
  end
  let(:bucket_name) { 'doc-example-bucket' }
  let(:index_document) { 'index.html' }
  let(:error_document) { '404.html' }

  it 'checks whether a bucket is configured as a static website' do
    expect(
      bucket_website_configured?(
        s3_client,
        bucket_name,
        index_document,
        error_document
      )
    ).to be(true)
  end
end
