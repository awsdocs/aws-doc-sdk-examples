/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-sending-events.html.

Purpose:
cwe_putevents.js demonstrates how to send custom events to Amazon CloudWatch Events so they can be matched with rules.

Inputs (replace in code):
- REGION
- RESOURCE_ARN

Running the code:
node cwe_putevents.js
*/
// snippet-start:[cwEvents.JavaScript.cwe.putEventsV3]

// Import required AWS SDK clients and commands for Node.js
const { PutEventsCommand } = require("@aws-sdk/client-cloudwatch-events");
const { cweClient } = require("./libs/cweClient");

// Set the parameters
const params = {
  Entries: [
    {
      Detail: '{ "key1": "value1", "key2": "value2" }',
      DetailType: "appRequestSubmitted",
      Resources: [
        "RESOURCE_ARN", //RESOURCE_ARN
      ],
      Source: "com.company.app",
    },
  ],
};

const run = async () => {
  try {
    const data = await cweClient.send(new PutEventsCommand(params));
    console.log("Success, event sent; requestID:", data.$metadata.requestId);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cwEvents.JavaScript.cwe.putEventsV3]
// For unit tests only.
// module.exports ={run, params};
