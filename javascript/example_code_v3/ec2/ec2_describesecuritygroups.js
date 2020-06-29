/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The pre-release version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-security-groups.html

Purpose:
ec2_describesecuritygroups.js demonstrates how to retrieve information about one or more security groups.

Inputs:
- REGION (into command line below)
- SECURITY_GROUP_ID (into command line below)

Running the code:
node ec2_describesecuritygroups.js REGION SECURITY_GROUP_ID

 */
// snippet-start:[ec2.JavaScript.v3.SecurityGroups.describeSecurityGroups]
// Import required AWS SDK clients and commands for Node.js
const {EC2, DescribeSecurityGroupsCommand} = require("@aws-sdk/client-ec2");
// Set the AWS region
const region = process.argv[2];
// Create EC2 service object
const ec2client = new EC2(region);
// Set the parameters
const params = {GroupIds: [process.argv[3]]};

async function run(){
   try {
      const data = await ec2client.send(new DescribeSecurityGroupsCommand(params))
      console.log("Success", JSON.stringify(data.SecurityGroups));
   }
   catch(err){
      console.log("Error", err);
   }
};
run();


// snippet-end:[ec2.JavaScript.v3.SecurityGroups.describeSecurityGroups]
exports.run = run; //for unit tests only
