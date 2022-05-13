# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../bucket_list"

describe BucketListWrapper do
  let(:resource) { Aws::S3::Resource.new(stub_responses: true) }
  subject { BucketListWrapper.new(resource) }

  it "confirms buckets were listed" do
    data = resource.client.stub_data(
      :list_buckets,
      { 'buckets': [{ 'name': "bucket-1" }, { 'name': "bucket-2" }] }
    )
    resource.client.stub_responses(:list_buckets, data)
    expect(subject.list_buckets(20)).to be(true)
  end

  it "confirms error is caught when bucket can't be created" do
    resource.client.stub_responses(:list_buckets, "TestError")
    expect(subject.list_buckets(20)).to be(false)
  end
end
