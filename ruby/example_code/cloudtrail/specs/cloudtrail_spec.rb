# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../create_trail"
require_relative "../delete_trail"
require_relative "../describe_trails"
require_relative "../lookup_events"
require "rspec"

describe "TestTrail" do
  let(:s3_client) { Aws::S3::Client.new }
  let(:sts_client) { Aws::STS::Client.new }
  let(:cloudtrail_client) { Aws::CloudTrail::Client.new }
  let(:trail_name) { "example-code-trail-#{rand(10**4)}" }
  let(:bucket_name) { "example-code-bucket-#{rand(10**4)}" }

  it "create" do
    create_trail_example(s3_client, sts_client, cloudtrail_client, trail_name, bucket_name)
  end

  it "describe" do
    describe_trails_example(cloudtrail_client)
  end

  it "lookup" do
    lookup_events_example(cloudtrail_client)
  end

  it "delete" do
    delete_trail_example(cloudtrail_client, trail_name)
  end
end
