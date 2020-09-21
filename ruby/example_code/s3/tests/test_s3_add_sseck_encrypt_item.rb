# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_add_sseck_encrypt_item'

describe '#customer_key_sse_encrypted_object_uploaded?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: {
    put_object: {
      etag: "\"944e42453de468ac6d5d605faEXAMPLE\"",
      server_side_encryption: 'AES256'
    }
  })}
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:content_to_encrypt) { 'This is the content of my-file.txt.' }
  let(:encryption_key) { get_random_aes_256_gcm_key }

  it 'adds an object encrypted with an AES256-GCM key to a bucket' do
    expect(customer_key_sse_encrypted_object_uploaded?(
      s3_client,
      bucket_name,
      object_key,
      content_to_encrypt,
      encryption_key
    )).to be(true)
  end
end
