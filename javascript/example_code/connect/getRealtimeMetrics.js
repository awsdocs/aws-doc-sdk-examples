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
// snippet-keyword:[getCurrentMetricData]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-10-17]
// snippet-sourceauthor:[Doug-AWS]
// snippet-start:[connect.javascript.getCurrentMetricData]
const aws = require('aws-sdk')
const connect = new aws.Connect({ apiVersion: '2017-08-08' })

const params = {
  CurrentMetrics: [{
    Name: 'AGENTS_AVAILABLE',
    Unit: 'COUNT'
  }],
  Filters: {
    Channels: ['VOICE'],
    Queues: ['yourQueueID'] // replace 'yourQueueID' with your Queue ID
  },
  InstanceId: 'yourInstanceID' // replace 'yourInstanceID' with your Instance ID
}

exports.handler = async (event) => {
  const data = await connect.getCurrentMetricData(params).promise()
  console.log(data)
  var str = JSON.stringify(data, null, 2)
  console.log(str)
}
// snippet-end:[connect.javascript.getCurrentMetricData]
