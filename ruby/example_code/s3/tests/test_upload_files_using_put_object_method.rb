# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../upload_files_using_put_object_method'

describe '#object_uploaded?' do
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:file_path) { "./#{object_key}" }
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:s3_resource) do
    Aws::S3::Resource.new(
      client: s3_client,
      stub_responses: {
        bucket: {
          name: bucket_name
        },
        object: {
          key: object_key
        },
        put: {}
      }
    )
  end

  it 'checks whether the object was uploaded' do
    expect(
      object_uploaded?(
        s3_resource,
        bucket_name,
        object_key,
        file_path
      )
    ).to be(true)
  end
end
