# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../manage_alarms"
require "rspec"

describe "#alarm_created_or_updated?", :integ do
  let(:alarm_name) { "ObjectsInBucket" }
  let(:alarm_description) { "Objects exist in this bucket for more than 1 day." }
  let(:metric_name) { "NumberOfObjects" }
  let(:alarm_actions) { ["arn:aws:sns:us-east-1:111111111111:Default_CloudWatch_Alarms_Topic"] }
  let(:namespace) { "AWS/S3" }
  let(:statistic) { "Average" }
  let(:dimensions) do
    [
      {
        name: "BucketName",
        value: "doc-example-bucket"
      },
      {
        name: "StorageType",
        value: "AllStorageTypes"
      }
    ]
  end
  let(:period) { 86_400 }
  let(:unit) { "Count" }
  let(:evaluation_periods) { 1 }
  let(:threshold) { 1 }
  let(:comparison_operator) { "GreaterThanThreshold" }
  let(:cloudwatch_client) do
    Aws::CloudWatch::Client.new
  end
  let(:manager) { CloudWatchAlarmManager.new(cloudwatch_client) }

  it "lists existing metric alarms" do
    manager.describe_metric_alarms
  end

  it "creates, updates, enables, disables, and deletes successfully" do
    alarm_name = "ObjectsInBucket"
    # Parameters for create_or_update_alarm are passed as a hash for readability
    alarm_opts = {
      alarm_description: "Objects exist in this bucket for more than 1 day.",
      metric_name: "NumberOfObjects",
      alarm_actions: ["arn:aws:sns:us-east-1:111111111111:Default_CloudWatch_Alarms_Topic"],
      namespace: "AWS/S3",
      statistic: "Average",
      dimensions: [{name: "BucketName", value: "doc-example-bucket"}, {name: "StorageType", value: "AllStorageTypes"}],
      period: 86400,
      unit: "Count",
      evaluation_periods: 1,
      threshold: 1,
      comparison_operator: "GreaterThanThreshold"
    }
    [:create_or_update, :disable, :enable, :delete].each { |action|
      manager.manage_alarm(action: action, alarm_name: alarm_name, **alarm_opts)
    }
  end
end
