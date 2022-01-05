# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require 'rspec'
require_relative '../bucket_put_encryption'

describe S3Wrapper do
  let(:client) { Aws::S3::Client.new(stub_responses: true) }
  let(:wrapper) { S3Wrapper.new(client) }

  it 'confirms the encryption state was set' do
    client.stub_responses(:put_bucket_encryption)
    expect(wrapper.set_encryption('test-bucket')).to be_eql(true)
  end

  it "confirms error is caught when objects can't be listed" do
    client.stub_responses(:put_bucket_encryption, StandardError)
    expect(wrapper.set_encryption('test-bucket')).to be_eql(false)
  end
end



