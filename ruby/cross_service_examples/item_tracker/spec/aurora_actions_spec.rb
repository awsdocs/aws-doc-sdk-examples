# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "yaml"
require "rspec"
require "aws-sdk-rdsdataservice"
require "aws-sdk-ses"
require_relative "../src/aurora"
require_relative "../src/report"

describe "CRUD commands on Aurora > " do
  rds_client = Aws::RDSDataService::Client.new
  ses_client = Aws::SES::Client.new
  config = YAML.safe_load(File.open(File.join(File.dirname(__FILE__), "./../env", "config.yml")))
  let(:wrapper) { AuroraActions.new(config, rds_client) }

  it "Adds a new item" do
    item_data = {
      description: "User research",
      guide: "dotnet",
      status: "active",
      username: "krodgers"
    }
    id = wrapper.add_work_item(item_data)
    expect(id).to be_an_instance_of(Integer)
  end

  it "Gets a specific item" do
    data = wrapper.get_work_items(1)
    expect(data[0]).to be_an_instance_of(Hash)
  end

  it "Gets multiple items" do
    item_data = {
      description: "Tech debt",
      guide: "python",
      status: "active",
      username: "jmayer",
    }
    wrapper.add_work_item(item_data)
    data = wrapper.get_work_items
    expect(data[0]).to be_an_instance_of(Hash)
  end

  it "Archives a specific item" do
    id = wrapper.archive_work_item(5)
    expect(id).to be_an_instance_of(Array)
    expect(id.empty?)
  end

  it "Gets archived items" do
    data = wrapper.get_work_items(nil, false)
    expect(data[0]).to be_an_instance_of(Hash)
  end

  it "Make report" do
    report = Report.new(wrapper, config["recipient_email"], ses_client)
    report.post_report(config["recipient_email"])
  end
end
