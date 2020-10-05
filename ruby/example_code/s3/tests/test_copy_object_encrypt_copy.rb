# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../copy_object_encrypt_copy'

describe '#object_copied_with_encryption?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:source_bucket_name) { 'doc-example-bucket1' }
  let(:source_object_key) { 'my-file.txt' }
  let(:target_bucket_name) { 'doc-example-bucket2' }
  let(:target_object_key) { 'my-copied-file.txt' }
  let(:encryption_type) { 'AES256' }

  it 'copies an object from one bucket to another and encrypts the copied object' do
    copy_data = s3_client.stub_data(
      :copy_object,
      copy_object_result: {
        etag: '265bada5d91e2b3f19bbad42c4f0dd99',
        last_modified: Time.now
      }
    )
    s3_client.stub_responses(:copy_object, copy_data)
    expect(object_copied_with_encryption?(
        s3_client,
        source_bucket_name,
        source_object_key,
        target_bucket_name,
        target_object_key,
        encryption_type
    )).to be(true)
  end
end
