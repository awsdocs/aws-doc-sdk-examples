# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-upload-item-with-metadata'

describe '#object_uploaded_with_metadata?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:metadata) do
    {
      author: 'Mary Doe',
      version: '1.0.0.0'
    }
  end
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_object: {
          etag: "\"6805f2cfc46c0f04559748bb039d69ae\"",
        }
      }
    )
  end

  it 'checks whether the object was uploaded with metadata' do
    expect(
      object_uploaded_with_metadata?(
        s3_client,
        bucket_name,
        object_key,
        metadata
      )
    ).to be(true)
  end
end
