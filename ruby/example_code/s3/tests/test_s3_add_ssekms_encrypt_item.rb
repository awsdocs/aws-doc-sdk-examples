# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_add_ssekms_encrypt_item'

describe '#kms_cmk_sse_encrypted_object_uploaded?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: {
    put_object: {
      etag: "\"944e42453de468ac6d5d605faEXAMPLE\"",
      server_side_encryption: 'aws:kms',
      ssekms_key_id: 'arn:aws:kms:us-east-1:111111111111:key/9041e78c-7a20-4db3-929e-828abEXAMPLE'
    }
  })}
  let(:bucket_name) { 'doc-example-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:content_to_encrypt) { 'This is the content of my-file.txt.' }
  let(:kms_customer_key_id) { '9041e78c-7a20-4db3-929e-828abEXAMPLE' }

  it 'adds an object encrypted with an AWS KMS CMK to a bucket' do
    expect(kms_cmk_sse_encrypted_object_uploaded?(
      s3_client,
      bucket_name,
      object_key,
      content_to_encrypt,
      kms_customer_key_id
    )).to be(true)
  end
end
