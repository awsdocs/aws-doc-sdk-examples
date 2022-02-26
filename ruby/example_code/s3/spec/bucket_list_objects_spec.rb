# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../bucket_list_objects"

describe BucketListObjectsWrapper do
  let(:bucket_name) { "test-bucket" }
  let(:bucket) { Aws::S3::Bucket.new(bucket_name, stub_responses: true) }
  let(:wrapper) { BucketListObjectsWrapper.new(bucket) }

  it "confirms the objects were listed" do
    data = bucket.client.stub_data(:list_objects_v2, { contents: [{ key: "test-1" }, { key: "test-2" }] })
    bucket.client.stub_responses(:list_objects_v2, data)
    expect(wrapper.list_objects(5)).to be_eql(2)
  end

  it "confirms error is caught when objects can't be listed" do
    bucket.client.stub_responses(:list_objects_v2, "TestError")
    expect(wrapper.list_objects(5)).to be_eql(0)
  end
end
