# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../object_get"

describe ObjectGetWrapper do
  let(:object) { Aws::S3::Object.new("test-bucket", "test-key", stub_responses: true) }
  let(:wrapper) { ObjectGetWrapper.new(object) }

  it "confirms the object was retrieved" do
    obj_data = object.client.stub_data(:get_object, { content_length: 100 })
    object.client.stub_responses(:get_object, obj_data)
    obj = wrapper.get_object("test-file-name.txt")
    expect(obj.content_length).to be_eql(100)
  end

  it "confirms error is caught when object can't be retrieved" do
    object.client.stub_responses(:get_object, "TestError")
    expect(wrapper.get_object("test-file-name.txt")).to be_nil
  end
end
