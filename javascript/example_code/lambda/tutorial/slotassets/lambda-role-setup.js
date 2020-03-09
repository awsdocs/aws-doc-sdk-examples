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

const ROLE = "ROLE";

var myPolicy = {
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
};

var createParams = {
 AssumeRolePolicyDocument: JSON.stringify(myPolicy),
 RoleName: ROLE
};

var lambdaPolicyParams = {
 PolicyArn: "arn:aws:iam::aws:policy/service-role/AWSLambdaRole",
 RoleName: ROLE
};

var dynamoPolicyParams = {
 PolicyArn: "arn:aws:iam::aws:policy/AmazonDynamoDBReadOnlyAccess",
 RoleName: ROLE
};

iam.createRole(createParams, function(err, data) {
  if (err) {
    console.log(err, err.stack); // an error occurred
  } else {
    console.log("Role ARN is", data.Role.Arn);           // successful response
    iam.attachRolePolicy(lambdaPolicyParams, function(err, data) {
      if (err) {
        console.log(err, err.stack); // an error occurred
      } else{
        console.log("AWSLambdaRole policy attached");           // successful response
        iam.attachRolePolicy(dynamoPolicyParams, function(err, data) {
          if (err) {
            console.log(err, err.stack); // an error occurred
          } else{
            console.log("DynamoDB read-only policy attached");           // successful response
          }
        });
      }
    });
    }
});
