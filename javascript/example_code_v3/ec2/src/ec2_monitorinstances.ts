/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-managing-instances.html

Purpose:
ec2_monitorinstances.ts demonstrates how to enable detailed monitoring for Amazon EC2 instances.

Inputs (replace in code):
- REGION
- INSTANCE_ID
- STATE: 'ON' or 'OFF'

Running the code:
ts-node ec2_monitorinstances.ts
 */

// snippet-start:[ec2.JavaScript.Instances.monitorInstancesV3]

// Import required AWS SDK clients and commands for Node.js
const {
  EC2,
  MonitorInstancesCommand,
  UnmonitorInstancesCommand,
} = require("@aws-sdk/client-ec2");

// Set the AWS region
const REGION = "region"; //e.g. "us-east-1"

// Create EC2 service object
const ec2client = new EC2(REGION);

// Set the parameters
const params = { InstanceIds: "INSTANCE_ID" }; // INSTANCE_ID
const state = "STATE"; // STATE; i.e., 'ON' or 'OFF'

const run = async () => {
  if (process.argv[4].toUpperCase() === "ON") {
    try {
      const data = await ec2client.send(new MonitorInstancesCommand(params));
      console.log("Success", data.InstanceMonitorings);
    } catch (err) {
      console.log("Error", err);
    }
  } else if (process.argv[4].toUpperCase() === "OFF") {
    try {
      const data = await ec2client.send(new UnmonitorInstancesCommand(params));
      console.log("Success", data.InstanceMonitorings);
    } catch (err) {
      console.log("Error", err);
    }
  }
};
run();
// snippet-end:[ec2.JavaScript.Instances.monitorInstancesV3]
//for unit tests only
// module.exports = {run};
