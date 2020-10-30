# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-create-bucket'

describe '#bucket_created?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        create_bucket: {
          location: '/' + bucket_name
        }
      }
    )
  end

  it 'checks whether a bucket was created' do
    expect(bucket_created?(s3_client, bucket_name)).to be(true)
  end
end
