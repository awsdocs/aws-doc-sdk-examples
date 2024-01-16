// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[connect.javascript.getCurrentMetricData]
/* 
This code uses callbacks to handle asynchronous function responses.
It currently demonstrates using an async-await pattern. 
AWS supports both the async-await and promises patterns.
For more information, see the following: 
https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/async_function
https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Using_promises
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/calling-services-asynchronously.html
https://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-handler.html 
*/
const aws = require("aws-sdk");
const connect = new aws.Connect({ apiVersion: "2017-08-08" });

const params = {
  CurrentMetrics: [
    {
      Name: "AGENTS_AVAILABLE",
      Unit: "COUNT",
    },
  ],
  Filters: {
    Channels: ["VOICE"],
    Queues: ["yourQueueID"], // replace 'yourQueueID' with your Queue ID
  },
  InstanceId: "yourInstanceID", // replace 'yourInstanceID' with your Instance ID
};

exports.handler = async (event) => {
  const data = await connect.getCurrentMetricData(params).promise();
  console.log(data);
  var str = JSON.stringify(data, null, 2);
  console.log(str);
};
// snippet-end:[connect.javascript.getCurrentMetricData]
