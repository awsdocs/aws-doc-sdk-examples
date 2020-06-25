/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/ec2-example-security-groups.html

Purpose:
ec2_createsecuritygroup.js demonstrates how to create a security group for an Amazon EC2 instance.

Inputs:
- REGION (into command line below)
- KEY_PAIR_NAME (into command line below)
- SECURITY_GROUP_NAME (into command line below)
- SECURITY_GROUP_ID (into command line below)

Running the code:
node ec2_createsecuritygroup.js REGION KEY_PAIR_NAME SECURITY_GROUP_NAME SECURITY_GROUP_ID
 */
// snippet-start:[ec2.JavaScript.v3.SecurityGroups.createSecurityGroup]
// Import required AWS SDK clients and commands for Node.js
const {EC2, DescribeVpcsCommand, CreateSecurityGroupCommand,
    AuthorizeSecurityGroupIngressCommand} = require("@aws-sdk/client-ec2");
// Set the AWS region
const region = process.argv[2];
// Create EC2 service object
const ec2client = new EC2(region);
// Set the parameters
const params = {KeyName: process.argv[3]};

async function run(){
    try {
        var data = await ec2client.send(new DescribeVpcsCommand(params));
        vpc = data.Vpcs[0].VpcId;
        var paramsSecurityGroup = {
            Description: 'DESCRIPTION',
            GroupName: process.argv[4],
            VpcId: vpc
        }
    }
    catch (err) {
        console.log("Error", err);
    }
    try {
        var data = await ec2client.send(new CreateSecurityGroupCommand(params));
        const SecurityGroupId = data.GroupId;
        console.log("Success", SecurityGroupId);
        var paramsIngress = {
            GroupId: process.argv[5],
            IpPermissions:[
                {
                    IpProtocol: "tcp",
                    FromPort: 80,
                    ToPort: 80,
                    IpRanges: [{"CidrIp":"0.0.0.0/0"}]
                },
                {
                    IpProtocol: "tcp",
                    FromPort: 22,
                    ToPort: 22,
                    IpRanges: [{"CidrIp":"0.0.0.0/0"}]
                }
            ]
        };
    } catch (err) {
        console.log("Error", err);
    }
    try{
        var data = await ec2client.send(new AuthorizeSecurityGroupIngressCommand(paramsIngress));
        console.log("Ingress Successfully Set", data);
    }
    catch (err) {
        console.log("Cannot retrieve a VPC", err);
    }
}
run();
// snippet-end:[ec2.JavaScript.v3.SecurityGroups.createSecurityGroup]
exports.run = run; //for unit tests only
