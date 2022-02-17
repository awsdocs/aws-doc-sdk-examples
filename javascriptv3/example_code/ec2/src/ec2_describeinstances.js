/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-managing-instances.html

Purpose:
ec2_describeinstances.js demonstrates how to retrieve information about one or more Amazon EC2 instances.

Running the code:
node ec2_describeinstances.js
 */

// snippet-start:[ec2.JavaScript.Instances.describeInstancesV3]
// Import required AWS SDK clients and commands for Node.js
import { DescribeInstancesCommand } from "@aws-sdk/client-ec2";
import { ec2Client } from "./libs/ec2Client";
const run = async () => {
  try {
    const data = await ec2Client.send(new DescribeInstancesCommand({}));
    console.log("Success", JSON.stringify(data));
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.Instances.describeInstancesV3]
// For unit tests only.
// module.exports ={run, params};
