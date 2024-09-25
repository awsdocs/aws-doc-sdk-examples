# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

require_relative '../alarm_actions'
require 'rspec'

describe '#alarm_created_or_updated?', :quarantine do
  let(:alarm_name) { 'ObjectsInBucket' }
  let(:alarm_description) { 'Objects exist in this bucket for more than 1 day.' }
  let(:metric_name) { 'NumberOfObjects' }
  let(:alarm_actions) { ['arn:aws:sns:us-east-1:111111111111:Default_CloudWatch_Alarms_Topic'] }
  let(:namespace) { 'AWS/S3' }
  let(:statistic) { 'Average' }
  let(:dimensions) do
    [
      {
<<<<<<< HEAD
        name: "BucketName",
        value: "amzn-s3-demo-bucket"
=======
        name: 'BucketName',
        value: 'doc-example-bucket'
>>>>>>> 999c6133e (fixes)
      },
      {
        name: 'StorageType',
        value: 'AllStorageTypes'
      }
    ]
  end
  let(:period) { 86_400 }
  let(:unit) { 'Count' }
  let(:evaluation_periods) { 1 }
  let(:threshold) { 1 }
  let(:comparison_operator) { 'GreaterThanThreshold' }
  let(:cloudwatch_client) do
    Aws::CloudWatch::Client.new(
      stub_responses: {
        put_metric_alarm: {}
      }
    )
  end

  it 'creates or updates an alarm' do
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

describe '#alarm_actions_disabled?', :integ do
  let(:alarm_name) { 'ObjectsInBucket' }
  let(:cloudwatch_client) do
    Aws::CloudWatch::Client.new(
      stub_responses: {
        disable_alarm_actions: {}
      }
    )
  end

  it 'disables actions for an alarm' do
    expect(
      alarm_actions_disabled?(cloudwatch_client, alarm_name)
    ).to be(true)
  end
end
