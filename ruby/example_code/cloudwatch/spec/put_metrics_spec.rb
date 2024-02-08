# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "../put_metrics"
require "rspec"

describe "Alarm metrics added successfully", :integ do
  let(:alarm_name) { "ObjectsInBucket" }
  let(:metric_namespace) { "SITE/TRAFFIC" }
  let(:cloudwatch_client) { Aws::CloudWatch::Client.new }
  let(:cw) { AlarmMetricsManager.new(cloudwatch_client) }

  it "Lists CloudWatch metrics for a namespace successfully" do
    puts "Metrics for namespace '#{metric_namespace}':"
    cw.list_metrics_for_namespace(metric_namespace)
  end

  it "Adds datapoints to CloudWatch metric successfully" do
    # Add three datapoints.
    cw.datapoint_added_to_metric?(
      metric_namespace,
      "UniqueVisitors",
      "SiteName",
      "example.com",
      5_885.0,
      "Count"
    )

    cw.datapoint_added_to_metric?(
      metric_namespace,
      "UniqueVisits",
      "SiteName",
      "example.com",
      8_628.0,
      "Count"
    )

    cw.datapoint_added_to_metric?(
      metric_namespace,
      "PageViews",
      "PageURL",
      "example.html",
      18_057.0,
      "Count"
    )
  end
end