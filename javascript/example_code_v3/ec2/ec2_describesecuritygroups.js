/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ec2-example-security-groups.html.

Purpose:
ec2_describesecuritygroups.js demonstrates how to retrieve information about one or more security groups.

Inputs:
- REGION (into command line  below)
- SECURITY_GROUP_ID (into command line  below)

Running the code:
node ec2_describesecuritygroups.js REGION SECURITY_GROUP_ID

 */
// snippet-start:[ec2.JavaScript.v3.SecurityGroups.describeSecurityGroups]
async function run(){
   try {
      const {EC2, DescribeSecurityGroupsCommand} = require("@aws-sdk/client-ec2");
      const region = process.argv[2];
      const ec2client = await new EC2(region);
      var params = {GroupIds: [process.argv[3]]};
      const data = await ec2client.send(new DescribeSecurityGroupsCommand(params))
      console.log("Success", JSON.stringify(data.SecurityGroups));
   }
   catch(err){
      console.log("Error", err);
   }
};
run();


// snippet-end:[ec2.JavaScript.v3.SecurityGroups.describeSecurityGroups]
exports.run = run;
