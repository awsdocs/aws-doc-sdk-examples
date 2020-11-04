# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-bucket-accessible'

describe '#bucket_in_region?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:region) { 'us-east-1' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        get_bucket_location: {
          location_constraint: region
        }
      }
    )
  end

  it 'checks whether a bucket exists within a Region' do
    expect(bucket_in_region?(s3_client, bucket_name, region)).to be(true)
  end
end
