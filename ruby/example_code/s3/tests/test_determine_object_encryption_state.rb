# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../determine_object_encryption_state'

describe '#get_server_side_encryption_state' do
  let(:bucket_name) { 'my-bucket' }
  let(:object_key) { 'my-file.txt' }
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }

  it 'gets the server side encryption state of an object' do
    object_data = s3_client.stub_data(
      :get_object,
      {
        server_side_encryption: 'AES256'
      }
    )
    s3_client.stub_responses(:get_object, object_data)
    expect(get_server_side_encryption_state(
      s3_client,
      bucket_name,
      object_key
    )).to eq('AES256')
  end
end
