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

// Create the IAM service object
var iam = new AWS.IAM({apiVersion: '2010-05-08'});

var paramsRoleList = {
  RoleName: process.argv[2]
};

var policyName = 'AmazonDynamoDBFullAccess';
var policyArn = 'arn:aws:iam::aws:policy/AmazonDynamoDBFullAccess';

iam.listAttachedRolePolicies(paramsRoleList).eachPage(function(err, data, done) {
  if (err) {
    throw err;
  }
  var foundPolicy = false;
  if (data && data.AttachedPolicies) {
    data.AttachedPolicies.forEach(function(rolePolicy) {
      if (rolePolicy.PolicyName !== policyName) {
        return;
      }
      foundPolicy = true;
      var params = {
        PolicyArn: policyArn,
        RoleName: process.argv[2]
      };
      iam.detachRolePolicy(params, function(err, data) {
        if (err) {
          console.error('Unable to detach policy from role.');
          throw err;
        } else {
          console.log('Policy detached from role successfully.');
          process.exit();
        }
      });
    });
    if (!foundPolicy) {
      done();
    }
  } else {
    console.log('Policy was not attached to the role.');
  }
});
