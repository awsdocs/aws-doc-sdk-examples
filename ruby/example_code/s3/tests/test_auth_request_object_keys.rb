# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative '../auth_request_object_keys.rb'

describe '#can_list_bucket_objects?' do
  let(:s3) { Aws::S3::Client.new(stub_responses: true) }
  let(:bucket_name) { 'my-bucket' }

  it "lists the objects' keys in the specified bucket" do
    objects_data = s3.stub_data(:list_objects_v2,
      contents: [
        { key: 'my-file-1.txt' },
        { key: 'my-file-2.txt' }
      ]
    )
    s3.stub_responses(:list_objects_v2, objects_data)
    expect(can_list_bucket_objects?(s3, bucket_name)).to be
  end
end
