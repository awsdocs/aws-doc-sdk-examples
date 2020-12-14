/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-managing-instances.html

Purpose:
ec2_rebootinstances.js demonstrates how to queue a reboot request for one or more Amazon EC2 instances.

Inputs (replace in code):
- REGION
- INSTANCE_ID

Running the code:
ts-node ec2_rebootinstances.ts
*/
// snippet-start:[ec2.JavaScript.Instances.rebootInstancesV3]
// Import required AWS SDK clients and commands for Node.js
const {
  EC2Client,
  RebootInstancesCommandInput,
} = require("@aws-sdk/client-ec2");

// Set the AWS region
const REGION = "region"; //e.g. "us-east-1"

// Create EC2 service object
const ec2client = new EC2Client({ region: REGION });

// Set the parameters
const params = { InstanceIds: "INSTANCE_ID" }; //INSTANCE_ID

const run = async () => {
  try {
    const data = await ec2client.send(new RebootInstancesCommandInput(params));
    console.log("Success", data.InstanceMonitorings);
  } catch (err) {
    console.log("Error", err);
  }
};
// snippet-end:[ec2.JavaScript.Instances.rebootInstancesV3]

