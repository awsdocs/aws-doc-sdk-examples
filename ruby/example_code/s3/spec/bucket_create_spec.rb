# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../bucket_create"

describe BucketCreateWrapper do
  let(:bucket_name) { "doc-example-bucket" }
  let(:bucket) { Aws::S3::Bucket.new(bucket_name, stub_responses: true) }
  let(:wrapper) { BucketCreateWrapper.new(bucket) }

  it "confirms the bucket was created" do
    bucket.client.stub_responses(:create_bucket)
    expect(wrapper.create?("us-test-2")).to be_eql(true)
    expect(wrapper.bucket.name).to be_eql(bucket_name)
  end

  it "confirms error is caught when bucket can't be created" do
    bucket.client.stub_responses(:create_bucket, "TestError")
    expect(wrapper.create?("us-test-2")).to be_eql(false)
  end

  it "confirms bucket location" do
    loc = "us-test-2"
    stub_data = bucket.client.stub_data(:get_bucket_location, { location_constraint: loc })
    bucket.client.stub_responses(:get_bucket_location, stub_data)
    expect(wrapper.location).to be_eql(loc)
  end

  it "confirms error is caught when bucket location can't be retrieved" do
    bucket.client.stub_responses(:get_bucket_location, "TestError")
    expect(wrapper.location).to start_with("Couldn't get the location")
  end
end
