/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-security-groups.html

Purpose:
ec2_createsecuritygroup.ts demonstrates how to create a security group for an Amazon EC2 instance.

Inputs (replace in code):
- REGION
- KEY_PAIR_NAME
- DESCRIPTION
- SECURITY_GROUP_NAME
- SECURITY_GROUP_ID

Running the code:
ts-node ec2_createsecuritygroup.ts
 */
// snippet-start:[ec2.JavaScript.SecurityGroups.createSecurityGroupV3]

// Import required AWS SDK clients and commands for Node.js
const {
  EC2,
  DescribeVpcsCommand,
  CreateSecurityGroupCommand,
  AuthorizeSecurityGroupIngressCommand,
} = require("@aws-sdk/client-ec2");
// Set the AWS region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const params = { KeyName: "KEY_PAIR_NAME" }; //KEY_PAIR_NAME

// Variable to hold a ID of a VPC
const vpc = null;

// Create EC2 service object
const ec2client = new EC2(REGION);

const run = async () => {
  try {
    var data = await ec2client.send(new DescribeVpcsCommand(params));
    vpc = data.Vpcs[0].VpcId;
    var paramsSecurityGroup = {
      Description: "DESCRIPTION", //DESCRIPTION
      GroupName: "SECURITY_GROUP_NAME", // SECURITY_GROUP_NAME
      VpcId: vpc,
    };
  } catch (err) {
    console.log("Error", err);
  }
  try {
    var data = await ec2client.send(new CreateSecurityGroupCommand(params));
    const SecurityGroupId = data.GroupId;
    console.log("Success", SecurityGroupId);
    var paramsIngress = {
      GroupId: "SECURITY_GROUP_ID", //SECURITY_GROUP_ID
      IpPermissions: [
        {
          IpProtocol: "tcp",
          FromPort: 80,
          ToPort: 80,
          IpRanges: [{ CidrIp: "0.0.0.0/0" }],
        },
        {
          IpProtocol: "tcp",
          FromPort: 22,
          ToPort: 22,
          IpRanges: [{ CidrIp: "0.0.0.0/0" }],
        },
      ],
    };
  } catch (err) {
    console.log("Error", err);
  }
  try {
    var data = await ec2client.send(
      new AuthorizeSecurityGroupIngressCommand(paramsIngress)
    );
    console.log("Ingress Successfully Set", data);
  } catch (err) {
    console.log("Cannot retrieve a VPC", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.SecurityGroups.createSecurityGroupV3]
//for unit tests only
// module.exports = {run};
