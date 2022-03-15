# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../object_copy_encrypt"

describe ObjectCopyEncryptWrapper do
  let(:source_object) { Aws::S3::Object.new("test-source-bucket", "test-source-key", stub_responses: true) }
  let(:target_bucket) { Aws::S3::Bucket.new("test-target-bucket", stub_responses: true) }
  let(:wrapper) { ObjectCopyEncryptWrapper.new(source_object) }

  it "confirms the object was copied" do
    source_object.client.stub_responses(:copy_object)
    target_obj = wrapper.copy_object(target_bucket, "test-target-key", "ServerSideEncryption")
    expect(target_obj.key).to be_eql("test-target-key")
    expect(target_obj.server_side_encryption).to be_eql("ServerSideEncryption")
  end

  it "confirms error is caught when object can't be copied" do
    source_object.client.stub_responses(:copy_object, "TestError")
    expect(wrapper.copy_object(target_bucket, "test-target-key", "ServerSideEncryption")).to be_nil
  end
end
