# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../create_job"
require "rspec"

describe "TestElastictranscoder" do
  let(:transcoder_client) { Aws::ElasticTranscoder::Client.new }
  let(:s3_resource) { Aws::S3::Resource.new }
  let(:pipeline_name) { "transcoder-pipeline-#{rand(10**4)}" }

  it "create" do
    create_elastictranscoder_job(transcoder_client, s3_resource, pipeline_name)
  end

end
