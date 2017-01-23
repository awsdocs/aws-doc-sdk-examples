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
// Load credentials and set region from JSON file
AWS.config.loadFromPath('./config.json');

// Create EC2 service object
var ec2 = new AWS.EC2({apiVersion: '2016-11-15'});

var params = {
  InstanceIds: ['INSTANCE_ID'],
  DryRun: true
};

// call EC2 to retrieve policy for selected bucket
ec2.rebootInstances(params, function(err, data) {
  if (err && err.code === 'DryRunOperation') {
    params.DryRun = false;
    ec2.rebootInstances(params, function(err, data) {
        if (err) {
          console.log("Error", err);
        } else if (data) {
          console.log("Success", data);
        }
    });
  } else {
    console.log("You don't have permission to change instance monitoring.");
  }
});
