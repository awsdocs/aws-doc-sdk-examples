# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-show-buckets-in-region'

describe '#list_accessible_buckets_in_region' do
  let(:region) { 'us-east-1' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        list_buckets: {
          buckets: [
            {
              name: 'doc-example-bucket'
            },
            {
              name: 'doc-example-bucket2'
            },
            {
              name: 'doc-example-bucket3'
            }
          ]
        },
        get_bucket_location: {
          location_constraint: region
        }
      }
    )
  end

  it 'lists the accessible buckets in the region' do
    expect(list_accessible_buckets_in_region(s3_client, region)).to be
  end
end
