# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative "manage_alarms"

def run
  cloudwatch_client = Aws::CloudWatch::Client.new(region: "us-east-1")
  manager = CloudWatchAlarmManager.new(cloudwatch_client)
  alarm_name = "ObjectsInBucket"
  manager.describe_metric_alarms

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

run if $PROGRAM_NAME == __FILE__
