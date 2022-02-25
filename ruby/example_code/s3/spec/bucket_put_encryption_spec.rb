# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../bucket_put_encryption"

describe BucketEncryptionWrapper do
  let(:client) { Aws::S3::Client.new(stub_responses: true) }
  subject { BucketEncryptionWrapper.new(client) }

  it "confirms the encryption state was set" do
    client.stub_responses(:put_bucket_encryption)
    expect(subject.set_encryption("test-bucket")).to be_eql(true)
  end

  it "confirms error is caught when objects can't be listed" do
    client.stub_responses(:put_bucket_encryption, "TestError")
    expect(subject.set_encryption("test-bucket")).to be_eql(false)
  end
end
