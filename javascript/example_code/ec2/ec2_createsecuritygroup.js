/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'REGION'});

// Create EC2 service object
var ec2 = new AWS.EC2({apiVersion: '2016-11-15'});

// Variable to hold a ID of a VPC
var vpc = null;

// Retrieve the ID of a VPC
ec2.describeVpcs(function(err, data) {
   if (err) {
     console.log("Cannot retrieve a VPC", err);
   } else {
     vpc = data.Vpcs[0].VpcId;
   }
});

var paramsSecurityGroup = {
   Description: 'Node.js SDK Example',
   GroupName: 'sdk-example',
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
        GroupName: 'sdk-example',
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
