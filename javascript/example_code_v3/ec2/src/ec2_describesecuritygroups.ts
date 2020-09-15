/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-security-groups.html

Purpose:
ec2_describesecuritygroups.ts demonstrates how to retrieve information about one or more security groups.

Inputs (replace in code):
- REGION
- SECURITY_GROUP_ID

Running the code:
ts-node ec2_describesecuritygroups.ts

 */
// snippet-start:[ec2.JavaScript.SecurityGroups.describeSecurityGroupsV3]

// Import required AWS SDK clients and commands for Node.js
const { EC2, DescribeSecurityGroupsCommand } = require("@aws-sdk/client-ec2");

// Set the AWS region
const REGION = "region"; //e.g. "us-east-1"

// Create EC2 service object
const ec2client = new EC2(REGION);

// Set the parameters
const params = { GroupIds: ["SECURITY_GROUP_ID"] }; //SECURITY_GROUP_ID

const run = async () => {
  try {
    const data = await ec2client.send(
      new DescribeSecurityGroupsCommand(params)
    );
    console.log("Success", JSON.stringify(data.SecurityGroups));
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[ec2.JavaScript.SecurityGroups.describeSecurityGroupsV3]
//for unit tests only
export = {run};
