 
//snippet-sourcedescription:[ec2_allocateaddress.js demonstrates how to allocate and associate an Elastic IP address to an Amazon EC2 instance.]
//snippet-keyword:[JavaScript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EC2]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[daviddeyo]


// Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// Licensed under the Apache-2.0 License on an "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND.   

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide//ec2-example-elastic-ip-addresses.html
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
