# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-head-bucket'

describe '#bucket_exists_and_accessible?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        head_bucket: {}
      }
    )
  end

  it 'checks whether the bucket exists and is accessible' do
    expect(bucket_exists_and_accessible?(s3_client, bucket_name)).to be(true)
  end
end
