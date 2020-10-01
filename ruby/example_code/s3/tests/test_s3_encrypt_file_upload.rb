# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_encrypt_file_upload'

describe '#upload_file_encrypted_aes256_at_rest?' do
  let(:s3_client) do
    Aws::S3::Client.new(
      stub_responses: {
        put_object: {
          etag: "\"6805f2cfc46c0f04559748bb039d69ae\"",
          server_side_encryption: 'AES256'
        }
      }
    )
  end
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:object_content) { 'This is the content of my-file.txt.' }

  it 'uploads a file to a bucket and then encrypts the file server-side with AES-256' do
    expect(
      upload_file_encrypted_aes256_at_rest?(
        s3_client,
        bucket_name,
        object_key,
        object_content
      )
    ).to be(true)
  end
end
