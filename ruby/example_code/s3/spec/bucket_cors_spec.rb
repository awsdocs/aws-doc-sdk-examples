# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../bucket_cors"

describe BucketCorsWrapper do
  let(:bucket_name) { "test-bucket" }
  let(:bucket_cors) { Aws::S3::BucketCors.new(bucket_name, stub_responses: true) }
  let(:wrapper) { BucketCorsWrapper.new(bucket_cors) }

  it "confirms CORS rules were set" do
    bucket_cors.client.stub_responses(:put_bucket_cors)
    expect(wrapper.set_cors(%w[GET DELETE], %w[http://www.example1.com http://www.example2.com])).to be_eql(true)
  end

  it "confirms error is caught when CORS can't be set" do
    bucket_cors.client.stub_responses(:put_bucket_cors, "TestError")
    expect(wrapper.set_cors(%w[GET DELETE], %w[http://www.example1.com])).to be_eql(false)
  end

  it "confirms CORS rules were retrieved" do
    data = bucket_cors.client.stub_data(
      :get_bucket_cors, {
        cors_rules: [{ allowed_methods: %w[GET POST], allowed_origins: %w[https://www.example.com] }]
      }
    )
    bucket_cors.client.stub_responses(:get_bucket_cors, data)
    expect(wrapper.get_cors).to be_eql(data)
  end

  it "confirms error is caught when CORS can't be retrieved" do
    bucket_cors.client.stub_responses(:get_bucket_cors, "TestError")
    expect(wrapper.get_cors).to be_nil
  end

  it "confirms CORS rules were deleted" do
    bucket_cors.client.stub_responses(:delete_bucket_cors)
    expect(wrapper.delete_cors).to be_eql(true)
  end

  it "confirms error is caught when CORS can't be deleted" do
    bucket_cors.client.stub_responses(:delete_bucket_cors, "TestError")
    expect(wrapper.delete_cors).to be_eql(false)
  end
end
