# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-show-50-buckets'

describe '#list_buckets?' do
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
        }
      }
    )
  end

  it 'lists the buckets' do
    expect(list_buckets(s3_client)).to be
  end
end
