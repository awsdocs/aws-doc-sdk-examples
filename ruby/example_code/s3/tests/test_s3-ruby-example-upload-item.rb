# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-upload-item'

describe '#object_uploaded?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_object: {
          etag: "\"6805f2cfc46c0f04559748bb039d69ae\"",
        }
      }
    )
  end

  it 'checks whether the object was uploaded' do
    expect(object_uploaded?(s3_client, bucket_name, object_key)).to be(true)
  end
end
