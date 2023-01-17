/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  DeleteAlarmsCommand,
  DescribeAlarmsCommand,
  GetMetricDataCommand,
  PutMetricAlarmCommand,
} from "@aws-sdk/client-cloudwatch";
import { client } from "./client.js";

export const createAlarm = async (name, instanceId) => {
  const command = new PutMetricAlarmCommand({
    AlarmName: name,
    ComparisonOperator: "GreaterThanThreshold",
    EvaluationPeriods: 1,
    MetricName: "CPUUtilization",
    Namespace: "AWS/EC2",
    Period: 60,
    Statistic: "Average",
    Threshold: 70.0,
    ActionsEnabled: false,
    AlarmDescription: "Alarm when server CPU exceeds 70%",
    Dimensions: [
      {
        Name: "InstanceId",
        Value: instanceId,
      },
    ],
    Unit: "Percent",
  });

  await client.send(command);
};

export const deleteAlarms = async (alarmNames) => {
  const command = new DeleteAlarmsCommand({ AlarmNames: [alarmNames] });
  return client.send(command);
};

export const describeAlarm = async (alarmName) => {
  const command = new DescribeAlarmsCommand({ AlarmNames: [alarmName] });
  const { MetricAlarms } = await client.send(command);
  return MetricAlarms[0];
};

/**
 *
 * @param {string} id uuid
 * @returns
 */
export const getMetricData = async (id) => {
  const oneHourInMs = 1000 * 60 * 60;
  const now = new Date();
  const oneHourAgo = new Date(Date.now() - oneHourInMs);
  const command = new GetMetricDataCommand({
    StartTime: oneHourAgo,
    EndTime: now,
    MetricDataQueries: [
      {
        Id: id,
        MetricStat: {
          Metric: {
            Namespace: "SITE/TRAFFIC",
            MetricName: "PAGES_VISITED",
            Dimensions: [
              {
                Name: "UNIQUE_PAGES",
                Value: "URLS",
              },
            ],
            Unit: "None",
          },
          Period: 60,
          Stat: "Sum",
        },
      },
    ],
  });

  return client.send(command);
};
