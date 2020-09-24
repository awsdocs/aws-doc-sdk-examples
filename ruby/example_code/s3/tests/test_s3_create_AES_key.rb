# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../s3_create_AES_key'

describe '#get_random_aes_256_gcm_key' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:variable_name) { 'variable_value_or_type' }

  it 'gets a random AES-256 key string' do
    expect(get_random_aes_256_gcm_key.length).to eq(32)
  end
end
