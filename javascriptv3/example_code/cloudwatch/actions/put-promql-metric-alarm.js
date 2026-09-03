// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[javascript.v3.cloudwatch.actions.PutMetricAlarm.PromQL]
import { PutMetricAlarmCommand } from "@aws-sdk/client-cloudwatch";
import { client } from "../libs/client.js";

// Create an alarm that evaluates a PromQL query over OpenTelemetry metrics.
//
// A PromQL alarm differs from a classic metric alarm in a few ways. The query can match
// many series at once, and each matching series is tracked separately as a contributor
// (see describe-alarm-contributors.js). Instead of counting breaching periods, you
// specify durations: a contributor moves to ALARM after it breaches continuously for
// PendingPeriod seconds, and back to OK after it stops breaching for RecoveryPeriod
// seconds. A PromQL alarm starts in OK rather than INSUFFICIENT_DATA.
//
// EvaluationCriteria is a union and is mutually exclusive with the classic MetricName
// and Metrics parameters. When you use it you must also set EvaluationInterval, and you
// must not set Period, Statistic, Threshold, ComparisonOperator, EvaluationPeriods,
// DatapointsToAlarm, or TreatMissingData.
const run = async () => {
  const command = new PutMetricAlarmCommand({
    AlarmName: process.env.CLOUDWATCH_ALARM_NAME, // Set CLOUDWATCH_ALARM_NAME to the name of the alarm to create.
    AlarmDescription: "Average CPU over 80% per host for the checkout service.",
    EvaluationCriteria: {
      PromQLCriteria: {
        // The comparison belongs in the query itself. There is no separate Threshold.
        Query:
          'avg by (host_name) (cpu_utilization_percent{service_name="checkout"}) > 80',
        PendingPeriod: 300,
        RecoveryPeriod: 120,
      },
    },
    // How often to run the query, in seconds. Valid values are 10, 20, 30, and any
    // multiple of 60, up to 3600.
    EvaluationInterval: 30,
    ActionsEnabled: false,
  });

  try {
    return await client.send(command);
  } catch (err) {
    console.error(err);
  }
};

export default run();
// snippet-end:[javascript.v3.cloudwatch.actions.PutMetricAlarm.PromQL]
