# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../object_upload_file"

describe ObjectUploadFileWrapper do
  let(:object) { Aws::S3::Object.new("test-bucket", "test-key", stub_responses: true) }
  let(:wrapper) { ObjectUploadFileWrapper.new(object) }

  it "confirms the object was uploaded" do
    object.client.stub_responses(:put_object)
    success = wrapper.upload_file(__FILE__)
    expect(success).to be_eql(true)
  end

  it "confirms error is caught when object can't be uploaded" do
    object.client.stub_responses(:put_object, "TestError")
    expect(wrapper.upload_file(__FILE__)).to be_eql(false)
  end
end
