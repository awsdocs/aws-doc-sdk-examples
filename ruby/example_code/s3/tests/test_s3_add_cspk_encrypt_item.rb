# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_add_cspk_encrypt_item'

describe '#encrypted_object_uploaded?' do
  let(:bucket_name) { 'my-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:encrypted_object_content) { '***my-encrypted-content***' }
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_object: { etag: '8c009a8b36167046a47caee5b3639de4' }
      }
    )
  end

  it 'uploads an encrypted object to an Amazon S3 bucket' do
    expect(encrypted_object_uploaded?(
      s3_client,
      bucket_name,
      object_key,
      encrypted_object_content
    )).to be(true)
  end
end
