/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-sending-events.html.

Purpose:
cwe_putevents.ts demonstrates how to send custom events to Amazon CloudWatch Events so they can be matched with rules.

Inputs (replace in code):
- REGION
- RESOURCE_ARN

Running the code:
ts-node cwe_putevents.ts
*/
// snippet-start:[cwEvents.JavaScript.cwe.putEventsV3]

// Import required AWS SDK clients and commands for Node.js
const {
  CloudWatchEventsClient,
  PutEventsCommand,
} = require("@aws-sdk/client-cloudwatch-events");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

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

// Create CloudWatch service object
const cwevents = new CloudWatchEventsClient({ region: REGION });

const run = async () => {
  try {
    const data = await cwevents.send(new PutEventsCommand(params));
    console.log("Success, event sent; requestID:", data.$metadata.requestId);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cwEvents.JavaScript.cwe.putEventsV3]

