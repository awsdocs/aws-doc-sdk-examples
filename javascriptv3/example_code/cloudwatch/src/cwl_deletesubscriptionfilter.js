/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cloudwatch-examples-subscriptions.html.

Purpose:
cwl_deletesubscriptionfilter.js demonstrates how to delete an Amazon CloudWatch Logs filter.

Inputs :
- FILTER
- LOG_GROUP

Running the code:
node cwl_deletesubscriptionfilter.js
*/
// snippet-start:[cwLogs.JavaScript.cwl.deleteSubscriptionFilterV3]

// Import required AWS SDK clients and commands for Node.js
import {
  DeleteSubscriptionFilterCommand,
} from "@aws-sdk/client-cloudwatch-logs";
import { cwlClient } from "./libs/cwlClient";

// Set the parameters
const params = {
  filterName: "FILTER", //FILTER
  logGroupName: "LOG_GROUP", //LOG_GROUP
};

const run = async () => {
  try {
    const data = await cwlClient.send(
      new DeleteSubscriptionFilterCommand(params)
    );
    console.log(
      "Success, subscription filter deleted; requestId: ",
      data.$metadata.requestId
    );
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[cwLogs.JavaScript.cwl.deleteSubscriptionFilterV3]
// For unit tests only.
// module.exports ={run, params};
