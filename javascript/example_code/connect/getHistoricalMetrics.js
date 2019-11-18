/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

// snippet-sourcedescription:[getRealtimeMetrics.js demonstrates how to .]
// snippet-service:[connect]
// snippet-keyword:[JavaScript]
// snippet-sourcesyntax:[javascript]
// snippet-keyword:[Amazon Connect]
// snippet-keyword:[Code Sample]
// snippet-keyword:[getMetricData]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-10-17]
// snippet-sourceauthor:[Doug-AWS]
// snippet-start:[connect.javascript.getMetricData]
const aws = require('aws-sdk')
const connect = new aws.Connect()

const params = {
  InstanceId: 'Provide your connect instance id',
  EndTime: 1571184000, // enter a new end time
  StartTime: 1571153400, // enter a new start time
  Filters: {
    Queues: ['Provide your connect instance queue id'],
    Channels: ['VOICE']
  },
  HistoricalMetrics: [
    {
      Name: 'AFTER_CONTACT_WORK_TIME',
      Unit: 'SECONDS',
      Statistic: 'AVG'
    },
    {
      Name: 'CONTACTS_QUEUED',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'CONTACTS_HANDLED',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'HANDLE_TIME',
      Unit: 'SECONDS',
      Statistic: 'AVG'
    },
    {
      Name: 'CONTACTS_TRANSFERRED_OUT',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'CONTACTS_MISSED',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'OCCUPANCY',
      Unit: 'PERCENT',
      Statistic: 'AVG'
    },
    {
      Name: 'QUEUED_TIME',
      Unit: 'SECONDS',
      Statistic: 'MAX'
    },
    {
      Name: 'HOLD_TIME',
      Unit: 'SECONDS',
      Statistic: 'AVG'
    },
    {
      Name: 'SERVICE_LEVEL',
      Threshold: {
        Comparison: 'LT',
        ThresholdValue: 60.0
      },
      Unit: 'PERCENT',
      Statistic: 'AVG'
    },
    {
      Name: 'SERVICE_LEVEL',
      Threshold: {
        Comparison: 'LT',
        ThresholdValue: 120.0
      },
      Unit: 'PERCENT',
      Statistic: 'AVG'
    },
    {
      Name: 'SERVICE_LEVEL',
      Threshold: {
        Comparison: 'LT',
        ThresholdValue: 30.0
      },
      Unit: 'PERCENT',
      Statistic: 'AVG'
    },
    {
      Name: 'CONTACTS_ABANDONED',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'CONTACTS_CONSULTED',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'CONTACTS_AGENT_HUNG_UP_FIRST',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'CONTACTS_HANDLED_INCOMING',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'CONTACTS_HANDLED_OUTBOUND',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'CONTACTS_HOLD_ABANDONS',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'CONTACTS_TRANSFERRED_IN',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'CONTACTS_TRANSFERRED_IN_FROM_QUEUE',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'CONTACTS_TRANSFERRED_OUT_FROM_QUEUE',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'CALLBACK_CONTACTS_HANDLED',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'API_CONTACTS_HANDLED',
      Unit: 'COUNT',
      Statistic: 'SUM'
    },
    {
      Name: 'ABANDON_TIME',
      Unit: 'SECONDS',
      Statistic: 'AVG'
    },
    {
      Name: 'QUEUE_ANSWER_TIME',
      Unit: 'SECONDS',
      Statistic: 'AVG'
    },
    {
      Name: 'INTERACTION_TIME',
      Unit: 'SECONDS',
      Statistic: 'AVG'
    },
    {
      Name: 'INTERACTION_AND_HOLD_TIME',
      Unit: 'SECONDS',
      Statistic: 'AVG'
    }
  ]
}

exports.handler = async (event) => {
  const result = await connect.getMetricData(params, function (err, data) {
    if (err) {
      console.log(err, err.stack)
    } else {
      console.log(data)
    }
  }).promise()

  var metrics = result.MetricResults
  console.log(metrics[0].Collections)
}
// snippet-end:[connect.javascript.getMetricData]
