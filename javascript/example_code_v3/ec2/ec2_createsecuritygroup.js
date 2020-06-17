/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/ec2-example-security-groups.html

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
async function run(){
    const {EC2, DescribeVpcsCommand, CreateSecurityGroupCommand, AuthorizeSecurityGroupIngressCommand} = require("@aws-sdk/client-ec2");
    const region = process.argv[2];
    const ec2client = await new EC2(region);
    const params = {KeyName: process.argv[3]};
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
// snippet-start:[ec2.JavaScript.SecurityGroups.createSecurityGroup]

var vpc = null;

// Retrieve the ID of a VPC
ec2.describeVpcs(function(err, data) {
   if (err) {
     console.log("Cannot retrieve a VPC", err);
   } else {
     vpc = data.Vpcs[0].VpcId;
     var paramsSecurityGroup = {
        Description: 'DESCRIPTION',
        GroupName: 'SECURITY_GROUP_NAME',
        VpcId: vpc
     };
     // Create the instance
     ec2.createSecurityGroup(paramsSecurityGroup, function(err, data) {
        if (err) {
           console.log("Error", err);
        } else {
           var SecurityGroupId = data.GroupId;
           console.log("Success", SecurityGroupId);
           var paramsIngress = {
             GroupId: 'SECURITY_GROUP_ID',
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
           ec2.authorizeSecurityGroupIngress(paramsIngress, function(err, data) {
             if (err) {
               console.log("Error", err);
             } else {
               console.log("Ingress Successfully Set", data);
             }
          });
        }
     });
   }
});
// snippet-end:[ec2.JavaScript.SecurityGroups.createSecurityGroup]
exports.run = run;
