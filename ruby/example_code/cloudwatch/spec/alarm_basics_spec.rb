# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../alarm_basics"
require "rspec"

describe "#list_alarms", :integ do
  let(:cloudwatch_client) do
    Aws::CloudWatch::Client.new(
      stub_responses: {
        describe_alarms: {
          metric_alarms: [
            {
              alarm_name: "ObjectsInBucket"
            }
          ]
        }
      }
    )
  end

  it "lists information about alarms" do
    expect { list_alarms(cloudwatch_client) }.not_to raise_error
  end
end

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
    Aws::CloudWatch::Client.new(
      stub_responses: {
        put_metric_alarm: {}
      }
    )
  end

  it "creates or updates an alarm" do
    expect(
      alarm_created_or_updated?(
        cloudwatch_client,
        alarm_name,
        alarm_description,
        metric_name,
        alarm_actions,
        namespace,
        statistic,
        dimensions,
        period,
        unit,
        evaluation_periods,
        threshold,
        comparison_operator
      )
    ).to be(true)
  end
end

describe "#alarm_deleted?", :integ do
  let(:alarm_name) { "ObjectsInBucket" }
  let(:cloudwatch_client) do
    Aws::CloudWatch::Client.new(
      stub_responses: {
        delete_alarms: {}
      }
    )
  end

  it "deletes an alarm" do
    expect(
      alarm_deleted?(cloudwatch_client, alarm_name)
    ).to be(true)
  end
end
