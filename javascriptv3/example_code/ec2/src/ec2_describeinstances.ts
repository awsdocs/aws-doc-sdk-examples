/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-managing-instances.html

Purpose:
ec2_describeinstances.ts demonstrates how to retrieve information about one or more Amazon EC2 instances.

Inputs (replace in code):
- REGION

Running the code:
ts-node ec2_describeinstances.ts
 */

// snippet-start:[ec2.JavaScript.Instances.describeInstancesV3]
// Import required AWS SDK clients and commands for Node.js
const { EC2Client, DescribeInstancesCommand } = require("@aws-sdk/client-ec2");

// Set the AWS region
const REGION = "REGION"; //e.g. "us-east-1"

// Create EC2 service object
const ec2client = new EC2Client(REGION);

const run = async () => {
  try {
    const data = await ec2client.send(new DescribeInstancesCommand({}));
    console.log("Success", JSON.stringify(data));
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.Instances.describeInstancesV3]

