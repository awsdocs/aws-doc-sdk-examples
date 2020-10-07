# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-bucket-exists'

describe '#bucket_exists?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        list_buckets: {
          buckets: [
            {
              name: bucket_name 
            }
          ]
        }
      }
    )
  end

  it 'checks whether a bucket exists' do
    expect(bucket_exists?(s3_client, bucket_name)).to be(true)
  end
end
