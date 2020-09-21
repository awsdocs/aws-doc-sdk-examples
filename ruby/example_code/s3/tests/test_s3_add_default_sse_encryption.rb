# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_add_default_sse_encryption'

describe '#default_bucket_encryption_sse_cmk_set?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:bucket_name) { 'doc-example-bucket' }
  let(:kms_master_key_id) { '9041e78c-7a20-4db3-929e-828abEXAMPLE' }

  it 'sets the default encryption state for a bucket using SSE with an AWS KMS CMK' do
    expect(default_bucket_encryption_sse_cmk_set?(
      s3_client,
      bucket_name,
      kms_master_key_id
    )).to be(true)
  end
end
