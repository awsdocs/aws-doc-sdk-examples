# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../copy_object_between_buckets'

describe '#object_copied?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:source_bucket_name) { 'my-source-bucket' }
  let(:source_key) { 'my-source-file.txt' }
  let(:target_bucket_name) { 'my-target-bucket' }
  let(:target_key) { 'my-target-file.txt' }

  it 'confirms the object was copied' do
    copy_data = s3_client.stub_data(
      :copy_object,
      copy_object_result: {
        etag: '265bada5d91e2b3f19bbad42c4f0dd99',
        last_modified: Time.now
      }
    )
    s3_client.stub_responses(:copy_object, copy_data)
    expect(object_copied?(
      s3_client,
      source_bucket_name,
      source_key,
      target_bucket_name,
      target_key
    )).to be(true)
  end
end
