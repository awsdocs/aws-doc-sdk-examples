/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ec2-example-security-groups.html.

Purpose:
ec2_describeresionsandzones.js demonstrates how to retrieve information about Amazon EC2 Regions and Availability Zones.

Inputs:
- REGION (into command line  below)

Running the code:
node ec2_describeresionsandzones.js REGION
*/
// snippet-start:[ec2.JavaScript.v3.Regions.describeRegions]
async function run(){
  try {
    const params = {};
    const {EC2, DescribeRegionsCommand} = require("@aws-sdk/client-ec2");
    const region = process.argv[2];
    const ec2client = await new EC2(region);
    const data = await ec2client.send(new DescribeRegionsCommand(params))
    console.log("Availability Zones: ", data.Regions);
  }
  catch(err){
    console.log("Error", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.v3.Regions.describeRegions]
exports.run = run;
