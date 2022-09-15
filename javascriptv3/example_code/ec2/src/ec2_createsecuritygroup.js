/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-security-groups.html

Purpose:
ec2_createsecuritygroup.js demonstrates how to create a security group for an Amazon EC2 instance.

Inputs (replace in code):
- KEY_PAIR_NAME
- DESCRIPTION
- SECURITY_GROUP_NAME
- SECURITY_GROUP_ID

Running the code:
node ec2_createsecuritygroup.js
 */
// snippet-start:[ec2.JavaScript.SecurityGroups.createSecurityGroupV3]
// Import required AWS SDK clients and commands for Node.js
import {
  DescribeVpcsCommand,
  CreateSecurityGroupCommand,
  AuthorizeSecurityGroupIngressCommand,
} from "@aws-sdk/client-ec2";
import { ec2Client } from "./libs/ec2Client";

// Set the parameters
const params = { KeyName: "KEY_PAIR_NAME" }; //KEY_PAIR_NAME

// Variable to hold a ID of a VPC
let vpc = null;

const run = async () => {
  try {
    const data = await ec2Client.send(new DescribeVpcsCommand(params));
    vpc = data.Vpcs[0].VpcId;
    return data;
  } catch (err) {
    console.log("Error", err);
  }
  try {
    const paramsSecurityGroup = {
      Description: "DESCRIPTION", //DESCRIPTION
      GroupName: "SECURITY_GROUP_NAME", // SECURITY_GROUP_NAME
      VpcId: vpc,
    };
    const data = await ec2Client.send(
      new CreateSecurityGroupCommand(paramsSecurityGroup)
    );
    const SecurityGroupId = data.GroupId;
    console.log("Success", SecurityGroupId);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
  try {
    const paramsIngress = {
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
    const data = await ec2Client.send(
      new AuthorizeSecurityGroupIngressCommand(paramsIngress)
    );
    console.log("Ingress Successfully Set", data);
    return data;
  } catch (err) {
    console.log("Cannot retrieve a VPC", err);
  }
};
run();
// snippet-end:[ec2.JavaScript.SecurityGroups.createSecurityGroupV3]
// For unit tests only.
// module.exports ={run, params};
