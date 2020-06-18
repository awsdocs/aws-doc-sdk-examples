/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ec2-example-managing-instances.html.

Purpose:
ec2_describeinstances.js demonstrates how to retrieve information about one or more Amazon EC2 instances.

Inputs:
- REGION (into command line  below)

Running the code:
node ec2_describeinstances.js REGION
 */

// snippet-start:[ec2.JavaScript.v3.Instances.describeInstances]
async function run(){
  try {
    const region = process.argv[2];
    const {EC2, DescribeInstancesCommand} = require("@aws-sdk/client-ec2");
    const ec2client = await new EC2(region);
    const data = await ec2client.send(new DescribeInstancesCommand({}))
    console.log("Success", JSON.stringify(data));
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.v3.Instances.describeInstances]
exports.run = run;
