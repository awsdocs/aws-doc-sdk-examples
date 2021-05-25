/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-sending-events.html.

Purpose:
cwe_puttargets.js demonstrates how to add or update a target to an Amazon CloudWatch Events rule.

Inputs (replace in code):
- LAMBDA_FUNCTION_ARN

Running the code:
node cwe_puttargets.js
*/
// snippet-start:[cwEvents.JavaScript.cwe.putTargetsV3]

// Import required AWS SDK clients and commands for Node.js
// ES Modules import
import { PutTargetsCommand } from "@aws-sdk/client-cloudwatch-events";
// CommonJS import
// const { PutTargetsCommand } = require("@aws-sdk/client-cloudwatch-events");

// ES Modules import
import { cweClient } from "./libs/cweClient";
// CommonJS import
// const { cweClient } = require("./libs/cweClient");


// Set the parameters
const params = {
  Rule: "DEMO_EVENT",
  Targets: [
    {
      Arn: "LAMBDA_FUNCTION_ARN", //LAMBDA_FUNCTION_ARN
      Id: "myCloudWatchEventsTarget",
    },
  ],
};

const run = async () => {
  try {
    const data = await cweClient.send(new PutTargetsCommand(params));
    console.log("Success, target added; requestID: ", data.$metadata.requestId);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cwEvents.JavaScript.cwe.putTargetsV3]
// For unit tests only.
// module.exports ={run, params};
