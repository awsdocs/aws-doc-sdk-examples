# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../object_exists"

describe ObjectExistsWrapper do
  let(:object) { Aws::S3::Object.new("test-bucket", "test-key", stub_responses: true) }
  let(:wrapper) { ObjectExistsWrapper.new(object) }

  it "confirms the object exists" do
    object.client.stub_responses(:head_object, { status_code: 200, headers: {}, body: "body" })
    exists = wrapper.exists?
    expect(exists).to be_eql(true)
  end

  it "confirms error is caught when object can't be retrieved" do
    object.client.stub_responses(:head_object, "TestError")
    expect(wrapper.exists?).to be_eql(false)
  end
end
