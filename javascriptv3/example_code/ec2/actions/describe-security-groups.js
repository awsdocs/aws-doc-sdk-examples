/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-security-groups.html

Purpose:
ec2_describesecuritygroups.js demonstrates how to retrieve information about one or more security groups.

Inputs (replace in code):
- SECURITY_GROUP_ID

Running the code:
node ec2_describesecuritygroups.js

 */
// snippet-start:[ec2.JavaScript.SecurityGroups.describeSecurityGroupsV3]

// Import required AWS SDK clients and commands for Node.js
import { DescribeSecurityGroupsCommand } from "@aws-sdk/client-ec2";
import { ec2Client } from "./libs/ec2Client";

// Set the parameters
const params = { GroupIds: ["SECURITY_GROUP_ID"] }; //SECURITY_GROUP_ID

const run = async () => {
  try {
    const data = await ec2Client.send(
      new DescribeSecurityGroupsCommand(params)
    );
    console.log("Success", JSON.stringify(data.SecurityGroups));
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[ec2.JavaScript.SecurityGroups.describeSecurityGroupsV3]
// For unit tests only.
module.exports ={run, params};
