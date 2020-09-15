# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_add_bucket_sses3_encryption_policy'

describe '#deny_uploads_without_server_side_aws_kms_encryption?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:bucket_name) { 'my-bucket' }

  it 'sets a bucket policy to not upload objects unless they are encrypted' do
    expect(deny_uploads_without_server_side_aws_kms_encryption?(s3_client, bucket_name)).to be(true)
  end
end
