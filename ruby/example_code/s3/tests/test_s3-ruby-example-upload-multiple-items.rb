# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3-ruby-example-upload-multiple-items'

describe '#file_exists_and_file?' do
  let(:file_name) { 'my-file.txt' }

  it 'checks whether the file exists and is indeed a file' do
    expect(file_exists_and_file?(file_name)).to be(true)
  end
end

describe '#bucket_exists?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:bucket_name) { 'doc-example-bucket' }

  it 'checks whether the bucket exists' do
    buckets_data = s3_client.stub_data(
      :list_buckets,
      buckets: [{ name: bucket_name }]
    )
    s3_client.stub_responses(:list_buckets, buckets_data)
    expect(bucket_exists?(s3_client, bucket_name)).to be(true)
  end
end

describe '#upload_file_to_bucket?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:bucket_name) { 'doc-example-bucket' }
  let(:file_name) { 'my-file.txt' }

  it 'checks whether file was uploaded to the bucket' do
    object_data = s3_client.stub_data(
      :put_object,
      {
        etag: '265bada5d91e2b3f19bbad42c4f0dd99',
        version_id: 'Kirh.unyZwjQ69YxcQLA8z4F5j3kJJKr'
      }
    )
    s3_client.stub_responses(:put_object, object_data)
    expect(upload_file_to_bucket?(s3_client, bucket_name, file_name)).to be(true)
  end
end
