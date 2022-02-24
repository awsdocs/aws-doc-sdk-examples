# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../object_get_encryption"

describe ObjectGetEncryptionWrapper do
  let(:object) { Aws::S3::Object.new("test-bucket", "test-key", stub_responses: true) }
  let(:wrapper) { ObjectGetEncryptionWrapper.new(object) }

  it "confirms the object encryption state was retrieved" do
    obj_data = object.client.stub_data(:get_object, { server_side_encryption: "TEST" })
    object.client.stub_responses(:get_object, obj_data)
    obj = wrapper.get_object
    expect(obj.server_side_encryption).to be_eql("TEST")
  end

  it "confirms error is caught when object can't be retrieved" do
    object.client.stub_responses(:get_object, "TestError")
    expect(wrapper.get_object).to be_nil
  end
end
