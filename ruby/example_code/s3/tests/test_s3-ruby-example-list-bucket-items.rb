# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-list-bucket-items'

describe '#list_bucket_objects' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        list_objects_v2: {
          contents: [
            {
              etag: "\"70ee1738b6b21e2c8a43f3a5ab0eee71\"",
              key: 'my-file.txt'
            },
            {
              etag: "\"becf17f89c30367a9a44495d62ed521a-1\"",
              key: 'my-other-file.txt'
            }
          ]
        }
      }
    )
  end

  it 'lists the objects in the bucket' do
    expect(list_bucket_objects(s3_client, bucket_name)).to be
  end
end
