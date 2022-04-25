# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../bucket_put_website"

describe BucketWebsiteWrapper do
  let(:bucket_name) { "test-bucket" }
  let(:bucket_website) { Aws::S3::BucketWebsite.new(bucket_name, stub_responses: true) }
  let(:wrapper) { BucketWebsiteWrapper.new(bucket_website) }

  it "confirms the website was set" do
    bucket_website.client.stub_responses(:put_bucket_website)
    expect(wrapper.set_website("index-test", "error-test")).to be_eql(true)
  end

  it "confirms error is caught when the website can't be set" do
    bucket_website.client.stub_responses(:put_bucket_website, "TestError")
    expect(wrapper.set_website("index-test", "error-test")).to be_eql(false)
  end
end
