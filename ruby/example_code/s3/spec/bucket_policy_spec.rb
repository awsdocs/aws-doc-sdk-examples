# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../bucket_policy"

describe BucketPolicyWrapper do
  let(:bucket_name) { "test-bucket" }
  let(:bucket_policy) { Aws::S3::BucketPolicy.new(bucket_name, stub_responses: true) }
  let(:wrapper) { BucketPolicyWrapper.new(bucket_policy) }

  it "confirms policy was set" do
    bucket_policy.client.stub_responses(:put_bucket_policy)
    expect(wrapper.set_policy("test policy")).to be_eql(true)
  end

  it "confirms error is caught when policy can't be set" do
    bucket_policy.client.stub_responses(:put_bucket_policy, "TestError")
    expect(wrapper.set_policy("test policy")).to be_eql(false)
  end

  it "confirms policy was retrieved" do
    policy = "test policy"
    data = bucket_policy.client.stub_data(
      :get_bucket_policy, { policy: "test policy" }
    )
    bucket_policy.client.stub_responses(:get_bucket_policy, data)
    expect(wrapper.get_policy).to be_eql(policy)
  end

  it "confirms error is caught when policy can't be retrieved" do
    bucket_policy.client.stub_responses(:get_bucket_policy, "TestError")
    expect(wrapper.get_policy).to be_nil
  end

  it "confirms policy was deleted" do
    bucket_policy.client.stub_responses(:delete_bucket_policy)
    expect(wrapper.delete_policy).to be_eql(true)
  end

  it "confirms error is caught when policy can't be deleted" do
    bucket_policy.client.stub_responses(:delete_bucket_policy, "TestError")
    expect(wrapper.delete_policy).to be_eql(false)
  end
end
