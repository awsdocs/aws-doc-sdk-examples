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

var paramsAllocateAddress = {
   Domain: 'vpc'
};

// Allocate the Elastic IP address
ec2.allocateAddress(paramsAllocateAddress, function(err, data) {
   if (err) {
      console.log("Address Not Allocated", err);
   } else {
      console.log("Address allocated:", data.AllocationId);
      var paramsAssociateAddress = {
        AllocationId: data.AllocationId,
        InstanceId: 'INSTANCE_ID'
      };
      // Associate the new Elastic IP address with an EC2 instance
      ec2.associateAddress(paramsAssociateAddress, function(err, data) {
        if (err) {
          console.log("Address Not Associated", err);
        } else {
          console.log("Address associated:", data.AssociationId);
        }
      });
   }
});
