/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ec2-example-security-groups.html.

Purpose:
ec2_deletesecuritygroup.js demonstrates how to delete a security group from an Amazon EC2 instance.
Inputs:
- REGION (into command line below)
- SECURITY_GROUP_ID (into command line below)

Running the code:
node ec2_deletesecuritygroup.js REGION SECURITY_GROUP_ID
 */
// snippet-start:[ec2.JavaScript.v3.SecurityGroups.deleteSecurityGroup]
async function run(){
   try {
      const {EC2, DeleteSecurityGroupCommand} = require("@aws-sdk/client-ec2");
      const region = process.argv[2];
      const ec2client = await new EC2(region);
      const params = {GroupId: process.argv[3]};
      const data = await ec2client.send(new DeleteSecurityGroupCommand(params));
      console.log("Security Group Deleted");
   }
   catch(err){
      console.log("Error", err);
   }
};
run();
// snippet-end:[ec2.JavaScript.v3.SecurityGroups.deleteSecurityGroup]
exports.run = run;
