# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../auth_request_test'

describe '#list_bucket_objects?' do
  let(:s3_client) { Aws::S3::Client.new(stub_responses: true) }
  let(:bucket_name) { 'doc-example-bucket' }

  it "lists the objects' keys in the specified bucket" do
    objects_data = s3_client.stub_data(
      :list_objects_v2,
      contents: [
        { key: 'my-file-1.txt' },
        { key: 'my-file-2.txt' }
      ]
    )
    s3_client.stub_responses(:list_objects_v2, objects_data)
    expect(list_bucket_objects?(s3_client, bucket_name)).to be(true)
  end
end
