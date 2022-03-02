# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../object_put_sse"

describe ObjectPutSseWrapper do
  let(:object) { Aws::S3::Object.new("test-bucket", "test-key", stub_responses: true) }
  let(:wrapper) { ObjectPutSseWrapper.new(object) }

  it "confirms the object was put" do
    object.client.stub_responses(:put_object)
    success = wrapper.put_object_encrypted("Test content", "TEST")
    expect(success).to be_eql(true)
  end

  it "confirms error is caught when object can't be put" do
    object.client.stub_responses(:put_object, "TestError")
    expect(wrapper.put_object_encrypted("Test content", "TEST")).to be_eql(false)
  end
end
