# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "rspec"
require_relative "../object_presigned_url_upload"

describe "object_presigned_url_upload" do
  let(:bucket) { Aws::S3::Bucket.new("test-bucket") }

  it "confirms the presigned URL is created" do
    url = get_presigned_url(bucket, "test-key")
    expect(url).not_to be_nil
  end
end
