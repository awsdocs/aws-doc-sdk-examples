# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "put_metrics"

def run
  metric_namespace = "SITE/TRAFFIC"

  cloudwatch_client = Aws::CloudWatch::Client.new(region: region)
  cw = AlarmMetricsManager.new(cloudwatch_client)

  # Add three datapoints.
  puts "Continuing..." unless cw.datapoint_added_to_metric?(
    metric_namespace,
    "UniqueVisitors",
    "SiteName",
    "example.com",
    5_885.0,
    "Count"
  )

  puts "Continuing..." unless cw.datapoint_added_to_metric?(
    metric_namespace,
    "UniqueVisits",
    "SiteName",
    "example.com",
    8_628.0,
    "Count"
  )

  puts "Continuing..." unless cw.datapoint_added_to_metric?(
    metric_namespace,
    "PageViews",
    "PageURL",
    "example.html",
    18_057.0,
    "Count"
  )

  puts "Metrics for namespace '#{metric_namespace}':"
  cw.list_metrics_for_namespace(metric_namespace)
end

run_me if $PROGRAM_NAME == __FILE__