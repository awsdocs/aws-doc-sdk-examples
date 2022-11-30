# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../show_alarms"
require "rspec"

describe "#describe_metric_alarms", :integ do
  let(:metric_namespace) { "SITE/TRAFFIC" }
  let(:metric_name) { "UniqueVisitors" }
  let(:dimension_name) { "SiteName" }
  let(:dimension_value) { "example.com" }
  let(:metric_value) { 5_885.0 }
  let(:metric_unit) { "Count" }
  let(:cloudwatch_client) do
    Aws::CloudWatch::Client.new(
      stub_responses: {
        describe_alarms: {
          metric_alarms: [
            {
              alarm_name: "ObjectsInBucket",
              state_value: "INSUFFICIENT_DATA",
              state_reason: "Unchecked: Initial alarm creation",
              metric_name: "NumberOfObjects",
              namespace: "AWS/S3",
              statistic: "Average",
              period: 86_400,
              unit: "Count",
              evaluation_periods: 1,
              threshold: 1.0,
              comparison_operator: "GreaterThanThreshold",
              ok_actions: [
                "arn:aws:sns:us-east-1:111111111111:Default_CloudWatch_Alarms_Topic"
              ],
              dimensions: [
                {
                  name: "BucketName",
                  value: "doc-example-bucket"
                },
                {
                    name: "StorageType",
                    value: "AllStorageTypes"
                }
              ]
            }
          ]
        }
      }
    )
  end

  it "shows information about metric alarms" do
    expect { describe_metric_alarms(cloudwatch_client) }.not_to raise_error
  end
end
