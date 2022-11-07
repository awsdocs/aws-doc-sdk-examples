# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require_relative "../metrics_basics"
require "rspec"

describe "#datapoint_added_to_metric?", :integ do
  let(:metric_namespace) { "SITE/TRAFFIC" }
  let(:metric_name) { "UniqueVisitors" }
  let(:dimension_name) { "SiteName" }
  let(:dimension_value) { "example.com" }
  let(:metric_value) { 5_885.0 }
  let(:metric_unit) { "Count" }
  let(:cloudwatch_client) do
    Aws::CloudWatch::Client.new(
      stub_responses: {
        put_metric_data: {}
      }
    )
  end

  it "adds a datapoint to a metric" do
    expect(
      datapoint_added_to_metric?(
        cloudwatch_client,
        metric_namespace,
        metric_name,
        dimension_name,
        dimension_value,
        metric_value,
        metric_unit
      )
    ).to eq(true)
  end
end

describe "list_metrics_for_namespace", :integ do
  let(:metric_namespace) { "SITE/TRAFFIC" }
  let(:cloudwatch_client) do
    Aws::CloudWatch::Client.new(
      stub_responses: {
        list_metrics: {
          metrics: [
            {
              metric_name: "UniqueVisitors",
              dimensions: [
                {
                  name: "SiteName",
                  value: "example.com"
                }
              ]
            }
          ]
        }
      }
    )
  end

  it "lists the metrics for a namespace" do
    expect {
      list_metrics_for_namespace(cloudwatch_client, metric_namespace)
    }.not_to raise_error
  end
end
