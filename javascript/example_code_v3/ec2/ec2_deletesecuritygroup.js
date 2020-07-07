/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-security-groups.html

Purpose:
ec2_deletesecuritygroup.js demonstrates how to delete a security group from an Amazon EC2 instance.

Inputs (replace in code):
- REGION
- SECURITY_GROUP_ID

Running the code:
node ec2_deletesecuritygroup.js
 */
// snippet-start:[ec2.JavaScript.SecurityGroups.deleteSecurityGroupV3]

// Import required AWS SDK clients and commands for Node.js
const {EC2, DeleteSecurityGroupCommand} = require("@aws-sdk/client-ec2");
// Set the AWS region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = {GroupId: "SECURITY_GROUP_ID"}; //SECURITY_GROUP_ID

// Create EC2 service object
const ec2client = new EC2(REGION);

const run = async () => {
   try {
      const data = await ec2client.send(new DeleteSecurityGroupCommand(params));
      console.log("Security Group Deleted");
   }
   catch(err){
      console.log("Error", err);
   }
};
run();
// snippet-end:[ec2.JavaScript.SecurityGroups.deleteSecurityGroupV3]
exports.run = run; //for unit tests only
