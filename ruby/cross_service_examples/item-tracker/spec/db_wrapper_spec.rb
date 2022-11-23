# frozen_string_literal: true

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require "multi_json"
require "yaml"
require "json"
require "rspec"
require "aws-sdk-rdsdataservice"
require "aws-sdk-ses"
require_relative("../db_wrapper")
require_relative("../report")

describe "CRUD commands on Aurora" do
  client = Aws::RDSDataService::Client.new
  config = YAML.safe_load(File.open(File.join(File.dirname(__FILE__), "./../helpers", "config.yml")))
  let(:wrapper) { DBWrapper.new(config, client) }

  it "gets the table name" do
    table = wrapper.get_table_name
    expect(table).to be_an_instance_of(String)
  end

  it "adds a new item" do
    item_data = {
      description: "User research",
      guide: "dotnet",
      status: "active",
      username: "krodgers"
    }
    id = wrapper.add_work_item(item_data)
    expect(id).to be_an_instance_of(Integer)
  end

  it "gets a specific item" do
    data = wrapper.get_work_items(1)
    expect(data[0]).to be_an_instance_of(Hash)
  end

  it "gets multiple items" do
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

  it "archives a specific item" do
    id = wrapper.archive_work_item(5)
    expect(id).to be_an_instance_of(TrueClass)
  end

  it "gets archived items" do
    data = wrapper.get_work_items(nil, false)
    expect(data[0]).to be_an_instance_of(Hash)
  end

  it "make report" do
    report = Report.new(wrapper, config["recipient_email"], Aws::SES::Client.new)
    report.post_report(config["recipient_email"])
  end
end
