# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_add_sses3_encrypt_item'

describe '#kms_sse_encrypted_object_uploaded?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: {
    put_object: {
      etag: "\"944e42453de468ac6d5d605faEXAMPLE\"",
      server_side_encryption: 'aws:kms'
    }
  })}
  let(:bucket_name) { 'my-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:content_to_encrypt) { 'This is the content of my-file.txt.' }

  it 'adds an object encrypted with the default aws/s3 AWS KMS CMK to a bucket' do
    expect(kms_sse_encrypted_object_uploaded?(
      s3_client,
      bucket_name,
      object_key,
      content_to_encrypt
    )).to be(true)
  end
end
